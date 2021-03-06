package me.dkim19375.continuityboost.plugin;

import me.dkim19375.continuityboost.api.BoostAPIProvider;
import me.dkim19375.continuityboost.api.enums.BoostType;
import me.dkim19375.continuityboost.plugin.api.BoostAPIImpl;
import me.dkim19375.continuityboost.plugin.commands.CommandHandler;
import me.dkim19375.continuityboost.plugin.commands.TabCompletionHandler;
import me.dkim19375.continuityboost.plugin.listeners.*;
import me.dkim19375.continuityboost.plugin.util.AppliedHolder;
import me.dkim19375.continuityboost.plugin.util.Boost;
import me.dkim19375.continuityboost.plugin.util.BoostManager;
import me.dkim19375.continuityboost.plugin.util.LoggingUtils;
import me.dkim19375.dkim19375core.ConfigFile;
import me.dkim19375.dkim19375core.CoreJavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class ContinuityBoost extends CoreJavaPlugin {
    private final BoostManager boostManager = new BoostManager(this);
    private ConfigFile boostsFile;
    private final Set<UUID> debuggedPlayers = new HashSet<>();

    @Override
    public void onLoad() {
        BoostAPIProvider.setApi(new BoostAPIImpl(this));
    }

    @Override
    public void onEnable() {
        try {
            register();
            saveConfigs();
        } catch (Exception e) {
            e.printStackTrace();
            log(Level.SEVERE, "Encountered an error while loading the plugin!");
            getServer().getPluginManager().disablePlugin(this);
        }
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
            if (boostsFile.getConfig().getSerializable(boost.getName(), Boost.class) == null) {
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
        ConfigurationSerialization.registerClass(AppliedHolder.class);
        boostsFile = new ConfigFile(this, "boosts.yml");
        command.setTabCompleter(new TabCompletionHandler(this));
        getServer().getPluginManager().registerEvents(new PlayerExpChangeListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            final Set<String> materials = new HashSet<>();
            for (Material material : Material.values()) {
                materials.add(material.name());
            }
            TabCompletionHandler.setMaterials(materials);
        });
        for (Player p : Bukkit.getOnlinePlayers()) {
            getBoostManager().getToggledPlayers().add(p.getUniqueId());
        }
    }

    public BoostManager getBoostManager() { return boostManager; }

    public ConfigFile getBoostsFile() { return boostsFile; }

    public Set<UUID> getDebuggedPlayers() {
        return debuggedPlayers;
    }
}