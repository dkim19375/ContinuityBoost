package me.dkim19375.continuityboost.plugin.listeners;

import me.dkim19375.continuityboost.api.BoostType;

import me.dkim19375.continuityboost.plugin.ContinuityBoost;
import me.dkim19375.continuityboost.plugin.util.Boost;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BlockBreakListener implements Listener {
    private final ContinuityBoost plugin;

    public BlockBreakListener(ContinuityBoost plugin) {
        this.plugin = plugin;
    }

    @EventHandler()
    public void onBlockBreakEvent(BlockBreakEvent e) {
        Boost boost = null;
        if (plugin.getBoostManager().getBoostsPerType(BoostType.ITEM_DROP_MULTIPLIER).size() < 1) {
            return;
        }
        int m = 0;
        for (Boost b : plugin.getBoostManager().getBoostsPerType(BoostType.ITEM_DROP_MULTIPLIER)) {
            if (b.getMultiplier() > m) {
                m = b.getMultiplier();
                boost = b;
            }
        }
        if (boost == null) {
            return;
        }
        e.setDropItems(false);
        final List<ItemStack> original = new ArrayList<>(e.getBlock().getDrops(e.getPlayer().getInventory().getItemInMainHand(), e.getPlayer()));
        final List<ItemStack> drops = new ArrayList<>();
        for (int i = 0; i < boost.getMultiplier(); i++) {
            for (ItemStack drop : original) {
                final Set<Material> appliedBlocks = boost.getAppliedBlocks();
                if (appliedBlocks != null) {
                    if (appliedBlocks.contains(drop.getType())) {
                        drops.add(drop);
                    }
                    continue;
                }
                drops.addAll(e.getBlock().getDrops(e.getPlayer().getInventory().getItemInMainHand(), e.getPlayer()));
            }
        }
        for (ItemStack item : drops) {
            if (e.getBlock().getLocation().getWorld() == null) {
                continue;
            }
            e.getBlock().getLocation().getWorld().dropItemNaturally(e.getBlock().getLocation(), item);
        }
    }
}
