package me.dkim19375.continuityboost.plugin.util;

import me.dkim19375.continuityboost.api.BoostType;
import me.dkim19375.continuityboost.api.Booster;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Boost implements Cloneable, ConfigurationSerializable, Booster {
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
    @Nullable
    private final Set<Material> appliedBlocks;

    @SuppressWarnings("unused")
    public Boost(@NotNull ItemStack boostingItem, int duration, @NotNull BoostType type, @Nullable String boostMessage
            , @Nullable PotionEffect effect, int multiplier, @Nullable UUID uuid, @Nullable Set<Material> appliedBlocks) {
        this.boostingItem = boostingItem;
        this.duration = duration;
        this.type = type;
        this.boostMessage = boostMessage == null ? "" : boostMessage;
        this.effect = effect;
        this.multiplier = multiplier;
        this.uuid = uuid == null ? UUID.randomUUID() : uuid;
        this.appliedBlocks = appliedBlocks;
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
        Set<Material> appliedBlocks = null;
        try {
            //noinspection unchecked
            appliedBlocks = (Set<Material>) map.get("applied-blocks");
        } catch (Exception ignored) {}
        return new Boost(boostingItem, duration, type, boostMessage, effect, multiplier, uuid, appliedBlocks);
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

    public @NotNull UUID getUniqueId() {
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

    @Nullable
    public Set<Material> getAppliedBlocks() {
        return appliedBlocks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Boost boost = (Boost) o;
        return duration == boost.duration && multiplier == boost.multiplier && type == boost.type && Objects.equals(effect, boost.effect) && boostingItem.equals(boost.boostingItem) && boostMessage.equals(boost.boostMessage) && uuid.equals(boost.uuid) && Objects.equals(appliedBlocks, boost.appliedBlocks);
    }

    public boolean isSimilar(Boost boost) {
        if (equals(boost)) {
            return true;
        }
        return duration == boost.duration && multiplier == boost.multiplier && type == boost.type && Objects.equals(effect, boost.effect) && boostingItem.equals(boost.boostingItem) && boostMessage.equals(boost.boostMessage) && Objects.equals(appliedBlocks, boost.appliedBlocks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(duration, type, effect, boostingItem, multiplier, boostMessage, uuid, appliedBlocks);
    }
}
