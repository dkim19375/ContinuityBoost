package me.dkim19375.continuityboost.plugin;

import me.dkim19375.continuityboost.api.BoostType;

import me.dkim19375.continuityboost.plugin.commands.CommandHandler;
import me.dkim19375.continuityboost.plugin.commands.TabCompletionHandler;
import me.dkim19375.continuityboost.plugin.listeners.BlockBreakListener;
import me.dkim19375.continuityboost.plugin.listeners.InventoryClickListener;
import me.dkim19375.continuityboost.plugin.listeners.PlayerExpChangeListener;
import me.dkim19375.continuityboost.plugin.listeners.PlayerInteractListener;
import me.dkim19375.continuityboost.plugin.listeners.PlayerJoinListener;
import me.dkim19375.continuityboost.plugin.util.Boost;
import me.dkim19375.continuityboost.plugin.util.BoostManager;
import me.dkim19375.continuityboost.plugin.util.LoggingUtils;
import me.dkim19375.dkim19375core.ConfigFile;
import me.dkim19375.dkim19375core.CoreJavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class ContinuityBoost extends CoreJavaPlugin {
    private final BoostManager boostManager = new BoostManager(this);
    private ConfigFile boostsFile;

    @Override
    public void onEnable() {
        register();
        saveConfigs();
    }

    @Override
    public void onDisable() { LoggingUtils.logInfo("Disabled ContinuityBoost!"); }

    public void saveConfigs() {
        saveDefaultConfig();
        boostsFile.createConfig();
        reloadConfig();
    }

    @Override
    public void reloadConfig() {
        saveDefaultConfig();
        super.reloadConfig();
        boostsFile.createConfig();
        boostsFile.reload();
        for (String key : new HashSet<>(boostsFile.getConfig().getKeys(false))) {
            final Boost boost = boostsFile.getConfig().getSerializable(key, Boost.class);
            if (boost != null) {
                try {
                    boostManager.addBoost(boost);
                } catch (NullPointerException ignored) {
                    boostManager.removeBoost(boost);
                }
                continue;
            }
            boostsFile.getConfig().set(key, null);
            boostManager.forceSave();
        }

        for (final Boost boost : new HashSet<>(boostManager.getBoosts())) {
            if (boostsFile.getConfig().getSerializable(boost.getUniqueId().toString(), Boost.class) == null) {
                boostManager.removeBoost(boost);
            }
        }
    }

    public void register() {
        final PluginCommand command = getCommand("continuityboost");
        if (command != null) {
            command.setExecutor(new CommandHandler(this));
        } else {
            LoggingUtils.logInfo(Level.SEVERE, "Cannot get command! Please contact dkim19375");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        boostManager.runTask();
        ConfigurationSerialization.registerClass(Boost.class);
        ConfigurationSerialization.registerClass(BoostType.class);
        boostsFile = new ConfigFile(this, "boosts.yml");
        command.setTabCompleter(new TabCompletionHandler(this));
        getServer().getPluginManager().registerEvents(new PlayerExpChangeListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            final Set<String> materials = new HashSet<>();
            for (Material material : Material.values()) {
                materials.add(material.name());
            }
            TabCompletionHandler.setMaterials(materials);
        });
    }

    public BoostManager getBoostManager() { return boostManager; }

    public ConfigFile getBoostsFile() { return boostsFile; }
}