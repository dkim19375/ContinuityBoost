package me.dkim19375.continuityboost.plugin.util;

import me.dkim19375.continuityboost.api.BoostType;

import me.dkim19375.continuityboost.plugin.ContinuityBoost;
import me.dkim19375.continuityboost.plugin.commands.CommandHandler;
import me.dkim19375.dkim19375core.external.FormattingUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.logging.Level;

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
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            final long time = System.currentTimeMillis();
            for (Boost boost : new HashSet<>(currentBoosts.keySet())) {
                if (currentBoosts.get(boost) == null) {
                    continue;
                }
                final long difference = (time - currentBoosts.get(boost)) / 1000;
                if (difference > boost.getDuration()) {
                    forceStopBoost(boost);
                }
            }
        }, 1, 1);
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (UUID uuid : toggledPlayers) {
                final Player player = Bukkit.getPlayer(uuid);
                if (player == null) {
                    continue;
                }
                CommandHandler.giveBoostToggled(plugin, player);
            }
        }, 20, 20);
    }

    public void forceSave() {
        plugin.getBoostsFile().save();
    }

    @NotNull
    public Set<Boost> getBoosts() {
        return boosts;
    }

    public void saveConfigurationFile(final Boost boost) {
        plugin.getBoostsFile().getConfig().set(boost.getName(), boost);
    }

    private void removeCurrentBoost(String name) {
        for (Boost b : new HashSet<>(currentBoosts.keySet())) {
            if (b.getName().equalsIgnoreCase(name)) {
                currentBoosts.remove(b);
            }
        }
    }

    public void removeBoost(Boost boost) {
        boosts.removeIf(b -> b.getName().equals(boost.getName()));
        removeCurrentBoost(boost.getName());
        plugin.getBoostsFile().getConfig().set(boost.getName(), null);
        forceSave();
    }

    @Nullable
    public Boost getBoostByName(String name) {
        for (Boost boost : boosts) {
            if (boost.getName().equalsIgnoreCase(name)) {
                return boost;
            }
        }
        return null;
    }

    public void forceStopBoost(final Boost boost) {
        if (boost.getEffect() != null) {
            toggledPlayers.forEach((uuid) -> {
                final Player player = Bukkit.getPlayer(uuid);
                if (player == null) {
                    return;
                }
                final PotionEffect effect = player.getPotionEffect(boost.getEffect().getType());
                if (effect == null) {
                    return;
                }
                if (effect.getType() == boost.getEffect().getType()) {
                    player.removePotionEffect(boost.getEffect().getType());
                }
            });
        }
        removeCurrentBoost(boost.getName());
    }

    public void forceStopBoost(final BoostType boostType) {
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

    public void startBoost(@NotNull final Boost boost, @Nullable final Player player, final boolean showMessage,
                           final String message) {
        switch (boost.getType()) {
            case EFFECT:
                if (boost.getEffect() == null) {
                    break;
                }
                final PotionEffect effect = boost.getEffect();
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.addPotionEffect(effect);
                    sendBoostMessage(boost, player, showMessage, message, p);
                }
                broadcastBoostMessage(boost, player, showMessage, message);
                removeCurrentBoost(boost.getName());
                currentBoosts.put(boost, System.currentTimeMillis());
                break;
            case EXP_MULTIPLIER:
            case ITEM_DROP_MULTIPLIER:
            case ENTITY_DROP_MULTIPLIER:
            case VILLAGER:
                for (Player p : Bukkit.getOnlinePlayers()) {
                    sendBoostMessage(boost, player, showMessage, message, p);
                }
                broadcastBoostMessage(boost, player, showMessage, message);
                removeCurrentBoost(boost.getName());
                currentBoosts.put(boost, System.currentTimeMillis());
                break;
            default:
                throw new IllegalArgumentException("The booster type");
        }
    }

    private void sendBoostMessage(@NotNull Boost boost, @Nullable Player player, boolean showMessage, String message, Player p) {
        if (showMessage) {
            p.sendMessage(formatBoostMsg(player, (player == null ? ""
                    : "&a&l" + player.getDisplayName() + " used &r") + (message == null ? boost.getBoostMessage() : message)));
        }
    }

    private void broadcastBoostMessage(@NotNull Boost boost, @Nullable Player player, boolean showMessage, String message) {
        if (showMessage) {
            Bukkit.getLogger().log(Level.INFO, formatBoostMsg(player, (player == null ? ""
                    : "&a&l" + player.getDisplayName() + " used &r") + (message == null ? boost.getBoostMessage() : message)));
        }
    }

    private String formatBoostMsg(@Nullable final Player player, @NotNull String s) {
        if (player == null) {
            return FormattingUtils.formatWithColors(s);
        }
        return FormattingUtils.formatWithColors(s);
    }

    public void startBoost(@NotNull final Boost boost, @Nullable final Player player) {
        startBoost(boost, player, true, boost.getBoostMessage());
    }

    public void startBoost(@NotNull final Boost boost, @Nullable final Player player, boolean showMessage) {
        startBoost(boost, player, showMessage, boost.getBoostMessage());
    }

    @NotNull
    public Map<Boost, Long> getCurrentBoosts() {
        return currentBoosts;
    }

    public int getCurrentBoostAmount(final BoostType type) {
        int i = 0;
        for (Boost boost : currentBoosts.keySet()) {
            if (boost.getType() == type) {
                i++;
            }
        }
        return i;
    }

    public Set<Boost> getCurrentBoostsPerType(final BoostType type) {
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
