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
        if (appliedBlocks != null) {
            for (Material m : appliedBlocks) {
                appliedBlocksString.add(m.name());
            }
        }
        if (appliedEntities != null) {
            for (EntityType e : appliedEntities) {
                appliedEntitiesString.add(e.name());
            }
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
            materialStringList = (HashSet<String>) map.get("materials");
            if (materialStringList == null) {
                throw new NullPointerException();
            }
        } catch (Exception e) {
            return null;
        }
        for (String s : materialStringList) {
            if (Material.matchMaterial(s) != null) {
                materialList.add(Material.matchMaterial(s));
            }
        }
        final Set<EntityType> entityList = new HashSet<>();
        final Set<String> entityStringList;
        try {
            //noinspection unchecked
            entityStringList = (HashSet<String>) map.get("entities");
            if (entityStringList == null) {
                throw new NullPointerException();
            }
        } catch (Exception e) {
            return null;
        }
        for (String s : entityStringList) {
            try {
                entityList.add(EntityType.valueOf(s));
            } catch (Exception ignored) {}
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
