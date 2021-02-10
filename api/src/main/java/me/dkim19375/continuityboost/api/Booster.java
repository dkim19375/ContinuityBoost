package me.dkim19375.continuityboost.api;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

@SuppressWarnings("unused")
public interface Booster {

    int getDuration();

    void setDuration(int duration);

    @NotNull
    BoostType getType();

    @Nullable
    PotionEffect getEffect();

    void setEffect(@Nullable PotionEffect effect);

    @NotNull
    ItemStack getBoostingItem();

    void setBoostingItem(@NotNull ItemStack boostingItem);

    int getMultiplier();

    void setMultiplier(int multiplier);

    @NotNull
    String getBoostMessage();

    void setBoostMessage(@NotNull String boostMessage);

    @NotNull
    UUID getUniqueId();

    @Nullable
    Set<Material> getAppliedBlocks();
}
