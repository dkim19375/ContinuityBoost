package me.dkim19375.continuityboost.plugin.util;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class AppliedHolder implements ConfigurationSerializable {
    private final Set<Material> appliedBlocks;
    private final Set<String> appliedBlocksString;
    private final Set<EntityType> appliedEntities;
    private final Set<String> appliedEntitiesString;

    public AppliedHolder(@Nullable Set<Material> appliedBlocks, @Nullable Set<EntityType> appliedEntities) {
        this.appliedBlocks = appliedBlocks;
        this.appliedEntities = appliedEntities;
        appliedBlocksString = new HashSet<>();
        appliedEntitiesString = new HashSet<>();
        for (Material m : appliedBlocks == null ? new ArrayList<Material>() : appliedBlocks) {
            appliedBlocksString.add(m.name());
        }
        for (EntityType e : appliedEntities == null ? new ArrayList<EntityType>() : appliedEntities) {
            appliedEntitiesString.add(e.name());
        }
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new HashMap<>();
        map.put("materials", new ArrayList<>(appliedBlocksString));
        map.put("entities", new ArrayList<>(appliedEntitiesString));
        return map;
    }

    @SuppressWarnings("unused")
    public static AppliedHolder deserialize(@NotNull final Map<String, Object> map) {
        final Set<Material> materialList = new HashSet<>();
        final Set<String> materialStringList;
        try {
            //noinspection unchecked
            materialStringList = new HashSet<>((List<String>) map.get("materials"));
        } catch (Exception e) {
            return null;
        }
        if (!materialStringList.isEmpty()) {
            for (String s : materialStringList) {
                if (Material.matchMaterial(s) != null) {
                    materialList.add(Material.matchMaterial(s));
                }
            }
        }
        final Set<EntityType> entityList = new HashSet<>();
        final Set<String> entityStringList;
        try {
            //noinspection unchecked
            entityStringList = new HashSet<>((List<String>) map.get("entities"));
        } catch (Exception e) {
            return null;
        }
        if (!entityStringList.isEmpty()) {
            for (String s : entityStringList) {
                try {
                    entityList.add(EntityType.valueOf(s));
                } catch (Exception ignored) {}
            }
        }
        return new AppliedHolder(materialList, entityList);
    }

    public Set<Material> getAppliedBlocks() {
        return appliedBlocks;
    }

    public Set<String> getAppliedBlocksString() {
        return appliedBlocksString;
    }

    public Set<EntityType> getAppliedEntities() {
        return appliedEntities;
    }

    public Set<String> getAppliedEntitiesString() {
        return appliedEntitiesString;
    }
}
