package me.dkim19375.continuityboost.plugin.listeners;

import me.dkim19375.continuityboost.api.enums.BoostType;
import me.dkim19375.continuityboost.plugin.ContinuityBoost;
import me.dkim19375.continuityboost.plugin.util.Boost;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    private final ContinuityBoost plugin;

    public PlayerQuitListener(ContinuityBoost plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        if (!plugin.getBoostManager().isToggled(e.getPlayer())) {
            return;
        }
        for (Boost toggleBoost : plugin.getBoostManager().getCurrentBoostsPerType(BoostType.EFFECT)) {
            if (toggleBoost.getEffect() != null) {
                e.getPlayer().removePotionEffect(toggleBoost.getEffect().getType());
            }
        }
    }
}
