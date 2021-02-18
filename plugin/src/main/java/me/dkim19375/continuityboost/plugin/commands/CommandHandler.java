package me.dkim19375.continuityboost.plugin.commands;

import me.dkim19375.continuityboost.api.enums.BoostType;
import me.dkim19375.continuityboost.plugin.ContinuityBoost;
import me.dkim19375.continuityboost.plugin.util.Boost;
import me.dkim19375.dkim19375core.NumberUtils;
import me.dkim19375.dkim19375core.PlayerUtils;
import me.dkim19375.dkim19375core.external.FormattingUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class CommandHandler implements CommandExecutor {
    private static final String NO_PERMISSION = ChatColor.RED + "You do not have permission to run this command!";
    private static final String TOO_MANY_ARGS = ChatColor.RED + "Too many arguments!";
    private static final String LITTLE_ARGS = ChatColor.RED + "Not enough arguments!";
    private static final String MUST_BE_PLAYER = ChatColor.RED + "You must be a player!";
    private static final String INVALID_NAME = ChatColor.RED + "Invalid name!";
    private final ContinuityBoost plugin;

    public CommandHandler(ContinuityBoost plugin) {
        this.plugin = plugin;
    }

    @NotNull
    private static String formatString(final String s) {
        String formatted = s;
        formatted = formatted.replace("_", " ");
        formatted = WordUtils.capitalize(formatted);
        return formatted;
    }

    public static void giveBoostToggled(ContinuityBoost plugin, Player togglePlayer) {
        for (Boost toggleBoost : plugin.getBoostManager().getCurrentBoostsPerType(BoostType.EFFECT)) {
            if (toggleBoost.getEffect() != null) {
                final long endTime = plugin.getBoostManager().getCurrentBoosts().get(toggleBoost) + (toggleBoost.getDuration() * 1000L);
                final long timeLeft = ((endTime - System.currentTimeMillis()) / 1000) * 20;
                final PotionEffect effect = new PotionEffect(toggleBoost.getEffect().getType(), (int) timeLeft, toggleBoost.getMultiplier());
                togglePlayer.addPotionEffect(effect);
            }
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("continuityboost.command")) {
            sender.sendMessage(NO_PERMISSION);
            return true;
        }
        if (args.length < 1) {
            showHelp(sender, label);
            sender.sendMessage(LITTLE_ARGS);
            return true;
        }
        switch (args[0].toLowerCase(Locale.ENGLISH)) {
            case "help":
                showHelp(sender, label);
                return true;
            //noinspection SpellCheckingInspection
            case "currentboosts":
                //noinspection SpellCheckingInspection
                if (!sender.hasPermission("continuityboost.currentboosts")) {
                    sender.sendMessage(NO_PERMISSION);
                    return true;
                }
                if (args.length > 1) {
                    sender.sendMessage(TOO_MANY_ARGS);
                    return true;
                }
                sender.sendMessage(ChatColor.GREEN + "Current boosts:");
                if (sender instanceof Player) {
                    if (NumberUtils.percentChance(80)) {
                        sender.sendMessage(ChatColor.GREEN + "Tip: You can click one of the UUIDs to copy it! (The uuid will show in the chat)");
                    }
                }
                for (Boost boost : plugin.getBoostManager().getCurrentBoosts().keySet()) {
                    showUUIDUsingComponents(sender, boost);
                }
                return true;
            case "boosts":
                if (!sender.hasPermission("continuityboost.boosts")) {
                    sender.sendMessage(NO_PERMISSION);
                    return true;
                }
                if (args.length > 1) {
                    sender.sendMessage(TOO_MANY_ARGS);
                    return true;
                }
                sender.sendMessage(ChatColor.GREEN + "Boosts:");
                if (sender instanceof Player) {
                    if (NumberUtils.percentChance(80)) {
                        sender.sendMessage(ChatColor.GREEN + "Tip: You can click one of the UUIDs to copy it! (The uuid will show in the chat)");
                    }
                }
                Set<String> names = new HashSet<>();
                for (Boost boost : plugin.getBoostManager().getBoosts()) {
                    if (!names.contains(boost.getName())) {
                        showUUIDUsingComponents(sender, boost);
                        names.add(boost.getName());
                        continue;
                    }
                    plugin.getBoostManager().removeBoost(boost);
                }
                return true;
            case "info":
                if (!sender.hasPermission("continuityboost.info")) {
                    sender.sendMessage(NO_PERMISSION);
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.AQUA + "--------------------------");
                    sender.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Current boosts:");
                    sender.sendMessage(ChatColor.AQUA + "--------------------------");
                    for (final Boost boost : new HashSet<>(plugin.getBoostManager().getCurrentBoosts().keySet())) {
                        showInfo(sender, boost);
                        sender.sendMessage(ChatColor.AQUA + "--------------------------");
                    }
                    return true;
                }
                if (args.length > 2) {
                    sender.sendMessage(TOO_MANY_ARGS);
                    return true;
                }
                final String name = args[1];
                Boost boost = null;
                for (Boost b : plugin.getBoostManager().getBoosts()) {
                    if (b.getName().equalsIgnoreCase(name)) {
                        boost = b;
                        break;
                    }
                }
                if (boost == null) {
                    sender.sendMessage(INVALID_NAME);
                    return true;
                }
                showInfo(sender, boost);
                return true;
            case "reload":
                if (!sender.hasPermission("continuityboost.reload")) {
                    sender.sendMessage(NO_PERMISSION);
                    return true;
                }
                if (args.length > 1) {
                    sender.sendMessage(TOO_MANY_ARGS);
                    return true;
                }
                sender.sendMessage(ChatColor.GREEN + "Reloading configs");
                plugin.reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "Successfully reloaded!");
                return true;
            case "stop":
                if (args.length < 2) {
                    sender.sendMessage(LITTLE_ARGS);
                    return true;
                }
                if (args.length > 2) {
                    sender.sendMessage(TOO_MANY_ARGS);
                    return true;
                }
                if (args[1].equalsIgnoreCase("all")) {
                    if (!sender.hasPermission("continuityboost.stop.all")) {
                        sender.sendMessage(NO_PERMISSION);
                        return true;
                    }
                    sender.sendMessage(ChatColor.RED + "Stopping all boosts!");
                    final int size = plugin.getBoostManager().getCurrentBoosts().size();
                    for (Boost boostStop : new HashSet<>(plugin.getBoostManager().getCurrentBoosts().keySet())) {
                        plugin.getBoostManager().forceStopBoost(boostStop);
                    }
                    sender.sendMessage(ChatColor.GOLD + "Successfully stopped all boosts! (" + size + ")");
                    return true;
                }
                try {
                    final Boost UUIDBoost = plugin.getBoostManager().getBoostByName(args[1]);
                    if (UUIDBoost != null) {
                        if (!sender.hasPermission("continuityboost.stop.uuid")) {
                            sender.sendMessage(NO_PERMISSION);
                            return true;
                        }
                        plugin.getBoostManager().forceStopBoost(UUIDBoost);
                        sender.sendMessage(ChatColor.GOLD + "Successfully stopped the boost!");
                        return true;
                    }
                } catch (IllegalArgumentException ignored) {
                }

                if (!sender.hasPermission("continuityboost.stop.type")) {
                    sender.sendMessage(NO_PERMISSION);
                    return true;
                }
                final BoostType boostType = BoostType.match(args[1]);
                if (boostType == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid boost type! (EXP_MULTIPLIER, ITEM_DROP_MULTIPLIER, EFFECT, ENTITY_DROP_MULTIPLIER, or VILLAGER)");
                    return true;
                }
                plugin.getBoostManager().forceStopBoost(boostType);
                sender.sendMessage(ChatColor.GREEN + "Successfully stopped all boosts for the type " + boostType.name() + "! ("
                        + plugin.getBoostManager().getCurrentBoostAmount(boostType) + ")");
                return true;
            case "remove":
                if (args.length < 2) {
                    sender.sendMessage(LITTLE_ARGS);
                    return true;
                }
                if (args.length > 2) {
                    sender.sendMessage(TOO_MANY_ARGS);
                    return true;
                }
                if (args[1].equalsIgnoreCase("all")) {
                    if (!sender.hasPermission("continuityboost.remove.all")) {
                        sender.sendMessage(NO_PERMISSION);
                        return true;
                    }
                    sender.sendMessage(ChatColor.RED + "Removing all boosts!");
                    final int size = plugin.getBoostManager().getBoosts().size();
                    for (Boost boostStop : new HashSet<>(plugin.getBoostManager().getBoosts())) {
                        plugin.getBoostManager().forceStopBoost(boostStop);
                        plugin.getBoostManager().removeBoost(boostStop);
                    }
                    sender.sendMessage(ChatColor.GOLD + "Successfully removed all boosts! (" + size + ")");
                    return true;
                }

                try {
                    final Boost UUIDBoost = plugin.getBoostManager().getBoostByName(args[1]);
                    if (UUIDBoost != null) {
                        if (!sender.hasPermission("continuityboost.remove.uuid")) {
                            sender.sendMessage(NO_PERMISSION);
                            return true;
                        }
                        plugin.getBoostManager().forceStopBoost(UUIDBoost);
                        plugin.getBoostManager().removeBoost(UUIDBoost);
                        sender.sendMessage(ChatColor.GOLD + "Successfully removed the boost!");
                        return true;
                    }
                } catch (IllegalArgumentException ignored) {
                }

                if (!sender.hasPermission("continuityboost.remove.type")) {
                    sender.sendMessage(NO_PERMISSION);
                    return true;
                }
                final BoostType boostTypeRemove = BoostType.match(args[1]);
                if (boostTypeRemove == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid boost type! (EXP_MULTIPLIER, ITEM_DROP_MULTIPLIER, EFFECT, ENTITY_DROP_MULTIPLIER, or VILLAGER)");
                    return true;
                }
                int amount = 0;
                for (Boost boostStop : new HashSet<>(plugin.getBoostManager().getBoosts())) {
                    if (boostStop.getType() == boostTypeRemove) {
                        plugin.getBoostManager().forceStopBoost(boostStop);
                        plugin.getBoostManager().removeBoost(boostStop);
                        amount++;
                    }
                }
                sender.sendMessage(ChatColor.GREEN + "Successfully removed all boosts for the type " + boostTypeRemove.name() + "! ("
                        + amount + ")");
                return true;
            case "add":
                if (!sender.hasPermission("continuityboost.add")) {
                    sender.sendMessage(NO_PERMISSION);
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(MUST_BE_PLAYER);
                    return true;
                }
                if (args.length < 6) {
                    sender.sendMessage(LITTLE_ARGS);
                    return true;
                }
                final String boostName = args[1];
                if (plugin.getBoostManager().getBoostByName(boostName) != null) {
                    sender.sendMessage(INVALID_NAME);
                    return true;
                }
                final Player player = (Player) sender;
                final int duration;
                try {
                    duration = Integer.parseInt(args[2]);
                } catch (NumberFormatException ignored) {
                    sender.sendMessage(ChatColor.RED + args[2] + " is not a number!");
                    return true;
                }
                final BoostType type = BoostType.match(args[3]);
                if (type == null) {
                    sender.sendMessage(ChatColor.RED + args[3] + " is not a valid type! (EXP_MULTIPLIER, ITEM_DROP_MULTIPLIER, EFFECT, ENTITY_DROP_MULTIPLIER, or VILLAGER)");
                    return true;
                }
                final int multiplier;
                try {
                    multiplier = Integer.parseInt(args[4]);
                } catch (NumberFormatException ignored) {
                    sender.sendMessage(ChatColor.RED + args[4] + " is not a number!");
                    return true;
                }
                final PotionEffectType effectType = PotionEffectType.getByName(args[5]);
                final Set<Material> selectedMaterials = new HashSet<>();
                final Set<String> invalidMaterials = new HashSet<>();
                boolean all = false;
                if (args[5].contains("*")) {
                    all = true;
                } else if (args[5].contains(",")) {
                    for (String s : args[5].split(",")) {
                        final Material mat = Material.matchMaterial(s.toUpperCase());
                        if (mat == null) {
                            invalidMaterials.add(s);
                            continue;
                        }
                        if (mat.isBlock()) {
                            selectedMaterials.add(mat);
                            continue;
                        }
                        invalidMaterials.add(s);
                    }
                } else {
                    final Material mat = Material.matchMaterial(args[5].toUpperCase());
                    if (mat != null) {
                        if (mat.isBlock()) {
                            selectedMaterials.add(mat);
                        } else {
                            invalidMaterials.add(args[5]);
                        }
                    } else {
                        invalidMaterials.add(args[5]);
                    }
                }
                final Set<EntityType> selectedEntities = new HashSet<>();
                final Set<String> invalidEntities = new HashSet<>();
                boolean allEntities = false;
                if (args[5].contains("*")) {
                    allEntities = true;
                } else if (args[5].contains(",")) {
                    for (String s : args[5].split(",")) {
                        final EntityType entityType = getType(s);
                        if (entityType == null) {
                            invalidEntities.add(s);
                            continue;
                        }
                        if (entityType.isAlive()) {
                            selectedEntities.add(entityType);
                            continue;
                        }
                        invalidEntities.add(s);
                    }
                } else {
                    final EntityType entityType = getType(args[5]);
                    if (entityType != null) {
                        if (entityType.isAlive()) {
                            selectedEntities.add(entityType);
                        } else {
                            invalidMaterials.add(args[5]);
                        }
                    } else {
                        invalidEntities.add(args[5]);
                    }
                }
                final String boostMessage;
                switch (type) {
                    case EFFECT:
                        if (effectType == null) {
                            sender.sendMessage(ChatColor.RED + args[4] + " is not a potion effect!");
                            return true;
                        }
                        boostMessage = getRestArgs(args, 6);
                        final PotionEffect effect = new PotionEffect(effectType, duration * 20, multiplier - 1);
                        final ItemStack eAdder = new ItemStack(player.getInventory().getItemInMainHand());
                        eAdder.setAmount(1);
                        final ItemMeta eMeta = eAdder.getItemMeta();
                        if (eMeta != null) {
                            ((Damageable) eMeta).setDamage(0);
                            eAdder.setItemMeta(eMeta);
                        }
                        Boost newBoost = new Boost(eAdder, duration, type, boostMessage, effect, multiplier, boostName, null, null);
                        plugin.getBoostManager().addBoost(newBoost);
                        break;
                    case ITEM_DROP_MULTIPLIER:
                        if (invalidMaterials.size() > 0) {
                            sender.sendMessage(ChatColor.RED + "The following were not valid Materials:");
                            for (String s : invalidMaterials) {
                                sender.sendMessage(ChatColor.GOLD + s);
                            }
                            return true;
                        }
                        boostMessage = getRestArgs(args, 6);
                        final ItemStack iAdder = new ItemStack(player.getInventory().getItemInMainHand());
                        iAdder.setAmount(1);
                        final ItemMeta iMeta = iAdder.getItemMeta();
                        if (iMeta != null) {
                            ((Damageable) iMeta).setDamage(0);
                            iAdder.setItemMeta(iMeta);
                        }
                        Boost newB = new Boost(iAdder, duration, type, boostMessage, null, multiplier, boostName, all ? null : selectedMaterials, null);
                        plugin.getBoostManager().addBoost(newB);
                        break;
                    case ENTITY_DROP_MULTIPLIER:
                        if (invalidEntities.size() > 0) {
                            sender.sendMessage(ChatColor.RED + "The following were not valid Entities:");
                            for (String s : invalidEntities) {
                                sender.sendMessage(ChatColor.GOLD + s);
                            }
                            return true;
                        }
                        boostMessage = getRestArgs(args, 6);
                        final ItemStack enAdder = new ItemStack(player.getInventory().getItemInMainHand());
                        enAdder.setAmount(1);
                        final ItemMeta enMeta = enAdder.getItemMeta();
                        if (enMeta != null) {
                            ((Damageable) enMeta).setDamage(0);
                            enAdder.setItemMeta(enMeta);
                        }
                        Boost newE = new Boost(enAdder, duration, type, boostMessage, null, multiplier, boostName, allEntities ? null : null, selectedEntities);
                        plugin.getBoostManager().addBoost(newE);
                        break;
                    default:
                        boostMessage = getRestArgs(args, 5);
                        final ItemStack dAdder = new ItemStack(player.getInventory().getItemInMainHand());
                        dAdder.setAmount(1);
                        final ItemMeta dMeta = dAdder.getItemMeta();
                        if (dMeta != null) {
                            ((Damageable) dMeta).setDamage(0);
                            dAdder.setItemMeta(dMeta);
                        }
                        Boost nBoost = new Boost(dAdder, duration, type, boostMessage, null, multiplier, boostName, null, null);
                        plugin.getBoostManager().addBoost(nBoost);
                        break;
                }
                sender.sendMessage(ChatColor.GREEN + "Successfully created a boost! (Name: " + boostName + ")");
                return true;
            //noinspection SpellCheckingInspection
            case "giveitem":
                if (args.length < 2) {
                    sender.sendMessage(LITTLE_ARGS);
                    return true;
                }
                if (args.length > 3) {
                    sender.sendMessage(TOO_MANY_ARGS);
                    return true;
                }
                if (args.length == 2) {
                    //noinspection SpellCheckingInspection
                    if (!sender.hasPermission("continuityboost.giveitem.self")) {
                        sender.sendMessage(NO_PERMISSION);
                        return true;
                    }
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(MUST_BE_PLAYER);
                        return true;
                    }
                    final Player p = (Player) sender;
                    final Boost boostToGive = plugin.getBoostManager().getBoostByName(args[1]);
                    if (boostToGive == null) {
                        p.sendMessage(INVALID_NAME);
                        return true;
                    }
                    final boolean dropped = giveItem(p, boostToGive.getBoostingItem());
                    p.sendMessage(ChatColor.GREEN + "You have been given the item!"
                            + (dropped ? "\nYour inventory is full so it was dropped on the ground." : ""));
                    return true;
                }
                //noinspection SpellCheckingInspection
                if (!sender.hasPermission("continuityboost.giveitem.others")) {
                    sender.sendMessage(NO_PERMISSION);
                    return true;
                }
                final Player playerToGive = PlayerUtils.getFromAll(args[2]);
                final Boost boostToGive = plugin.getBoostManager().getBoostByName(args[1]);
                if (playerToGive == null) {
                    sender.sendMessage(ChatColor.RED + "That is not a valid username or UUID!");
                    return true;
                }
                if (boostToGive == null) {
                    sender.sendMessage(INVALID_NAME);
                    return true;
                }
                final boolean dropped = giveItem(playerToGive, boostToGive.getBoostingItem());
                sender.sendMessage(ChatColor.GREEN + "Successfully gave them the item!");
                playerToGive.sendMessage(ChatColor.GREEN + "You have been given a boosting item!"
                        + (dropped ? "\nYour inventory is full so it was dropped on the ground." : ""));
                return true;
            case "toggle":
                if (args.length > 2) {
                    sender.sendMessage(TOO_MANY_ARGS);
                    return true;
                }
                if (args.length < 2) {
                    if (!sender.hasPermission("continuityboost.toggle.self")) {
                        sender.sendMessage(NO_PERMISSION);
                        return true;
                    }
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(MUST_BE_PLAYER);
                        return true;
                    }
                    final Player togglePlayer = (Player) sender;
                    final boolean toggled = plugin.getBoostManager().togglePlayer(togglePlayer.getUniqueId());
                    if (toggled) {
                        sender.sendMessage(applyColors("&6&lGlobal Boost &7&l[&a✔&7&l]"));
                        giveBoostToggled(togglePlayer);
                        return true;
                    }
                    sender.sendMessage(applyColors("&6&lGlobal Boost &7&l[&c✕&7&l]"));
                    for (Boost toggleBoost : plugin.getBoostManager().getCurrentBoostsPerType(BoostType.EFFECT)) {
                        if (toggleBoost.getEffect() != null) {
                            togglePlayer.removePotionEffect(toggleBoost.getEffect().getType());
                        }
                    }
                    return true;
                }
                if (!sender.hasPermission("continuityboost.toggle.others")) {
                    sender.sendMessage(NO_PERMISSION);
                    return true;
                }
                final Player togglePlayer = PlayerUtils.getFromAll(args[1]);
                if (togglePlayer == null) {
                    sender.sendMessage(ChatColor.RED + "That is not a valid username or UUID!");
                    return true;
                }
                final boolean toggled = plugin.getBoostManager().togglePlayer(togglePlayer.getUniqueId());
                if (toggled) {
                    sender.sendMessage(applyColors("&6Successfully toggled their boost on!"));
                    togglePlayer.sendMessage(applyColors("&6&lGlobal Boost &7&l[&a✔&7&l]"));
                    giveBoostToggled(togglePlayer);
                    return true;
                }
                sender.sendMessage(ChatColor.GOLD + "You have now toggled off the boosts!");
                togglePlayer.sendMessage(applyColors("&6&lGlobal Boost &7&l[&c✕&7&l]"));
                for (Boost toggleBoost : plugin.getBoostManager().getCurrentBoostsPerType(BoostType.EFFECT)) {
                    if (toggleBoost.getEffect() != null) {
                        togglePlayer.removePotionEffect(toggleBoost.getEffect().getType());
                    }
                }
                return true;
            case "start":
                if (!sender.hasPermission("continuityboost.start")) {
                    sender.sendMessage(NO_PERMISSION);
                    return true;
                }
                if (args.length > 2) {
                    final Boost startBoost;
                    try {
                        startBoost = plugin.getBoostManager().getBoostByName(args[1]);
                        if (startBoost == null) {
                            throw new NullPointerException("The boost cannot be null!");
                        }
                    } catch (IllegalArgumentException | NullPointerException ignored) {
                        sender.sendMessage(INVALID_NAME);
                        return true;
                    }
                    final String message = getRestArgs(args, 2);
                    if (message.equalsIgnoreCase("none")) {
                        plugin.getBoostManager().startBoost(startBoost, sender instanceof Player ? (Player) sender : null, false);
                        return true;
                    }
                    plugin.getBoostManager().startBoost(startBoost, sender instanceof Player ? (Player) sender : null, true, message);
                    return true;
                }
                final Boost startBoost;
                try {
                    startBoost = plugin.getBoostManager().getBoostByName(args[1]);
                    if (startBoost == null) {
                        throw new NullPointerException("The boost cannot be null!");
                    }
                } catch (IllegalArgumentException | NullPointerException ignored) {
                    sender.sendMessage(INVALID_NAME);
                    return true;
                }
                plugin.getBoostManager().startBoost(startBoost, sender instanceof Player ? (Player) sender : null);
                sender.sendMessage(ChatColor.GREEN + "Successfully started the boost!");
                return true;
            case "debug":
                if (!sender.hasPermission("continuityboost.debug")) {
                    sender.sendMessage(NO_PERMISSION);
                    return true;
                }
                if (args.length > 1) {
                    sender.sendMessage(TOO_MANY_ARGS);
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(MUST_BE_PLAYER);
                    return true;
                }
                final Player debugPlayer = (Player) sender;
                if (plugin.getDebuggedPlayers().contains(debugPlayer.getUniqueId())) {
                    plugin.getDebuggedPlayers().remove(debugPlayer.getUniqueId());
                    sender.sendMessage(ChatColor.GREEN + "Successfully disabled debug!");
                    return true;
                }
                plugin.getDebuggedPlayers().add(debugPlayer.getUniqueId());
                sender.sendMessage(ChatColor.GREEN + "Successfully enabled debug!");
                return true;
            default:
                sender.sendMessage(ChatColor.RED + "Invalid argument!");
                return true;
        }
    }

    private void giveBoostToggled(Player togglePlayer) {
        giveBoostToggled(plugin, togglePlayer);
    }

    private boolean giveItem(Player p, ItemStack item) {
        if (p.getInventory().firstEmpty() == -1) {
            p.getWorld().dropItemNaturally(p.getLocation(), item);
            return true;
        }
        p.getInventory().addItem(item);
        return false;
    }

    private String applyColors(String s) {
        return FormattingUtils.formatWithColors(s);
    }

    private void showUUIDUsingComponents(@NotNull CommandSender sender, Boost boost) {
        final TextComponent message = new TextComponent(ChatColor.GREEN + "- " + boost.getName());
        message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, boost.getName()));
        if (sender instanceof Player) {
            ((Player) sender).spigot().sendMessage(message);
            return;
        }
        sender.sendMessage(ChatColor.GREEN + "- " + boost.getName());
    }

    private String combine(Set<Material> list) {
        return combineStrings(list.stream().map(Material::name).collect(Collectors.toList()));
    }

    private String combineEntities(Set<EntityType> list) {
        return combineStrings(list.stream().map(EntityType::name).collect(Collectors.toList()));
    }

    private String combineStrings(List<String> list) {
        StringBuilder s = new StringBuilder();
        int i = 1;
        for (String string : list) {
            s.append(string);
            if (i < list.size()) {
                s.append(", ");
            }
            i++;
        }
        return s.toString();
    }

    private void showHelp(CommandSender sender, String label) {
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "ContinuityBoost v" + plugin.getDescription().getVersion() + "Commands");
        sendFormatted(sender, label, "help", "Show this help page. <param> = required, [param] = optional");
        sendFormatted(sender, label, "currentBoosts", "Show all boosts currently happening");
        sendFormatted(sender, label, "boosts", "Show all boosts saved in the file");
        sendFormatted(sender, label, "info [name]", "See more information about a boost");
        sendFormatted(sender, label, "reload", "Reload the configuration files");
        sendFormatted(sender, label, "stop <type|all|name>", "Stop all boosts, or a specific type");
        sendFormatted(sender, label, "remove <type|all|name>", "Remove all boosts, or a specific type");
        sendFormatted(sender, label, "add <name> <time in seconds> <type> <multiplier> <effect (only if the type is EFFECT),  " +
                        "Material,Material,Material, or EntityType,EntityType etc> <boost message>",
                "Add the current item");
        //noinspection SpellCheckingInspection
        sendFormatted(sender, label, "giveitem <name> [player]", "Give the item to a player");
        sendFormatted(sender, label, "toggle [player]", "Toggle the boost for a player");
        sendFormatted(sender, label, "start <name> [custom message|NONE]", "Start a boost");
        sendFormatted(sender, label, "debug", "Show debug");
    }

    @Nullable
    private EntityType getType(String s) {
        try {
            return EntityType.valueOf(s.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    private String getRestArgs(final String[] args, final int index) {
        StringBuilder str = new StringBuilder();
        int i = 0;
        for (String ignored : args) {
            if (i < index) {
                i++;
                continue;
            }
            str.append(args[i]).append(" ");
            i++;
        }
        str = new StringBuilder(str.toString().trim());
        return str.toString();
    }

    private void sendFormatted(CommandSender sender, String label, String command, String desc) {
        sender.sendMessage(ChatColor.AQUA + "/" + label + " " + command + " - " + desc);
    }

    private String formatNumbers(final long number) {
        long newNumber = number;
        int hours;
        int minutes;
        int seconds;
        hours = (int) newNumber / 3600;
        newNumber = newNumber % 3600;
        minutes = (int) newNumber / 60;
        seconds = (int) newNumber % 60;
        if (hours < 1) {
            if (minutes < 1) {
                return seconds + " second" + (seconds == 1 ? "" : "s");
            }
            return minutes + " minute" + (minutes == 1 ? "" : "s") + " and " + seconds + " second" + (seconds == 1 ? "" : "s");
        }
        return hours + " hour" + (hours == 1 ? "" : "s") + ", " + minutes + " minute" + (minutes == 1 ? "" : "s") + ", and " + seconds + " second" + (seconds == 1 ? "" : "s");
    }

    public void showInfo(CommandSender sender, Boost boost) {
        try {
            sender.sendMessage(ChatColor.GOLD + "Info:");
            sender.sendMessage(ChatColor.GOLD + "BoostType: " + ChatColor.AQUA + boost.getType().name());
            sender.sendMessage(ChatColor.GOLD + "Multiplier: " + ChatColor.AQUA + boost.getMultiplier());
            if (boost.getType() == BoostType.ITEM_DROP_MULTIPLIER) {
                sender.sendMessage(ChatColor.GOLD + "Applied items: " + ChatColor.AQUA + (boost.getAppliedBlocks() == null
                        ? "ALL" : combine(boost.getAppliedBlocks())));
            }
            if (boost.getType() == BoostType.ENTITY_DROP_MULTIPLIER) {
                sender.sendMessage(ChatColor.GOLD + "Applied entities: " + ChatColor.AQUA + (boost.getAppliedEntities() == null
                        ? "ALL" : combineEntities(boost.getAppliedEntities())));
            }
            if (plugin.getBoostManager().getCurrentBoosts().containsKey(boost)) {
                final long startedTime = plugin.getBoostManager().getCurrentBoosts().get(boost);
                final long endTime = plugin.getBoostManager().getCurrentBoosts().get(boost) + (boost.getDuration() * 1000L);
                sender.sendMessage(ChatColor.GOLD + "Time started: "
                        + ChatColor.AQUA + formatNumbers((-(startedTime - System.currentTimeMillis())) / 1000) + " ago");
                sender.sendMessage(ChatColor.GOLD + "Time when expires: "
                        + ChatColor.AQUA + formatNumbers((endTime - System.currentTimeMillis()) / 1000));
            }
            sender.sendMessage(ChatColor.GOLD + "Total time of boost: " + ChatColor.AQUA + formatNumbers(boost.getDuration()));
            sender.sendMessage(ChatColor.GOLD + "Item: " + ChatColor.RESET + formatString(boost.getBoostingItem().getType().name()));
            final ItemMeta meta = boost.getBoostingItem().getItemMeta();
            sender.sendMessage(ChatColor.GOLD + "Item name: " + ChatColor.AQUA + ((meta == null)
                    ? "" : meta.getDisplayName()));
            final TextComponent message = new TextComponent(ChatColor.GOLD + "Name: " + ChatColor.AQUA + boost.getName());
            message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, boost.getName()));
            if (sender instanceof Player) {
                ((Player) sender).spigot().sendMessage(message);
                return;
            }
            sender.sendMessage(ChatColor.GOLD + "Name: " + ChatColor.AQUA + boost.getName());
        } catch (NullPointerException ignored) {
            plugin.getBoostManager().removeBoost(boost);
        }
    }
}