package com.atherys.towns.plot;

import com.atherys.towns.Settings;
import com.atherys.towns.managers.NationManager;
import com.atherys.towns.nation.Nation;
import com.atherys.towns.resident.Resident;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PlotFlags {

    private interface ExtentChecker<R,F,T,B> {
        B apply(R resident, F flag, T plot);
    }

    public enum Flag {
        PVP             (Extent.ALL, Extent.NONE),
        BUILD           (Extent.ALL, /*Extent.ALLIES, Extent.ENEMIES,*/ Extent.NATION, Extent.TOWN, Extent.NONE),
        DESTROY         (Extent.ALL, /*Extent.ALLIES, Extent.ENEMIES,*/ Extent.NATION, Extent.TOWN, Extent.NONE),
        SWITCH          (Extent.ALL, /*Extent.ALLIES, Extent.ENEMIES,*/ Extent.NATION, Extent.TOWN, Extent.NONE),
        DAMAGE_ENTITY   (Extent.ALL, /*Extent.ALLIES, Extent.ENEMIES,*/ Extent.NATION, Extent.TOWN, Extent.NONE),
        JOIN            (Extent.ALL, Extent.NONE);

        private Extent[] permittedExtents;

        Flag ( Extent... permittedExtents ) {
            this.permittedExtents = permittedExtents;
        }

        public boolean checkExtent ( Extent extent ) {
            for ( Extent e : permittedExtents ) {
                if ( e.equals(extent) ) return true;
            }
            return false;
        }
    }

    public enum Extent {
        NULL("",                (resident, flag, plot) -> true ),
        NONE("%none%",          (resident, flag, plot) -> false ),
        ALL("%all%",            (resident, flag, plot) -> true ),
        TOWN("%town%",          (resident, flag, plot) -> {
            if ( resident.getTown().isPresent() ) {
                if ( resident.getTown().get().equals( plot.getParent().get() ) ) return true;
            }
            return false;
        }),
        NATION("%nation%",      (resident, flag, plot) -> {
            Optional<Nation> resNation = NationManager.getInstance().getByResident(resident);
            Optional<Nation> plotNation = NationManager.getInstance().getByPlot(plot);
            // if resident has no nation, and the plot has a nation flag, then that means the resident's permission is indeterminate. Return false;
            // if plot has no nation, but has a nation flag, that means nobody who is part of a nation should have permission;
            if ( !resNation.isPresent() || !plotNation.isPresent() ) return false;
            // if resident's town and plot's town have same nation
            if ( resident.getTown().isPresent() ) {
                if ( resNation.get().equals(plotNation.get()) ) return true;
            }
            return false;
        })
        //,
        //ALLIES("%allies%",      (resident, flag, plot) -> {
        //    Optional<Nation> resNation = AtherysTowns.getInstance().getNationManager().getByResident(resident);
        //    Optional<Nation> plotNation = AtherysTowns.getInstance().getNationManager().getByPlot(plot);
        //    // if resident's town's nation and plot's town's nation are allied
        //    if ( resNation.isPresent() && plotNation.isPresent() ) {
        //        if ( resNation.get().equals(plotNation.get()) || resNation.get().isAlliedWith(plotNation.get()) ) return true;
        //    }
        //    return false;
        //}),
        //NEUTRALS("%neutrals%",  (resident, flag, plot) -> {
        //    Optional<Nation> resNation = AtherysTowns.getInstance().getNationManager().getByResident(resident);
        //    Optional<Nation> plotNation = AtherysTowns.getInstance().getNationManager().getByPlot(plot);
        //    // if neither nations have any diplomatic relationships ( ie they are neither enemies nor allies )
        //    if ( resNation.isPresent() && plotNation.isPresent() ) {
        //        if ( !resNation.get().isAlliedWith(plotNation.get()) && !resNation.get().isEnemiesWith(plotNation.get()) ) return true;
        //    }
        //    return false;
        //}),
        //ENEMIES("%enemies%",    (resident, flag, plot) -> {
        //    Optional<Nation> resNation = AtherysTowns.getInstance().getNationManager().getByResident(resident);
        //    Optional<Nation> plotNation = AtherysTowns.getInstance().getNationManager().getByPlot(plot);
        //    // if resident's town's nation and plot's town's nation are at war
        //    if ( resNation.isPresent() && plotNation.isPresent() ) {
        //        if ( resNation.get().equals(plotNation.get()) || resNation.get().isEnemiesWith(plotNation.get()) ) return true;
        //    }
        //    return false;
        //})
        ;

        private String name;
        private ExtentChecker<Resident,Flag,Plot,Boolean> checker;
        public static final Extent[] VALUES = values();

        Extent ( String name, ExtentChecker<Resident,Flag,Plot,Boolean> checker ) {
            this.name = name;
            this.checker = checker;
        }

        public String getName() {
            return name;
        }

        public boolean check ( Resident res, Flag flag, Plot plot ) { return checker.apply(res, flag, plot); }

        public static Extent fromName(String name) {
            for (Extent extent : VALUES) {
                if (extent.name.equals(name)) {
                    return extent;
                }
            }
            return NULL;
        }

        public static String list() {
            StringBuilder builder = new StringBuilder();
            builder.append("[ ");
            for ( Extent e : VALUES ) {
                builder.append(e.name).append("; ");
            }
            builder.append(" ]");
            return builder.toString();
        }
    }

    private Map<Flag,Extent> flags;

    private PlotFlags () {
        flags = new HashMap<>();
    }

    public static PlotFlags empty() {
        return new PlotFlags();
    }

    public static PlotFlags create( Flag flag, Extent value ) {
        PlotFlags flags = new PlotFlags();
        flags.set(flag, value);
        return flags;
    }

    public static PlotFlags regular() {
        return   create(    PlotFlags.Flag.BUILD,           PlotFlags.Extent.TOWN   )
                .set(       PlotFlags.Flag.DESTROY,         PlotFlags.Extent.TOWN   )
                .set(       PlotFlags.Flag.PVP,             PlotFlags.Extent.ALL    )
                .set(       PlotFlags.Flag.SWITCH,          PlotFlags.Extent.TOWN   )
                .set(       PlotFlags.Flag.DAMAGE_ENTITY,   PlotFlags.Extent.TOWN   )
                .set(       PlotFlags.Flag.JOIN,             PlotFlags.Extent.NONE  );
    }

    public PlotFlags set ( Flag flag, Extent value ) {
        flags.put(flag, value );
        return this;
    }

    public PlotFlags remove ( Flag flag ) {
        flags.remove(flag);
        return this;
    }

    public boolean isAllowed ( Resident res, Flag flag, Plot plot ) {
        if (!res.getPlayer().isPresent()) return false;
        if (!flags.containsKey(flag)) return true;

        Extent e = flags.get(flag);
        return e.check(res, flag, plot);
    }

    public Extent get(Flag flag) {
        return flags.get(flag);
    }

    public PlotFlags copy() {
        PlotFlags copy = new PlotFlags();
        for ( Map.Entry<Flag,Extent> entry : flags.entrySet() ) {
            copy.set(entry.getKey(), entry.getValue());
        }
        return copy;
    }

    public Text formatted() {
        Text.Builder textBuilder = Text.builder();
        for ( Map.Entry<Flag,Extent> flag : flags.entrySet() ) {
            textBuilder.append( Text.of(Settings.PRIMARY_COLOR, flag.getKey().name(), Settings.DECORATION_COLOR, " ( ", Settings.TEXT_COLOR, flag.getValue().toString(), Settings.DECORATION_COLOR, " ) \n" ) );
        }
        return textBuilder.build();
    }

    public Text formattedSingleLine() {
        Text.Builder textBuilder = Text.builder();
        for ( Map.Entry<Flag,Extent> flag : flags.entrySet() ) {
            textBuilder.append( Text.of(Settings.PRIMARY_COLOR, flag.getKey().name(), Settings.DECORATION_COLOR, " ( ", Settings.TEXT_COLOR, flag.getValue().toString(), Settings.DECORATION_COLOR, " ); " ) );
        }
        return textBuilder.build();
    }

    public Map<Flag,Extent> getAll() { return flags; }

    public boolean equals ( PlotFlags flags ) {
        if (this == flags) {
            return true;
        }
        for ( Map.Entry<Flag,Extent> entry : flags.getAll().entrySet() ) {
            if ( !entry.getValue().equals(this.get(entry.getKey()) ) ) return false;
        }
        return true;
    }

    public Text differencesFormatted(PlotFlags flags) {
        Text.Builder diffBuilder = Text.builder();
        diffBuilder.append( Text.of( Settings.DECORATION_COLOR, ".o0o.-{ " ) );
        for ( Map.Entry<Flag,Extent> entry : flags.getAll().entrySet() ) {
            if ( !entry.getValue().equals( this.flags.get(entry.getKey()) ) ) {
                diffBuilder.append( Text.of( Settings.PRIMARY_COLOR, entry.getKey().name(), " ( ", Settings.WARNING_COLOR, entry.getValue().name(), Settings.PRIMARY_COLOR, " ) ", TextColors.RESET ) );
            }
        }
        diffBuilder.append( Text.of( Settings.DECORATION_COLOR, " }-.o0o." ) );
        return diffBuilder.build();
    }
}
