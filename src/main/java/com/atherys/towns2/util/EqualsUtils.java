package com.atherys.towns2.util;

public final class EqualsUtils {

    public static boolean equalsAny(Object test, Object... others) {
        for ( Object other : others ) if ( test.equals(other) ) return true;
        return false;
    }

    public static boolean equalsNone(Object test, Object... others) {
        return !equalsAny(test, others);
    }

    public static boolean equalsAll(Object test, Object... others) {
        for ( Object other : others ) if ( !test.equals(other) ) return false;
        return true;
    }

}
