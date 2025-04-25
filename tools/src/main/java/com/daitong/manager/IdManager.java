package com.daitong.manager;

import java.util.UUID;

public class IdManager {

    public static long getId(){
        UUID uuid = UUID.randomUUID();
        long mostSignificantBits = uuid.getMostSignificantBits();
        long leastSignificantBits = uuid.getLeastSignificantBits();
        return (mostSignificantBits ^ leastSignificantBits) & Long.MAX_VALUE;
    }

    public static String getIdString(){
        return String.valueOf(getId());
    }
}
