package com.atherys.towns.api.permission;

public class Permissions {

//    public static final int RESERVED_31 =      0b10000000000000000000000000000000;
//    public static final int RESERVED_30 =      0b01000000000000000000000000000000;
//    public static final int RESERVED_29 =      0b00100000000000000000000000000000;
//    public static final int RESERVED_28 =      0b00010000000000000000000000000000;
//    public static final int RESERVED_27 =      0b00001000000000000000000000000000;
//    public static final int RESERVED_26 =      0b00000100000000000000000000000000;
//    public static final int RESERVED_25 =      0b00000010000000000000000000000000;
//    public static final int RESERVED_24 =      0b00000001000000000000000000000000;
//    public static final int RESERVED_23 =      0b00000000100000000000000000000000;
//    public static final int RESERVED_22 =      0b00000000010000000000000000000000;
//    public static final int RESERVED_21 =      0b00000000001000000000000000000000;
//    public static final int RESERVED_20 =      0b00000000000100000000000000000000;
//    public static final int RESERVED_19 =      0b00000000000010000000000000000000;
//    public static final int RESERVED_18 =      0b00000000000001000000000000000000;
//    public static final int RESERVED_17 =      0b00000000000000100000000000000000;
//    public static final int RESERVED_16 =      0b00000000000000010000000000000000;
//    public static final int RESERVED_15 =      0b00000000000000001000000000000000;
//    public static final int RESERVED_14 =      0b00000000000000000100000000000000;
//    public static final int RESERVED_13 =      0b00000000000000000010000000000000;
//    public static final int RESERVED_12 =      0b00000000000000000001000000000000;
//    public static final int RESERVED_11 =      0b00000000000000000000100000000000;
//    public static final int RESERVED_10 =      0b00000000000000000000010000000000;
//    public static final int RESERVED_9 =       0b00000000000000000000001000000000;
//    public static final int RESERVED_8 =       0b00000000000000000000000100000000;
//    public static final int RESERVED_7 =       0b00000000000000000000000010000000;

    public static final int OPEN_CHESTS =       0b00000000000000000000000001000000;
    public static final int SWITCH_REDSTONE =   0b00000000000000000000000000100000;
    public static final int OPEN_DOORS =        0b00000000000000000000000000010000;
    public static final int DAMAGE_PLAYERS =    0b00000000000000000000000000001000;
    public static final int DAMAGE_ENTITIES =   0b00000000000000000000000000000100;
    public static final int PLACE_BLOCKS =      0b00000000000000000000000000000010;
    public static final int DESTROY_BLOCKS =    0b00000000000000000000000000000001;

    public static final int NONE =              0b00000000000000000000000000000000;

    private int permissions;

    private Permissions(int permissions) {
        this.permissions = permissions;
    }

    public static Permissions of (int permissions) {
        return new Permissions(permissions);
    }

    public static int toInteger(Permissions permissions) {
        return permissions.permissions;
    }

    public boolean check(int input) {
        return (permissions & input) == input;
    }

    public void flip(int permissions) {
        this.permissions ^= permissions;
    }
}
