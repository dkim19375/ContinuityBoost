package me.dkim19375.continuityboost.plugin.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RandomTypeUtils {
    private RandomTypeUtils() {}

    public static List<ItemStack> getDropsOfItem(Material material, ItemStack tool, Entity entity) {
        final Location loc = getLoc(entity);
        final Block block = loc.getBlock();
        final BlockState oldState = block.getState();
        block.setType(material);
        final List<ItemStack> itemDrops = new ArrayList<>(block.getDrops(tool, entity));
        oldState.update(true, false);
        return itemDrops;
    }

    private static Location getLoc(Entity entity) {
        final Location highest = new Location(entity.getWorld(), entity.getLocation().getX(),
                entity.getWorld().getMaxHeight() - 1 - (int) entity.getLocation().getY(), entity.getLocation().getZ(),
                entity.getLocation().getYaw(), entity.getLocation().getPitch());
        final Location lowest = new Location(entity.getWorld(), entity.getLocation().getX(),
                (int) entity.getLocation().getY(), entity.getLocation().getZ(),
                entity.getLocation().getYaw(), entity.getLocation().getPitch());
        return highest.distance(entity.getLocation()) >= lowest.distance(entity.getLocation()) ? highest : lowest;
    }
}
