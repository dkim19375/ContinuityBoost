package me.dkim19375.continuityboost.plugin.util;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class AppliedBlocksHolder implements ConfigurationSerializable {
    private final Set<Material> appliedBlocks;
    private final Set<String> appliedBlocksString;

    public AppliedBlocksHolder(Set<Material> appliedBlocks) {
        this.appliedBlocks = appliedBlocks;
        appliedBlocksString = new HashSet<>();
        for (Material m : appliedBlocks) {
            appliedBlocksString.add(m.name());
        }
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new HashMap<>();
        map.put("materials", new ArrayList<>(appliedBlocksString));
        return map;
    }

    @SuppressWarnings("unused")
    public static AppliedBlocksHolder deserialize(@NotNull final Map<String, Object> map) {
        final Set<Material> list = new HashSet<>();
        final Set<String> stringList;
        try {
            //noinspection unchecked
            stringList = (HashSet<String>) map.get("materials");
            if (stringList == null) {
                throw new NullPointerException();
            }
        } catch (Exception e) {
            return null;
        }
        for (String s : stringList) {
            if (Material.matchMaterial(s) != null) {
                list.add(Material.matchMaterial(s));
            }
        }
        return new AppliedBlocksHolder(list);
    }

    public Set<Material> getAppliedBlocks() {
        return appliedBlocks;
    }

    public Set<String> getAppliedBlocksString() {
        return appliedBlocksString;
    }
}
