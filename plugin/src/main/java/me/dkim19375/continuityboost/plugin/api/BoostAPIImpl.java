package me.dkim19375.continuityboost.plugin.api;

import me.dkim19375.continuityboost.api.BoostType;

import me.dkim19375.continuityboost.api.Booster;
import me.dkim19375.continuityboost.api.ContinuityBoostAPI;
import me.dkim19375.continuityboost.plugin.ContinuityBoost;
import me.dkim19375.continuityboost.plugin.util.Boost;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BoostAPIImpl implements ContinuityBoostAPI {
    private final ContinuityBoost plugin;

    public BoostAPIImpl(ContinuityBoost plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public Set<Booster> getBoosts() {
        final Set<Boost> boosts = plugin.getBoostManager().getBoosts();
        return new HashSet<>(boosts);
    }

    @Override
    @NotNull
    public Set<Booster> getCurrentBoosts() {
        return null;
    }

    @Override
    @NotNull
    public Set<UUID> getToggedPlayers() {
        return null;
    }

    @Override
    public void togglePlayer(@NotNull final UUID player) {

    }

    @Override
    public void togglePlayerOn(@NotNull final UUID player) {

    }

    @Override
    public void togglePlayerOff(@NotNull final UUID player) {

    }

    @Override
    public boolean isToggled(@NotNull final UUID player) {
        return false;
    }

    @Override
    @Nullable
    public Booster getBoosterByUUID(@NotNull UUID uuid) {
        return null;
    }

    @Override
    public void stopBoost(@NotNull final Booster Boost) {

    }

    @Override
    public void startBoost(@NotNull final Booster Boost) {

    }

    @Override
    public void stopBoost(@NotNull final BoostType type) {

    }

    @Override
    public void addBoost(@NotNull final Booster Boost) {

    }

    @Override
    @NotNull
    public Set<Booster> getBoostsPerType(@NotNull final BoostType type) {
        return null;
    }
}
