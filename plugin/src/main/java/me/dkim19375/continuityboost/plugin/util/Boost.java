package me.dkim19375.continuityboost.plugin.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class Boost implements Cloneable {
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
    public Boost(ConfigurationSection section) {
        duration = section.getInt("duration");
        type = Objects.requireNonNull(BoostType.match(section.getString("type")));
        String effectString = section.getString("effect");
        int effectDuration = section.getInt("duration");
        int effectAmplifier = section.getInt("amplifier") - 1;
        if (effectString != null) {
            final PotionEffectType type = PotionEffectType.getByName(effectString);
            if (type != null) {
                effect = new PotionEffect(type, effectDuration, effectAmplifier);
            }
        }
        boostingItem = Objects.requireNonNull(section.getItemStack("item"));
        multiplier = section.getInt("multiplier");
        final String boostMsg = section.getString("boost-message");
        boostMessage = boostMsg == null ? "The server has been boosted!" : boostMsg;
        final UUID uuid1;
        try {
            uuid1 = UUID.fromString(Objects.requireNonNull(section.getString("uuid")));
        } catch (Exception e) {
            final UUID u = UUID.randomUUID();
            section.set("uuid", u.toString());
            uuid = u;
            return;
        }
        uuid = uuid1;
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

    @SuppressWarnings("unused")
    public enum BoostType {
        EXP_MULTIPLIER, ITEM_DROP_MULTIPLIER, EFFECT, VILLAGER;

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
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Boost boost = (Boost) o;
        return duration == boost.duration && multiplier == boost.multiplier && type == boost.type && Objects.equals(effect, boost.effect) && boostingItem.equals(boost.boostingItem) && boostMessage.equals(boost.boostMessage);
    }
}
