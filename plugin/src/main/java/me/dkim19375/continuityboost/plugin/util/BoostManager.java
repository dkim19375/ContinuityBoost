package me.dkim19375.continuityboost.plugin.util;

import me.dkim19375.continuityboost.plugin.ContinuityBoost;
import me.dkim19375.dkim19375core.external.FormattingUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BoostManager {
    private final ContinuityBoost plugin;
    @NotNull
    private final Set<Boost> boosts = new HashSet<>();
    @NotNull
    private final Map<Boost, Long> currentBoosts = new HashMap<>();
    @NotNull
    private final Set<UUID> toggledPlayers = new HashSet<>();

    public BoostManager(final ContinuityBoost plugin) {
        this.plugin = plugin;
    }

    public void runTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            final long time = System.currentTimeMillis();
            for (Boost boost : new HashSet<>(currentBoosts.keySet())) {
                final long difference = (time - currentBoosts.get(boost)) / 1000;
                if (difference > boost.getDuration()) {
                    forceStopBoost(boost);
                }
            }
        }, 1, 1);
    }

    public void forceSave() {
        plugin.getBoostsFile().save();
    }

    public @NotNull Set<Boost> getBoosts() {
        return boosts;
    }

    public void saveConfigurationFile(final Boost boost) {
        plugin.getBoostsFile().getConfig().set(boost.getUniqueId().toString(), boost);
    }

    public void removeBoost(Boost boost) {
        boosts.remove(boost);
        currentBoosts.remove(boost);
        if (plugin.getBoostsFile().getConfig().getSerializable(boost.getUniqueId().toString(), Boost.class) == null) {
            return;
        }
        plugin.getBoostsFile().getConfig().set(boost.getUniqueId().toString(), null);
        forceSave();
    }

    @Nullable
    public Boost getBoostByUUID(UUID uuid) {
        for (Boost boost : boosts) {
            if (boost.getUniqueId().equals(uuid)) {
                return boost;
            }
        }
        return null;
    }

    @Nullable
    public Boost getBoostByUUID(String uuid) {
        final UUID uuid1;
        try {
            uuid1 = UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            return null;
        }
        for (Boost boost : boosts) {
            if (boost.getUniqueId().equals(uuid1)) {
                return boost;
            }
        }
        return null;
    }

    public void forceStopBoost(final Boost boost) {
        if (boost.getEffect() != null) {
            Bukkit.getOnlinePlayers().forEach((player) -> {
                if (player.getPotionEffect(boost.getEffect().getType()) != null) {
                    if (Objects.requireNonNull(player.getPotionEffect(boost.getEffect().getType())).equals(boost.getEffect())) {
                        player.removePotionEffect(boost.getEffect().getType());
                    }
                }
            });
        }
        currentBoosts.remove(boost);
    }

    public void forceStopBoost(final Boost.BoostType boostType) {
        for (Boost boost : currentBoosts.keySet()) {
            if (boost.getType() == boostType) {
                forceStopBoost(boost);
            }
        }
    }

    public void addBoost(@NotNull final Boost boost) {
        boolean similar = false;
        for (Boost boost1 : boosts) {
            if (boost1.equals(boost)) {
                similar = true;
                break;
            }
        }
        if (!similar) {
            boosts.add(boost);
            saveConfigurationFile(boost);
            forceSave();
        }
    }

    public void startBoost(@NotNull final Boost boost) {
        switch (boost.getType()) {
            case EFFECT:
                if (boost.getEffect() == null) {
                    break;
                }
                final PotionEffect effect = boost.getEffect();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.addPotionEffect(effect);
                    p.sendMessage(FormattingUtils.formatWithColors(boost.getBoostMessage()));
                }
                currentBoosts.put(boost, System.currentTimeMillis());
                break;
            case EXP_MULTIPLIER:
            case ITEM_DROP_MULTIPLIER:
            case VILLAGER:
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(FormattingUtils.formatWithColors(boost.getBoostMessage()));
                }
                currentBoosts.put(boost, System.currentTimeMillis());
        }
    }

    @NotNull
    public Map<Boost, Long> getCurrentBoosts() {
        return currentBoosts;
    }

    public int getCurrentBoostAmount(final Boost.BoostType type) {
        int i = 0;
        for (Boost boost : currentBoosts.keySet()) {
            if (boost.getType() == type) {
                i++;
            }
        }
        return i;
    }

    public Set<Boost> getBoostsPerType(final Boost.BoostType type) {
        final Set<Boost> boosts = new HashSet<>();
        for (Boost boost : currentBoosts.keySet()) {
            if (boost.getType() == type) {
                boosts.add(boost);
            }
        }
        return boosts;
    }

    @NotNull
    public Set<UUID> getToggledPlayers() {
        return toggledPlayers;
    }

    public boolean isToggled(UUID uuid) {
        return toggledPlayers.contains(uuid);
    }

    public boolean isToggled(Player player) {
        return isToggled(player.getUniqueId());
    }

    public boolean togglePlayer(UUID uuid) {
        if (toggledPlayers.contains(uuid)) {
            toggledPlayers.remove(uuid);
            return false;
        }
        toggledPlayers.add(uuid);
        return true;
    }

    public void togglePlayerOn(UUID uuid) {
        toggledPlayers.add(uuid);
    }

    public void togglePlayerOff(UUID uuid) {
        toggledPlayers.remove(uuid);
    }
}
