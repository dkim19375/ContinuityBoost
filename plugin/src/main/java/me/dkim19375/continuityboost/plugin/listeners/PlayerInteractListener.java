package me.dkim19375.continuityboost.plugin.listeners;

import me.dkim19375.continuityboost.plugin.ContinuityBoost;
import me.dkim19375.continuityboost.plugin.util.Boost;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.logging.Level;

public class PlayerInteractListener implements Listener {
    private final ContinuityBoost plugin;

    public PlayerInteractListener(ContinuityBoost plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        if (!e.getAction().name().startsWith("RIGHT_CLICK_")) {
            return;
        }
        final ItemStack clickedItem = e.getItem();
        if (clickedItem == null) {
            return;
        }

        Boost boost = null;
        for (Boost b : plugin.getBoostManager().getBoosts()) {
            if (isSimilar(b.getBoostingItem(), clickedItem)) {
                boost = b;
                break;
            }
        }
        if (boost == null) {
            return;
        }
        if (plugin.getConfig().getBoolean("remove-item")) {
            e.getPlayer().getInventory().remove(clickedItem);
        }
        plugin.getBoostManager().startBoost(boost);
    }

    public boolean isSimilar(ItemStack item1, ItemStack item2) {
        if (item1.getType() != item2.getType()) {
            return false;
        }
        final ItemMeta meta1 = item1.getItemMeta();
        final ItemMeta meta2 = item2.getItemMeta();
        if (meta1 == null && meta2 != null) {
            Bukkit.getLogger().log(Level.SEVERE, "boosting item = null");
            return false;
        }
        if (meta1 != null && meta2 == null) {
            Bukkit.getLogger().log(Level.SEVERE, "clicked item = null");
            return false;
        }
        if (meta1 != null) {
            if (!meta1.equals(meta2)) {
                System.out.println("Doesn't equal");
                return false;
            }
            System.out.println("Null meta");
        }
        if (!item1.getEnchantments().equals(item2.getEnchantments())) {
            System.out.println("Different enchants");
            return false;
        }
        return true;
    }
}
