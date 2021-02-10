package me.dkim19375.continuityboost.plugin.listeners;

import me.dkim19375.continuityboost.api.BoostType;
import me.dkim19375.continuityboost.plugin.ContinuityBoost;
import me.dkim19375.continuityboost.plugin.util.Boost;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class InventoryClickListener implements Listener {
    private final ContinuityBoost plugin;

    public InventoryClickListener(ContinuityBoost plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClickEvent(InventoryClickEvent e) {
        if (!(e.getInventory().getType() == InventoryType.MERCHANT)) {
            return;
        }
        if (!(e.getInventory().getHolder() instanceof Villager)) {
            return;
        }
        if (plugin.getBoostManager().getBoostsPerType(BoostType.VILLAGER).size() < 1) {
            return;
        }
        ((Villager) e.getInventory().getHolder()).getRecipes().forEach(recipe -> recipe.setUses(1));
    }
}
