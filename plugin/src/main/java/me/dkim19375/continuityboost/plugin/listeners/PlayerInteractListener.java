package me.dkim19375.continuityboost.plugin.listeners;

import me.dkim19375.continuityboost.api.Booster;
import me.dkim19375.continuityboost.plugin.ContinuityBoost;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Set;

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
        Set<Booster> boosts = new HashSet<>();
        for (Booster b : plugin.getBoostManager().getBoosts()) {
            if (isSimilar(b.getBoostingItem(), clickedItem)) {
                boosts.add(b);
            }
        }
        if (boosts.size() < 1) {
            return;
        }
        if (plugin.getConfig().getBoolean("remove-item")) {
            if (e.getPlayer().getInventory().getItemInMainHand().isSimilar(e.getItem())) {
                removeOneItem(e.getPlayer().getInventory().getItemInMainHand());
            } else {
                if (e.getPlayer().getInventory().getItemInOffHand().isSimilar(e.getItem())) {
                    removeOneItem(e.getPlayer().getInventory().getItemInOffHand());
                }
            }
        }
        for (Booster boost : boosts) {
            plugin.getBoostManager().startBoost(boost);
        }
    }

    private void removeOneItem(ItemStack item) {
        item.setAmount(item.getAmount() - 1);
    }

    public boolean isSimilar(ItemStack item1, ItemStack item2) {
        if (item1.getType() != item2.getType()) {
            return false;
        }
        final ItemMeta meta1 = item1.getItemMeta();
        final ItemMeta meta2 = item2.getItemMeta();
        if (meta1 == null && meta2 != null) {
            return false;
        }
        if (meta1 != null && meta2 == null) {
            return false;
        }
        if (meta1 != null) {
            if (!meta1.equals(meta2)) {
                return false;
            }
        }
        return item1.getEnchantments().equals(item2.getEnchantments());
    }
}
