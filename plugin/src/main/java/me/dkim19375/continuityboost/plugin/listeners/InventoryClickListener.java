package me.dkim19375.continuityboost.plugin.listeners;

import me.dkim19375.continuityboost.api.enums.BoostType;
import me.dkim19375.continuityboost.plugin.ContinuityBoost;
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
        if (!plugin.getBoostManager().isToggled(e.getWhoClicked().getUniqueId())) {
            if (plugin.getDebuggedPlayers().contains(e.getWhoClicked().getUniqueId())) {
                e.getWhoClicked().sendMessage("DEBUG - Not toggled");
            }
            return;
        }
        if (!(e.getInventory().getType() == InventoryType.MERCHANT)) {
            if (plugin.getDebuggedPlayers().contains(e.getWhoClicked().getUniqueId())) {
                e.getWhoClicked().sendMessage("DEBUG - Not merchant");
            }
            return;
        }
        if (!(e.getInventory().getHolder() instanceof Villager)) {
            if (plugin.getDebuggedPlayers().contains(e.getWhoClicked().getUniqueId())) {
                e.getWhoClicked().sendMessage("DEBUG - Not villager");
            }
            return;
        }
        if (plugin.getBoostManager().getCurrentBoostsPerType(BoostType.VILLAGER).size() < 1) {
            if (plugin.getDebuggedPlayers().contains(e.getWhoClicked().getUniqueId())) {
                e.getWhoClicked().sendMessage("DEBUG - No current boosts");
            }
            return;
        }
        ((Villager) e.getInventory().getHolder()).getRecipes().forEach(recipe -> {
            recipe.setUses(1);
            if (plugin.getDebuggedPlayers().contains(e.getWhoClicked().getUniqueId())) {
                e.getWhoClicked().sendMessage("DEBUG - Not toggled");
            }
        });
    }
}
