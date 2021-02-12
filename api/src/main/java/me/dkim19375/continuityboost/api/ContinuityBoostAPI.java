package me.dkim19375.continuityboost.api;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

@SuppressWarnings("unused")
public interface ContinuityBoostAPI {

    /**
     * @return a <b>copy</b> of all boosts
     */
    @NotNull
    Set<Booster> getBoosts();

    /**
     * @return a <b>copy</b> of all boosts that are currently active
     */
    @NotNull
    Set<Booster> getCurrentBoosts();

    /**
     * @return a <b>copy</b> of all players toggled on the boost
     */
    @NotNull
    Set<UUID> getToggedPlayers();

    /**
     * @param player The player to toggle the boost
     */
    void togglePlayer(@NotNull final UUID player);

    /**
     * @param player The player to toggle the boost on
     */
    void togglePlayerOn(@NotNull final UUID player);

    /**
     * @param player The player to toggle the boost off
     */
    void togglePlayerOff(@NotNull final UUID player);

    /**
     * @param player The player to check if boosted
     */
    boolean isToggled(@NotNull final UUID player);

    /**
     * @param name The booster's name
     * @return The Booster of the UUID
     */
    @Nullable
    Booster getBoosterByUUID(@NotNull final String name);

    /**
     * @param booster The booster to stop
     */
    void stopBoost(@NotNull final Booster booster);

    /**
     * @param booster The booster to start
     */
    void startBoost(@NotNull final Booster booster);

    /**
     * @param type The types of boosts to stop
     */
    void stopBoost(@NotNull final BoostType type);

    /**
     * @param type The type to get the boosts for
     * @return A copy of the boosts for the type
     */
    @NotNull
    Set<Booster> getBoostsPerType(@NotNull final BoostType type);

    /**
     * @param type The {@link BoostType} to get the current boosts for
     * @return A copy of the <b>{@code Set<Booster>}</b> for the current {@link BoostType}
     */
    Set<Booster> getCurrentBoostsPerType(@NotNull final BoostType type);

    /**
     * @param booster The booster to get the start time for
     * @return The time in milliseconds of when the boost started.
     * For example, if the boost started 7.2 seconds ago, it would return {@code 7200}.
     */
    long getBoostStartTime(Booster booster);

    /**
     * @param boostingItem The item used to boost, use {@link Booster#getBoostingItem()} to get the item
     * @param duration The duration of the boost, use {@link Booster#getDuration()} to get the duration
     * @param type The {@link BoostType} of the boost, use {@link Booster#getType()} to get the type
     * @param boostMessage The message that will be broadcasted when boosted,
     *                     use {@link Booster#getBoostMessage()} to get the duration
     * @param effect The potion effect that will be applied on boost if the type is {@link BoostType#EFFECT},
     *               use {@link Booster#getEffect()} to get the effect
     * @param name The name of the boost
     * @param multiplier The multiplier/amplifier of the boost, doesn't apply to {@link BoostType#VILLAGER},
     *                   use {@link Booster#getMultiplier()} to get the multiplier
     * @param appliedBlocks The blocks that will be affected with {@link BoostType#ITEM_DROP_MULTIPLIER},
     *                      use {@link Booster#getAppliedBlocks()} to get the applied blocks
     * @param appliedEntities The entities that will be affected with {@link BoostType#ITEM_DROP_MULTIPLIER},
     *                      use {@link Booster#getAppliedEntities()} to get the applied blocks
     * @return the booster created
     */
    @NotNull
    Booster createBoost(@NotNull ItemStack boostingItem, int duration, @NotNull BoostType type, @Nullable String boostMessage
            , @Nullable PotionEffect effect, int multiplier, @NotNull String name, @Nullable Set<Material> appliedBlocks, @Nullable Set<EntityType> appliedEntities);
}
