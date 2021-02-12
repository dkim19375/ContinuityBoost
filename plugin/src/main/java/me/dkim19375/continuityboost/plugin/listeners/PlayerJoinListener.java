package me.dkim19375.continuityboost.plugin.listeners;

import me.dkim19375.continuityboost.plugin.ContinuityBoost;
import me.dkim19375.continuityboost.plugin.commands.CommandHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final ContinuityBoost plugin;

    public PlayerJoinListener(ContinuityBoost plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        plugin.getBoostManager().getToggledPlayers().add(e.getPlayer().getUniqueId());
        CommandHandler.giveBoostToggled(plugin, e.getPlayer());
    }
}