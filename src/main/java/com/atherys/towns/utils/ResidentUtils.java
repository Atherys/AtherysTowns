package com.atherys.towns.utils;

import com.atherys.towns.resident.Resident;

import java.util.List;

public final class ResidentUtils {

    public static List<Resident> sortResidentsByDate ( List<Resident> list ) {
        Resident res;
        for ( int i = 0; i < list.size(); i++ ) {
            for ( int j = 0; j < list.size(); j++ ) {
                if ( list.get( i ).getLastOnlineDate().after( list.get( j ).getLastOnlineDate() ) ) {
                    res = list.get( j );
                    list.set( j, list.get( i ) );
                    list.set( i, res );
                }
            }
        }
        return list;
    }

}
