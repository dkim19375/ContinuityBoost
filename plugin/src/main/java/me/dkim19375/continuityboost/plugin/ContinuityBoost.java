package me.dkim19375.continuityboost.plugin;

import me.dkim19375.continuityboost.plugin.commands.CommandHandler;
import me.dkim19375.continuityboost.plugin.commands.TabCompletionHandler;
import me.dkim19375.continuityboost.plugin.listeners.BlockBreakListener;
import me.dkim19375.continuityboost.plugin.listeners.PlayerExpChangeListener;
import me.dkim19375.continuityboost.plugin.listeners.PlayerInteractListener;
import me.dkim19375.continuityboost.plugin.util.Boost;
import me.dkim19375.continuityboost.plugin.util.BoostManager;
import me.dkim19375.continuityboost.plugin.util.LoggingUtils;
import me.dkim19375.dkim19375core.ConfigFile;
import me.dkim19375.dkim19375core.CoreJavaPlugin;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class ContinuityBoost extends CoreJavaPlugin {
    private final BoostManager boostManager = new BoostManager(this);
    private final ConfigFile boostsFile = new ConfigFile(this, "boosts.yml");

    @Override
    public void onEnable() {
        saveConfigs();
        register();
    }

    @Override
    public void onDisable() {
        LoggingUtils.logInfo("Disabled ContinuityBoost!");
    }

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
        for (String key : boostsFile.getConfig().getKeys(false)) {
            final ConfigurationSection section = boostsFile.getConfig().getConfigurationSection(key);
            if (section == null) {
                continue;
            }
            final Boost boost;
            try {
                boost = new Boost(section);
            } catch (Exception ignored) {
                continue;
            }
            boostManager.addBoost(boost);
        }
    }

    public void register() {
        //noinspection SpellCheckingInspection
        final PluginCommand command = getCommand("continuityboost");
        if (command != null) {
            command.setExecutor(new CommandHandler(this));
        } else {
            LoggingUtils.logInfo(Level.SEVERE, "Cannot get command! Please contact dkim19375");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        boostManager.runTask();
        command.setTabCompleter(new TabCompletionHandler(this));
        getServer().getPluginManager().registerEvents(new PlayerExpChangeListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
    }

    public BoostManager getBoostManager() {
        return boostManager;
    }

    public ConfigFile getBoostsFile() {
        return boostsFile;
    }
}
