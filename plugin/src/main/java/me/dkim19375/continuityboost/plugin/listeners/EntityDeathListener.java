package me.dkim19375.continuityboost.plugin.listeners;

import me.dkim19375.continuityboost.api.enums.BoostType;
import me.dkim19375.continuityboost.plugin.ContinuityBoost;
import me.dkim19375.continuityboost.plugin.util.Boost;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class EntityDeathListener implements Listener {
    private final ContinuityBoost plugin;

    public EntityDeathListener(ContinuityBoost plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeathEvent(EntityDeathEvent e) {
        final Player player = e.getEntity().getKiller();
        if (player == null) {
            return;
        }
        if (plugin.getBoostManager().getCurrentBoostAmount(BoostType.ENTITY_DROP_MULTIPLIER) < 1) {
            return;
        }
        Boost boost = null;
        if (plugin.getBoostManager().getCurrentBoostsPerType(BoostType.ENTITY_DROP_MULTIPLIER).size() < 1) {
            return;
        }
        int m = 0;
        Set<EntityType> types = new HashSet<>();
        for (Boost b : plugin.getBoostManager().getCurrentBoostsPerType(BoostType.ENTITY_DROP_MULTIPLIER)) {
            if (b.getMultiplier() > m) {
                m = b.getMultiplier();
                boost = b;
            }
        }
        if (boost == null) {
            return;
        }
        if (boost.getAppliedEntities() == null) {
            types.addAll(Arrays.asList(EntityType.values().clone()));
        } else {
            types.addAll(boost.getAppliedEntities());
        }
        if (!types.contains(e.getEntityType())) {
            return;
        }
        final List<ItemStack> original = e.getDrops();
        final List<ItemStack> drops = new ArrayList<>();
        for (int i = 0; i < boost.getMultiplier(); i++) {
            drops.addAll(original);
        }
        e.getDrops().clear();
        for (ItemStack drop : drops) {
            e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), drop);
        }
    }
}
