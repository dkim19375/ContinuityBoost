package me.dkim19375.continuityboost.plugin.util;

import me.dkim19375.continuityboost.plugin.ContinuityBoost;
import me.dkim19375.dkim19375core.external.FormattingUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class BoostManager {
    private final ContinuityBoost plugin;
    @NotNull
    private final Set<Boost> boosts = new HashSet<>();
    @NotNull
    private final Map<Boost, Long> currentBoosts = new HashMap<>();

    public BoostManager(final ContinuityBoost plugin) {
        this.plugin = plugin;
    }

    public void runTask() {
/*        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            boolean save = false;
            for (Boost boost : boosts) {
                boolean found = false;
                for (String key : new HashSet<>(plugin.getBoostsFile().getConfig().getKeys(false))) {
                    final ConfigurationSection section = plugin.getBoostsFile().getConfig().getConfigurationSection(key);
                    if (section == null) {
                        continue;
                    }
                    if (!isValidConfiguration(section)) {
                        continue;
                    }
                    final Boost b = new Boost(section);
                    if (boost.equals(b)) {
                        found = true;
                    }
                }
                if (found) {
                    continue;
                }
                saveConfigurationFile(boost, plugin.getBoostsFile().getConfig().createSection(UUID.randomUUID().toString()));
                save = true;
            }
            if (save) {
                Bukkit.getScheduler().runTask(plugin, this::forceSave);
            }
        }, 20L, 20L);*/
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

    public void saveConfigurationFile(final Boost boost, final ConfigurationSection section) {
        section.set("duration", boost.getDuration());
        section.set("type", boost.getType().name());
        section.set("multiplier", boost.getMultiplier());
        section.set("item", boost.getBoostingItem());
        section.set("boost-message", boost.getBoostMessage());
        final PotionEffect effect = boost.getEffect();
        if (effect != null) {
            section.set("effect", effect.getType().getName());
        }
    }

    public void removeBoost(Boost boost) {
        boosts.remove(boost);
        currentBoosts.remove(boost);
        if (plugin.getBoostsFile().getConfig().getConfigurationSection(boost.getUniqueId().toString()) == null) {
            throw new IllegalArgumentException("The configuration section " + boost.getUniqueId().toString() + " doesn't exist!");
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

    public boolean isValidConfiguration(ConfigurationSection section) {
        if (section == null) {
            return false;
        }
        if (section.getInt("duration") < 1) {
            return false;
        }
        final Boost.BoostType type = Boost.BoostType.match(section.getString("type"));
        if (type == null) {
            return false;
        }
        if (section.getInt("multiplier") < 1) {
            return false;
        }
        if (section.getItemStack("item") == null) {
            return false;
        }
        if (section.getString("boost-message") == null) {
            section.set("boost-message", "The server has been boosted!");
        }
        if (type != Boost.BoostType.EFFECT) {
            return true;
        }
        final String stringEffect = section.getString("effect");
        if (stringEffect == null) {
            return false;
        }
        final PotionEffectType potionEffectType = PotionEffectType.getByName(stringEffect.toUpperCase());
        return potionEffectType != null;
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
            saveConfigurationFile(boost, plugin.getBoostsFile().getConfig().createSection(boost.getUniqueId().toString()));
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
}
