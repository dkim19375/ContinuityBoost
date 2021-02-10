package me.dkim19375.continuityboost.plugin.util;

import me.dkim19375.continuityboost.api.BoostType;
import me.dkim19375.continuityboost.api.Booster;
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
    private final Set<Booster> boosts = new HashSet<>();
    @NotNull
    private final Map<Booster, Long> currentBoosts = new HashMap<>();
    @NotNull
    private final Set<UUID> toggledPlayers = new HashSet<>();

    public BoostManager(final ContinuityBoost plugin) {
        this.plugin = plugin;
    }

    public void runTask() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            final long time = System.currentTimeMillis();
            for (Booster boost : currentBoosts.keySet()) {
                if (currentBoosts.get(boost) == null) {
                    continue;
                }
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

    @NotNull
    public Set<Booster> getBoosts() {
        return boosts;
    }

    public void saveConfigurationFile(final Booster boost) {
        plugin.getBoostsFile().getConfig().set(boost.getUniqueId().toString(), boost);
    }

    private void removeCurrentBoost(UUID uuid) {
        for (Booster b : currentBoosts.keySet()) {
            if (b.getUniqueId().equals(uuid)) {
                currentBoosts.remove(b);
            }
        }
    }

    public void removeBoost(Booster boost) {
        boosts.removeIf(b -> b.getUniqueId().equals(boost.getUniqueId()));
        removeCurrentBoost(boost.getUniqueId());
        plugin.getBoostsFile().getConfig().set(boost.getUniqueId().toString(), null);
        forceSave();
    }

    @Nullable
    public Booster getBoostByUUID(UUID uuid) {
        for (Booster boost : boosts) {
            if (boost.getUniqueId().equals(uuid)) {
                return boost;
            }
        }
        return null;
    }

    @Nullable
    public Booster getBoostByUUID(String uuid) {
        final UUID uuid1;
        try {
            uuid1 = UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            return null;
        }
        for (Booster boost : boosts) {
            if (boost.getUniqueId().equals(uuid1)) {
                return boost;
            }
        }
        return null;
    }

    public void forceStopBoost(final Booster boost) {
        if (boost.getEffect() != null) {
            Bukkit.getOnlinePlayers().forEach((player) -> {
                if (player.getPotionEffect(boost.getEffect().getType()) != null) {
                    if (Objects.requireNonNull(player.getPotionEffect(boost.getEffect().getType())).equals(boost.getEffect())) {
                        player.removePotionEffect(boost.getEffect().getType());
                    }
                }
            });
        }
        removeCurrentBoost(boost.getUniqueId());
    }

    public void forceStopBoost(final BoostType boostType) {
        for (Booster boost : currentBoosts.keySet()) {
            if (boost.getType() == boostType) {
                forceStopBoost(boost);
            }
        }
    }

    public void addBoost(@NotNull final Booster boost) {
        boolean similar = false;
        for (Booster boost1 : boosts) {
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

    public void startBoost(@NotNull final Booster boost) {
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
                removeCurrentBoost(boost.getUniqueId());
                currentBoosts.put(boost, System.currentTimeMillis());
                break;
            case EXP_MULTIPLIER:
            case ITEM_DROP_MULTIPLIER:
            case VILLAGER:
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(FormattingUtils.formatWithColors(boost.getBoostMessage()));
                }
                removeCurrentBoost(boost.getUniqueId());
                currentBoosts.put(boost, System.currentTimeMillis());
        }
    }

    @NotNull
    public Map<Booster, Long> getCurrentBoosts() {
        return currentBoosts;
    }

    public int getCurrentBoostAmount(final BoostType type) {
        int i = 0;
        for (Booster boost : currentBoosts.keySet()) {
            if (boost.getType() == type) {
                i++;
            }
        }
        return i;
    }

    public Set<Booster> getBoostsPerType(final BoostType type) {
        final Set<Booster> boosts = new HashSet<>();
        for (Booster boost : currentBoosts.keySet()) {
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
