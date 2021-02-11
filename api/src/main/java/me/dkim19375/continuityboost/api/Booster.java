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

    /**
     * @return the duration in <b>seconds</b> that the boost will last for
     */
    int getDuration();

    /**
     * @param duration the duration in <b>seconds</b> that the boost will last for
     */
    void setDuration(int duration);

    /**
     * @return the {@link BoostType} of the boost
     */
    @NotNull
    BoostType getType();

    /**
     * @return the {@link PotionEffect} for boosts of the type {@link BoostType#EFFECT}
     */
    @Nullable
    PotionEffect getEffect();

    /**
     * @param effect the {@link PotionEffect} for boosts of the type {@link BoostType#EFFECT}
     */
    void setEffect(@Nullable PotionEffect effect);

    /**
     * @return the item used to start the boost
     */
    @NotNull
    ItemStack getBoostingItem();

    /**
     * @param boostingItem the item used to start the boost
     */
    void setBoostingItem(@NotNull ItemStack boostingItem);

    /**
     * @return The multiplier or amplifier of the boost, doesn't apply to {@link BoostType#VILLAGER}
     */
    int getMultiplier();

    /**
     * @param multiplier The multiplier or amplifier of the boost, doesn't apply to {@link BoostType#VILLAGER}
     */
    void setMultiplier(int multiplier);

    /**
     * @return the message to be broadcasted when boosted
     */
    @NotNull
    String getBoostMessage();

    /**
     * @param boostMessage the message to broadcast when boosted
     */
    void setBoostMessage(@NotNull String boostMessage);

    /**
     * @return a unique and persistent id for this entity
     */
    @NotNull
    UUID getUniqueId();

    /**
     * @return the blocks that will be affected with {@link BoostType#ITEM_DROP_MULTIPLIER},
     * or <b>null</b> if all blocks will be applied
     */
    @Nullable
    Set<Material> getAppliedBlocks();

    /**
     * @param blocks The blocks that will be affected with {@link BoostType#ITEM_DROP_MULTIPLIER},
     * or <b>null</b> if all blocks will be applied
     */
    void setAppliedBlocks(@Nullable Set<Material> blocks);
}
