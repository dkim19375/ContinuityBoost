package me.dkim19375.continuityboost.plugin.util;

import me.dkim19375.continuityboost.api.enums.BoostType;
import me.dkim19375.continuityboost.api.Booster;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.EntityType;
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
    @NotNull
    private final String name;
    @Nullable
    private Set<Material> appliedBlocks;
    @Nullable
    private Set<EntityType> appliedEntities;

    @SuppressWarnings("unused")
    public Boost(@NotNull ItemStack boostingItem, int duration, @NotNull BoostType type, @Nullable String boostMessage
            , @Nullable PotionEffect effect, int multiplier, @NotNull String name, @Nullable Set<Material> appliedBlocks, @Nullable Set<EntityType> appliedEntities) {
        this.boostingItem = boostingItem;
        this.duration = duration;
        this.type = type;
        this.boostMessage = boostMessage == null ? "" : boostMessage;
        this.effect = effect;
        this.multiplier = multiplier;
        this.name = name;
        this.appliedBlocks = appliedBlocks;
        this.appliedEntities = appliedEntities;
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
        final String name = (String) map.get("name");
        Set<Material> appliedBlocks = null;
        Set<EntityType> appliedEntities = null;
        try {
            appliedBlocks = ((AppliedHolder) map.get("applied")).getAppliedBlocks();
            appliedEntities = ((AppliedHolder) map.get("applied")).getAppliedEntities();
        } catch (Exception ignored) {}
        return new Boost(boostingItem, duration, type, boostMessage, effect, multiplier, name, appliedBlocks, appliedEntities);
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @SuppressWarnings("unused")
    @Override
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    @NotNull
    public BoostType getType() {
        return type;
    }

    @SuppressWarnings("unused")
    public void setType(@NotNull BoostType type) {
        this.type = type;
    }

    @Override
    @Nullable
    public PotionEffect getEffect() {
        return effect;
    }

    @SuppressWarnings("unused")
    @Override
    public void setEffect(@Nullable PotionEffect effect) {
        this.effect = effect;
    }

    @Override
    @NotNull
    public ItemStack getBoostingItem() {
        return boostingItem;
    }

    @SuppressWarnings("unused")
    @Override
    public void setBoostingItem(@NotNull ItemStack boostingItem) {
        this.boostingItem = boostingItem;
    }

    @Override
    public int getMultiplier() {
        return multiplier;
    }

    @SuppressWarnings("unused")
    @Override
    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    @NotNull
    @Override
    public String getBoostMessage() {
        return boostMessage;
    }

    @Override
    public void setBoostMessage(@NotNull String boostMessage) {
        this.boostMessage = boostMessage;
    }

    @Override
    @NotNull
    public String getName() {
        return name;
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
        map.put("name", name);
        map.put("applied", new AppliedHolder(appliedBlocks, appliedEntities));
        return map;
    }

    @Nullable
    @Override
    public Set<Material> getAppliedBlocks() {
        return appliedBlocks;
    }

    @Override
    public void setAppliedBlocks(@Nullable Set<Material> blocks) {
        appliedBlocks = blocks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Boost boost = (Boost) o;
        return duration == boost.duration && multiplier == boost.multiplier && type == boost.type && Objects.equals(effect, boost.effect) && boostingItem.equals(boost.boostingItem) && boostMessage.equals(boost.boostMessage) && name.equals(boost.name) && Objects.equals(appliedBlocks, boost.appliedBlocks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(duration, type, effect, boostingItem, multiplier, boostMessage, name, appliedBlocks);
    }

    @Nullable
    public Set<EntityType> getAppliedEntities() {
        return appliedEntities;
    }

    public void setAppliedEntities(@Nullable Set<EntityType> appliedEntities) {
        this.appliedEntities = appliedEntities;
    }
}
