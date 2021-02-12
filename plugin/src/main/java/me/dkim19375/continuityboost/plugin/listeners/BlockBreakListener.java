package me.dkim19375.continuityboost.plugin.listeners;

import me.dkim19375.continuityboost.api.BoostType;
import me.dkim19375.continuityboost.plugin.ContinuityBoost;
import me.dkim19375.continuityboost.plugin.util.Boost;
import me.dkim19375.continuityboost.plugin.util.RandomTypeUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
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
        if (e.isCancelled()) {
            return;
        }
        Boost boost = null;
        if (plugin.getBoostManager().getCurrentBoostsPerType(BoostType.ITEM_DROP_MULTIPLIER).size() < 1) {
            return;
        }
        int m = 0;
        for (Boost b : plugin.getBoostManager().getCurrentBoostsPerType(BoostType.ITEM_DROP_MULTIPLIER)) {
            if (b.getMultiplier() > m) {
                m = b.getMultiplier();
                boost = b;
            }
        }
        if (boost == null) {
            return;
        }
        if (boost.getAppliedBlocks() != null) {
            if (!boost.getAppliedBlocks().contains(e.getBlock().getType())) {
                return;
            }
        }
        if (e.getPlayer().getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
            return;
        }
        e.setDropItems(false);
        final List<ItemStack> drops = new ArrayList<>();
        for (int i = 0; i < boost.getMultiplier(); i++) {
            drops.addAll(RandomTypeUtils.getDropsOfItem(e.getBlock().getType(), e.getPlayer().getInventory().getItemInMainHand(), e.getPlayer()));
        }
        int iron = 0;
        int gold = 0;
        for (ItemStack item : drops) {
            if (item.getType() == Material.IRON_ORE) {
                iron++;
            }
            if (item.getType() == Material.GOLD_ORE) {
                gold++;
            }
        }
        drops.removeIf(this::isIronOrGold);
        for (int i = 0; i < iron; i++) {
            drops.add(new ItemStack(Material.IRON_INGOT));
        }
        for (int i = 0; i < gold; i++) {
            drops.add(new ItemStack(Material.GOLD_INGOT));
        }
        for (ItemStack item : drops) {
            if (e.getBlock().getLocation().getWorld() == null) {
                continue;
            }
            e.getBlock().getLocation().getWorld().dropItem(e.getBlock().getLocation(), item);
        }
    }

    private boolean isIronOrGold(ItemStack item) {
        return item.getType() == Material.IRON_ORE || item.getType() == Material.GOLD_ORE;
    }
}
