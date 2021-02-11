package me.dkim19375.continuityboost.plugin.listeners;

import me.dkim19375.continuityboost.api.BoostType;
import me.dkim19375.continuityboost.plugin.ContinuityBoost;
import me.dkim19375.continuityboost.plugin.util.Boost;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntityDeathListener implements Listener {
    private final ContinuityBoost plugin;

    public EntityDeathListener(ContinuityBoost plugin) {
        this.plugin = plugin;
    }

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
            if (b.getAppliedEntities() != null) {
                types.addAll(b.getAppliedEntities());
            }
            if (b.getMultiplier() > m) {
                m = b.getMultiplier();
                boost = b;
            }
        }
        if (boost == null) {
            return;
        }
        if (!types.contains(e.getEntityType())) {
            return;
        }
        final List<ItemStack> original = new ArrayList<>(e.getDrops());
        final List<ItemStack> drops = new ArrayList<>();
        for (int i = 0; i < boost.getMultiplier(); i++) {
            drops.addAll(original);
        }
        e.getDrops().clear();
        e.getDrops().addAll(drops);
    }
}
