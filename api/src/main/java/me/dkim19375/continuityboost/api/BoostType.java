package me.dkim19375.continuityboost.api;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum BoostType implements ConfigurationSerializable {
    EXP_MULTIPLIER, ITEM_DROP_MULTIPLIER, EFFECT, VILLAGER, ENTITY_DROP_MULTIPLIER;

    static {
        ConfigurationSerialization.registerClass(BoostType.class);
    }

    @Nullable
    public static BoostType match(final String s) {
        if (s == null) {
            return null;
        }
        switch (s.toUpperCase(Locale.ENGLISH)) {
            case "EXP_MULTIPLIER":
                return EXP_MULTIPLIER;
            case "ITEM_DROP_MULTIPLIER":
                return ITEM_DROP_MULTIPLIER;
            case "EFFECT":
                return EFFECT;
            case "VILLAGER":
                return VILLAGER;
            case "ENTITY_DROP_MULTIPLIER":
                return ENTITY_DROP_MULTIPLIER;
            default:
                return null;
        }
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", name());
        return map;
    }

    public static BoostType deserialize(@NotNull Map<String, Object> map) {
        return BoostType.match((String) map.get("type"));
    }
}
