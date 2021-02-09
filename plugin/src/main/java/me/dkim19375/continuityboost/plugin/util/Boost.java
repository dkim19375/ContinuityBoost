package me.dkim19375.continuityboost.plugin.util;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Boost implements Cloneable, ConfigurationSerializable {
    private int duration;
    @NotNull
    private BoostType type;
    @Nullable
    private PotionEffect effect;
    @NotNull
    private ItemStack boostingItem;
    private int multiplier;
    @NotNull
    private String boostMessage;
    private final UUID uuid;

    @SuppressWarnings("unused")
    public Boost(@NotNull ItemStack boostingItem, int duration, @NotNull BoostType type, @Nullable String boostMessage
            , @Nullable PotionEffect effect, int multiplier, @Nullable UUID uuid) {
        this.boostingItem = boostingItem;
        this.duration = duration;
        this.type = type;
        this.boostMessage = boostMessage == null ? "" : boostMessage;
        this.effect = effect;
        this.multiplier = multiplier;
        this.uuid = uuid == null ? UUID.randomUUID() : uuid;
    }

    @SuppressWarnings("unused")
    public Boost(@NotNull ItemStack boostingItem, int duration, @NotNull BoostType type, @Nullable String boostMessage
            , @Nullable PotionEffect effect, @Nullable UUID uuid) {
        this.boostingItem = boostingItem;
        this.duration = duration;
        this.type = type;
        this.boostMessage = boostMessage == null ? "" : boostMessage;
        this.effect = effect;
        this.uuid = uuid == null ? UUID.randomUUID() : uuid;
    }

    @SuppressWarnings("unused")
    public Boost(@NotNull ItemStack boostingItem, int duration, @NotNull BoostType type, @Nullable String boostMessage
            , @Nullable UUID uuid) {
        this.boostingItem = boostingItem;
        this.duration = duration;
        this.type = type;
        this.boostMessage = boostMessage == null ? "" : boostMessage;
        this.uuid = uuid == null ? UUID.randomUUID() : uuid;
    }

    @SuppressWarnings("unused")
    public static Boost deserialize(Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        final int duration = (int) map.get("duration");
        final BoostType type = (BoostType) map.get("type");
        final PotionEffect effect = (PotionEffect) map.get("effect");
        final ItemStack boostingItem = (ItemStack) map.get("item");
        final int multiplier = (int) map.get("multiplier");
        final String boostMessage = (String) map.get("boost-message");
        final UUID uuid = UUID.fromString((String) map.get("uuid"));
        return new Boost(boostingItem, duration, type, boostMessage, effect, multiplier, uuid);
    }

    public int getDuration() {
        return duration;
    }

    @SuppressWarnings("unused")
    public void setDuration(int duration) {
        this.duration = duration;
    }

    public @NotNull BoostType getType() {
        return type;
    }

    @SuppressWarnings("unused")
    public void setType(@NotNull BoostType type) {
        this.type = type;
    }

    public @Nullable PotionEffect getEffect() {
        return effect;
    }

    @SuppressWarnings("unused")
    public void setEffect(@Nullable PotionEffect effect) {
        this.effect = effect;
    }

    public @NotNull ItemStack getBoostingItem() {
        return boostingItem;
    }

    @SuppressWarnings("unused")
    public void setBoostingItem(@NotNull ItemStack boostingItem) {
        this.boostingItem = boostingItem;
    }

    public int getMultiplier() {
        return multiplier;
    }

    @SuppressWarnings("unused")
    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public String toString() {
        return "Boost{" +
                "duration=" + duration +
                ", type=" + type +
                ", effect=" + effect +
                ", boostingItem=" + boostingItem +
                ", multiplier=" + multiplier +
                '}';
    }

    @NotNull
    public String getBoostMessage() {
        return boostMessage;
    }

    public void setBoostMessage(@NotNull String boostMessage) {
        this.boostMessage = boostMessage;
    }

    public UUID getUniqueId() {
        return uuid;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("duration", duration);
        map.put("type", type);
        map.put("effect", effect);
        map.put("item", boostingItem);
        map.put("multiplier", multiplier);
        map.put("boost-message", boostMessage);
        map.put("uuid", uuid.toString());
        return map;
    }

    @SuppressWarnings("unused")
    public enum BoostType implements ConfigurationSerializable {
        EXP_MULTIPLIER, ITEM_DROP_MULTIPLIER, EFFECT, VILLAGER;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Boost boost = (Boost) o;
        return duration == boost.duration && multiplier == boost.multiplier && type == boost.type && Objects.equals(effect, boost.effect) && boostingItem.equals(boost.boostingItem) && boostMessage.equals(boost.boostMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(duration, type, effect, boostingItem, multiplier, boostMessage, uuid);
    }
}
