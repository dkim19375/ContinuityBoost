package me.dkim19375.continuityboost.plugin.listeners;

import me.dkim19375.continuityboost.plugin.ContinuityBoost;
import me.dkim19375.continuityboost.plugin.util.Boost;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BlockBreakListener implements Listener {
    private final ContinuityBoost plugin;

    public BlockBreakListener(ContinuityBoost plugin) {
        this.plugin = plugin;
    }

    @EventHandler()
    public void onBlockBreakEvent(BlockBreakEvent e) {
        boolean shouldMultiply = false;
        Boost boost = null;
        for (Boost b : plugin.getBoostManager().getCurrentBoosts().keySet()) {
            if (b.getType() == Boost.BoostType.ITEM_DROP_MULTIPLIER) {
                shouldMultiply = true;
                boost = b;
                break;
            }
        }
        if (!shouldMultiply) {
            return;
        }
        e.setDropItems(false);
        List<ItemStack> drops = new ArrayList<>(e.getBlock().getDrops(e.getPlayer().getInventory().getItemInMainHand(), e.getPlayer()));
        for (int i = 0; i < (boost.getMultiplier() - 1); i++) {
            drops.addAll(e.getBlock().getDrops(e.getPlayer().getInventory().getItemInMainHand(), e.getPlayer()));
        }
        for (ItemStack item : drops) {
            if (e.getBlock().getLocation().getWorld() == null) {
                continue;
            }
            e.getBlock().getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(), item);
        }
    }
}
