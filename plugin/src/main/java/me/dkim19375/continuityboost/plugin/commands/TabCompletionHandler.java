package me.dkim19375.continuityboost.plugin.commands;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import me.dkim19375.continuityboost.plugin.ContinuityBoost;
import me.dkim19375.continuityboost.plugin.util.Boost;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class TabCompletionHandler implements TabCompleter {
    private final ContinuityBoost plugin;

    private final HashMultimap<String, String> completesListMap;

    public TabCompletionHandler(ContinuityBoost plugin) {
        this.plugin = plugin;
        completesListMap = HashMultimap.create();
        add("core", "help", "currentBoosts", "boosts", "info", "reload", "stop", "add");
        add("stop", "type", "all", "<uuid>");
        add("time", "<time in seconds>");
        String[] types = new String[Boost.BoostType.values().length];
        int i = 0;
        for (Boost.BoostType type : Boost.BoostType.values()) {
            types[i] = type.name();
            i++;
        }
        add("types", types);
        add("multiplier", "<multiplier>");
        String[] effects = new String[PotionEffectType.values().length];
        int eI = 0;
        for (PotionEffectType type : PotionEffectType.values()) {
            effects[eI] = type.getName();
            eI++;
        }
        add("effects", effects);
        add("msg", "<boost message>");
    }

    private Set<String> getCurrentBoosts() {
        final Set<String> set = new HashSet<>();
        for (final Boost boost : plugin.getBoostManager().getCurrentBoosts().keySet()) {
            set.add(boost.getUniqueId().toString());
        }
        return set;
    }

    private Set<String> getAllBoosts() {
        final Set<String> set = new HashSet<>();
        for (final Boost boost : plugin.getBoostManager().getBoosts()) {
            set.add(boost.getUniqueId().toString());
        }
        return set;
    }

    private Set<String> getTypes() {
        final Set<String> set = new HashSet<>();
        for (final Boost.BoostType type : Boost.BoostType.values()) {
            set.add(type.name().toLowerCase());
        }
        return set;
    }

    private void add(@SuppressWarnings("SameParameterValue") String key, String... args) { completesListMap.putAll(key, Arrays.asList(args)); }

    private List<String> getPartial(String token, Iterable<String> collection) {
        return StringUtil.copyPartialMatches(token, collection, new ArrayList<>());
    }

    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        switch (args.length) {
            case 0:
                return Lists.newArrayList(completesListMap.get("core"));
            case 1: return getPartial(args[0], completesListMap.get("core"));
            case 2:
                if (args[0].equalsIgnoreCase("stop") || args[0].equalsIgnoreCase("remove")) {
                    if (startsWith(args[1])) {
                        if (isType(args[1])) {
                            return getPartial(args[1], getTypes());
                        }
                        return getPartial(args[1], completesListMap.get("stop"));
                    }
                    return getPartial(args[1], getCurrentBoosts());
                }
                if (args[0].equalsIgnoreCase("info")) {
                    return getPartial(args[1], getAllBoosts());
                }
                if (args[0].equalsIgnoreCase("add")) {
                    return getPartial(args[1], completesListMap.get("time"));
                }
            case 3:
                if (args[0].equalsIgnoreCase("add")) {
                    return getPartial(args[2], completesListMap.get("types"));
                }
            case 4:
                if (args[0].equalsIgnoreCase("add")) {
                    return getPartial(args[3], completesListMap.get("multiplier"));
                }
            case 5:
                // add <time in seconds> <type> <multiplier> <effect (only if the type is EFFECT)> <boost message>
                if (args[0].equalsIgnoreCase("add")) {
                    final Boost.BoostType boostType = Boost.BoostType.match(args[2]);
                    if (boostType != null) {
                        if (boostType == Boost.BoostType.EFFECT) {
                            return getPartial(args[4], completesListMap.get("effects"));
                        }
                    }
                    return getPartial(args[4], completesListMap.get("msg"));
                }
            default:
                if (args.length > 5) {
                    return getPartial(args[args.length - 1], completesListMap.get("msg"));
                }
                return ImmutableList.of();
        }
    }

    private Set<String> combine(List<String> first, List<String> second) {
        Set<String> total = new HashSet<>();
        total.addAll(first);
        total.addAll(second);
        return total;
    }

    private Set<String> combine(Set<String> set, String... varargs) {
        Set<String> newSet = new HashSet<>();
        newSet.addAll(set);
        newSet.addAll(Arrays.asList(varargs));
        return newSet;
    }

    private boolean startsWith(String s) {
        if ("all".startsWith(s.toLowerCase())) {
            return true;
        }
        for (Boost.BoostType type : Boost.BoostType.values()) {
            if (type.name().toLowerCase().startsWith(s.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean isType(String s) {
        for (Boost.BoostType type : Boost.BoostType.values()) {
            if (type.name().toLowerCase().startsWith(s.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
