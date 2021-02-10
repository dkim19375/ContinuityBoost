package me.dkim19375.continuityboost.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public interface ContinuityBoostAPI {

    @NotNull
    Set<Booster> getBoosts();

    @NotNull
    Set<Booster> getCurrentBoosts();

    @NotNull
    Set<UUID> getToggedPlayers();

    void togglePlayer(@NotNull final UUID player);

    void togglePlayerOn(@NotNull final UUID player);

    void togglePlayerOff(@NotNull final UUID player);

    boolean isToggled(@NotNull final UUID player);

    @Nullable
    Booster getBoosterByUUID(@NotNull final UUID uuid);

    void stopBoost(@NotNull final Booster booster);

    void startBoost(@NotNull final Booster booster);

    void stopBoost(@NotNull final BoostType type);

    void addBoost(@NotNull final Booster booster);

    @NotNull
    Set<Booster> getBoostsPerType(@NotNull final BoostType type);
}
