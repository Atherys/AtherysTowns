package com.atherys.towns.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TownsValues {

    private static final Map<UUID, Map<TownsKey, Object>> values = new HashMap<>();

    public static void set(UUID uuid, TownsKey key, Object value) {
        Map<TownsKey, Object> vals;
        if (!values.containsKey(uuid)) {
            vals = new HashMap<>();
            values.put(uuid, vals);
        } else {
            vals = values.get(uuid);
        }
        vals.put(key, value);
    }

    public static Optional<Object> get(UUID uuid, TownsKey key) {
        if (!values.containsKey(uuid)) {
            return Optional.empty();
        }
        if (!values.get(uuid).containsKey(key)) {
            return Optional.empty();
        }
        return Optional.of(values.get(uuid).get(key));
    }

    public static void remove(UUID uuid, TownsKey key) {
        if (values.containsKey(uuid)) {
            Map<TownsKey, Object> playerMap = values.get(uuid);
            playerMap.remove(key);
        }
    }

    public enum TownsKey {
        PLOT_SELECTOR_1ST,
        TOWN_BORDERS,
        PLOT_SELECTOR_2ND
    }

}
