package me.dkim19375.continuityboost.plugin.listeners;

import me.dkim19375.continuityboost.api.BoostType;
import me.dkim19375.continuityboost.api.Booster;
import me.dkim19375.continuityboost.plugin.ContinuityBoost;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class PlayerExpChangeListener implements Listener {
    private final ContinuityBoost plugin;

    public PlayerExpChangeListener(ContinuityBoost plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerExpChangeEvent(PlayerExpChangeEvent e) {
        boolean shouldMultiply = false;
        Booster boost = null;
        for (Booster b : plugin.getBoostManager().getCurrentBoosts().keySet()) {
            if (b.getType() == BoostType.EXP_MULTIPLIER) {
                shouldMultiply = true;
                boost = b;
                break;
            }
        }
        if (!shouldMultiply) {
            return;
        }
        e.setAmount(e.getAmount() * boost.getMultiplier());
    }
}
