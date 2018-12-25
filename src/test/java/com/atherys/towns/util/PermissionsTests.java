package com.atherys.towns.util;

import org.junit.Assert;
import org.junit.Test;

import static com.atherys.towns.util.Permissions.*;

public class PermissionsTests {

    @Test
    public void allowedToTest() {
        Permissions permissions = Permissions.of(DAMAGE_ENTITIES | OPEN_CHESTS | PLACE_BLOCKS);

        // should return true because the player is allowed to damage entities
        Assert.assertTrue(permissions.allowedTo(DAMAGE_ENTITIES));

        // should return true because the player is allowed to do all of these, but DAMAGE_PLAYERS
        Assert.assertTrue(permissions.allowedTo(DAMAGE_ENTITIES | OPEN_CHESTS | PLACE_BLOCKS | DAMAGE_PLAYERS));

        // should return false?
        Assert.assertFalse(permissions.allowedTo((DAMAGE_ENTITIES | OPEN_CHESTS | PLACE_BLOCKS) & DAMAGE_PLAYERS));

        // should return false because the player is not allowed to damage players
        Assert.assertFalse(permissions.allowedTo(DAMAGE_PLAYERS));

        // should return false because while the player is allowed to damage entities, they are not allowed to damage players
        Assert.assertFalse(permissions.allowedTo(DAMAGE_ENTITIES & DAMAGE_PLAYERS));
    }

}
