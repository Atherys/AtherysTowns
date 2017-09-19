package com.atherys.towns.listeners;

import com.atherys.towns.Settings;
import com.atherys.towns.commands.TownsValues;
import com.atherys.towns.commands.plot.PlotToolCommand;
import com.atherys.towns.managers.PlotManager;
import com.atherys.towns.managers.ResidentManager;
import com.atherys.towns.messaging.TownMessage;
import com.atherys.towns.plot.Plot;
import com.atherys.towns.plot.PlotFlags;
import com.atherys.towns.resident.Resident;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class PlayerListeners {

    @Listener
    public void onPlayerJoin (ClientConnectionEvent.Join event) {
        if ( !ResidentManager.getInstance().has(event.getTargetEntity().getUniqueId()) ) {
            Resident.create(event.getTargetEntity());
        }
    }

    @Listener
    public void onPlayerLeave (ClientConnectionEvent.Disconnect event ) {
        ResidentManager.getInstance().get(event.getTargetEntity().getUniqueId()).ifPresent(resident -> {
            resident.updateLastOnline();
            ResidentManager.getInstance().saveOne(resident);
        });
    }

    @Listener
    @IsCancelled(Tristate.FALSE)
    public void onPlayerMove (MoveEntityEvent event, @Root Player player) {
        if ( event.getToTransform().getLocation().getBlockPosition().equals(event.getFromTransform().getLocation().getBlockPosition() ) ) return;

        Optional<Plot> plotFrom = PlotManager.getInstance().getByLocation(event.getFromTransform().getLocation());
        Optional<Plot> plotTo = PlotManager.getInstance().getByLocation(event.getToTransform().getLocation());

        if ( !plotFrom.isPresent() && plotTo.isPresent() ) {
            // If player is going into a town
            TownMessage.enterTown(player, plotTo.get().getParent().get());
        } else if ( plotFrom.isPresent() && !plotTo.isPresent() ) {
            // If player is coming out of a town
            TownMessage.leaveTown(player, plotFrom.get().getParent().get());
        } else if ( plotFrom.isPresent() && plotTo.isPresent() ) {
            // player is moving between 2 plots
            if ( !plotFrom.get().getName().equals(plotTo.get().getName()) && !plotTo.get().getName().equals("None") ) {
                TownMessage.subtitleAnnounce(player, Text.of( Settings.SECONDARY_COLOR, TextStyles.ITALIC, "~ ", plotTo.get().getName(), " ~" ) );
            }
            if ( !plotFrom.get().getFlags().equals(plotTo.get().getFlags()) ) {
                player.sendMessage(plotTo.get().getFlags().differencesFormatted(plotFrom.get().getFlags()));
            }
        }
    }

    @Listener
    @IsCancelled(Tristate.FALSE)
    public void onPlayerChangeBlocks ( ChangeBlockEvent event, @Root Player player ) {

        PlotFlags.Flag flag;
        String msg;

        if ( event instanceof ChangeBlockEvent.Break ) {
            flag = PlotFlags.Flag.DESTROY;
            msg = "destroy";
        } else if ( event instanceof ChangeBlockEvent.Place ) {
            flag = PlotFlags.Flag.BUILD;
            msg = "build";
        } else return;

        ResidentManager.getInstance().get(player.getUniqueId()).ifPresent( resident -> {
            for (Transaction<BlockSnapshot> trans : event.getTransactions() ) {
                Optional<Location<World>> loc = trans.getOriginal().getLocation();
                if ( loc.isPresent() ) {
                    Optional<Plot> plot = PlotManager.getInstance().getByLocation(loc.get());
                    if (plot.isPresent()) {
                        if (!plot.get().getFlags().isAllowed(resident, flag, plot.get())) {
                            TownMessage.warn(player, "You are not permitted to ", msg, " in ", plot.get().getParent().get().getName());
                            event.setCancelled(true);
                            return;
                        }
                    } else {
                        // FOR WILDERNESS REGEN
                        //BlockSnapshot snap = trans.getOriginal();

                        //if (WildernessManager.isItemRegenerable(snap.getExtendedState().getType()) ) {
                        //    DatabaseManager.saveSnapshot(loc.get(), WildernessManager.getRegenSnapshot( event, snap ), System.currentTimeMillis());
                        //}
                    }
                } else event.setCancelled(true);
            }
        });
    }

    @Listener
    @IsCancelled(Tristate.FALSE)
    public void onPlayerPrimaryBlockInteract (InteractBlockEvent event, @Root Player player ) {

        Optional<Plot> plotFrom = PlotManager.getInstance().getByLocation(event.getTargetBlock().getLocation().orElse(player.getLocation()));
        if (plotFrom.isPresent()) {
            Optional<Resident> resOpt = ResidentManager.getInstance().get(player.getUniqueId());
            if (resOpt.isPresent() && !plotFrom.get().isResidentAllowedTo(resOpt.get(), PlotFlags.Flag.SWITCH) && Settings.SWITCH_FLAG_BLOCKS.contains(event.getTargetBlock().getExtendedState().getType().getName()) ) {
                TownMessage.warn(player, "You are not allowed to switch in this town.");
                event.setCancelled(true);
                return;
            }
        }

        TownsValues.TownsKey key;
        String locStr;

        if ( event instanceof InteractBlockEvent.Primary.MainHand ) {
            key = TownsValues.TownsKey.PLOT_SELECTOR_1ST;
            locStr = "1st";
        } else if ( event instanceof InteractBlockEvent.Secondary.MainHand ) {
            key = TownsValues.TownsKey.PLOT_SELECTOR_2ND;
            locStr = "2nd";
        } else return;

        Optional<ItemStack> itemInHand = player.getItemInHand( HandTypes.MAIN_HAND );
        if ( itemInHand.isPresent() ) {
            if ( PlotToolCommand.plotSelector().equalTo( itemInHand.get() ) ) {
                Optional<Location<World>> locOpt = event.getTargetBlock().getLocation();
                if ( locOpt.isPresent() ) {
                    Location loc = locOpt.get().setPosition(event.getTargetBlock().getPosition().toDouble().add(0.5, 0.5, 0.5));
                    TownsValues.set(player.getUniqueId(), key, loc);
                    player.sendMessage(ChatTypes.CHAT, Text.of(TownMessage.MSG_PREFIX, "Set " + locStr + " Position for plot definition. " + loc.getBlockPosition().toString() ));
                    event.setCancelled(true);
                }
            }
        }

    }

    @Listener
    public void onPlayerDamage (DamageEntityEvent event) {
        if ( event.getCause().root() instanceof EntityDamageSource ) {
            if (((EntityDamageSource) event.getCause().root()).getSource() instanceof Player) {
                Player player = (Player) ((EntityDamageSource) event.getCause().root()).getSource();

                String action;
                PlotFlags.Flag flag;

                if (!(event.getTargetEntity() instanceof Player)) {
                    action = "Damage Entities";
                    flag = PlotFlags.Flag.DAMAGE_ENTITY;
                } else {
                    action = "PVP";
                    flag = PlotFlags.Flag.PVP;
                }

                PlotManager.getInstance().getByLocation(player.getLocation()).ifPresent( plot -> ResidentManager.getInstance().get(player.getUniqueId()).ifPresent(resident -> {
                    if ( !plot.getFlags().isAllowed(resident, flag, plot) ) {
                        TownMessage.warn(player, Text.of("You are not permitted to ", action, " in ", plot.getParent().get().getName()));
                        event.setCancelled(true);
                    }
                }));
            }
        }
    }
    /*

    @Listener
    public void onPlayerChat (MessageEvent event, @Root Player player) {
        if (TownsValues.get(player.getUniqueId(), TownsValues.TownsKey.TOWN_CHAT).isPresent()) {
            Optional<Resident> resident = ResidentManager.getInstance().get(player.getUniqueId());
            if (!resident.isPresent()) return;
            if ( resident.get().town().isPresent() ) {
                // TODO: Fix this
                // Currently displays "<PlayerName> Message"
                // Should be displaying only "Message"
                resident.get().town().get().sendMessage( resident.get(), event.getMessage() );
            }
            event.setMessageCancelled(true);
        }
    }*/

}
