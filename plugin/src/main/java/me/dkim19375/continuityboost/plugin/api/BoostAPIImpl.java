package me.dkim19375.continuityboost.plugin.api;

import me.dkim19375.continuityboost.api.BoostType;

import me.dkim19375.continuityboost.api.Booster;
import me.dkim19375.continuityboost.api.ContinuityBoostAPI;
import me.dkim19375.continuityboost.plugin.ContinuityBoost;
import me.dkim19375.continuityboost.plugin.util.Boost;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
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
        final Set<Boost> boosts = plugin.getBoostManager().getCurrentBoosts().keySet();
        return new HashSet<>(boosts);
    }

    @Override
    @NotNull
    public Set<UUID> getToggedPlayers() {
        return new HashSet<>(plugin.getBoostManager().getToggledPlayers());
    }

    @Override
    public void togglePlayer(@NotNull final UUID player) {
        plugin.getBoostManager().togglePlayer(player);
    }

    @Override
    public void togglePlayerOn(@NotNull final UUID player) {
        plugin.getBoostManager().togglePlayerOn(player);
    }

    @Override
    public void togglePlayerOff(@NotNull final UUID player) {
        plugin.getBoostManager().togglePlayerOff(player);
    }

    @Override
    public boolean isToggled(@NotNull final UUID player) {
        return plugin.getBoostManager().isToggled(player);
    }

    @Override
    @Nullable
    public Booster getBoosterByUUID(@NotNull String name) {
        return plugin.getBoostManager().getBoostByName(name);
    }

    @Override
    public void stopBoost(@NotNull final Booster boost) {
        final Boost boost1 = plugin.getBoostManager().getBoostByName(boost.getName());
        if (boost1 != null) {
            plugin.getBoostManager().forceStopBoost(boost1);
        }
    }

    @Override
    public void startBoost(@NotNull final Booster boost, @Nullable final Player boostStarter) {
        final Boost boost1 = plugin.getBoostManager().getBoostByName(boost.getName());
        if (boost1 != null) {
            plugin.getBoostManager().startBoost(boost1, boostStarter);
        }
    }

    @Override
    public void stopBoost(@NotNull final BoostType type) {
        plugin.getBoostManager().forceStopBoost(type);
    }

    @Override
    public @NotNull Set<Booster> getBoostsPerType(@NotNull BoostType type) {
        Set<Booster> boosts = new HashSet<>();
        for (Booster boost : new HashSet<>(plugin.getBoostManager().getBoosts())) {
            if (boost.getType() == type) {
                boosts.add(boost);
            }
        }
        return boosts;
    }

    @Override
    @NotNull
    public Set<Booster> getCurrentBoostsPerType(@NotNull final BoostType type) {
        return new HashSet<>(plugin.getBoostManager().getCurrentBoostsPerType(type));
    }

    @Override
    public long getBoostStartTime(Booster booster) {
        final long endTime = plugin.getBoostManager().getCurrentBoosts().get(convert(booster)) + (convert(booster).getDuration() * 1000L);
        return endTime - System.currentTimeMillis();
    }

    @Override
    @NotNull
    public Booster createBoost(@NotNull ItemStack boostingItem, int duration, @NotNull BoostType type, @Nullable String boostMessage
            , @Nullable PotionEffect effect, int multiplier, @NotNull String name, @Nullable Set<Material> appliedBlocks, @Nullable Set<EntityType> appliedEntities) {
        return new Boost(boostingItem, duration, type, boostMessage, effect, multiplier, name, appliedBlocks, appliedEntities);
    }

    private Boost convert(Booster boost) {
        return plugin.getBoostManager().getBoostByName(boost.getName());
    }
}
