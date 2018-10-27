package com.atherys.towns;

import com.atherys.towns.model.Resident;
import com.atherys.towns.model.Town;
import com.atherys.towns.persistence.NationManager;
import com.atherys.towns.persistence.RankManager;
import com.atherys.towns.persistence.TownManager;
import org.mockito.Mockito;
import org.spongepowered.api.world.World;
import org.testng.annotations.Test;

import java.util.UUID;

/**
 * Created by NeumimTo on 27.10.2018.
 * Todo mocks
 */
public class TownManagementTest {

    @Test
    public void testTownManagement() {
        Resident major = new Resident(UUID.randomUUID());

        Resident outsider0 = new Resident(UUID.randomUUID());
        Resident outsider1 = new Resident(UUID.randomUUID());

        RankManager rankManager = new RankManager();
        TownManager townManager = new TownManager();
        NationManager nationManager = new NationManager();



        World worldMock = Mockito.mock(World.class);
        Town town = new Town(major, worldMock);


    }
}
