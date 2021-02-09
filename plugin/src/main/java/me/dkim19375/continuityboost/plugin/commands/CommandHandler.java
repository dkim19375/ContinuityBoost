package me.dkim19375.continuityboost.plugin.commands;

import me.dkim19375.continuityboost.plugin.ContinuityBoost;
import me.dkim19375.continuityboost.plugin.util.Boost;
import me.dkim19375.dkim19375core.NumberUtils;
import me.dkim19375.dkim19375core.PlayerUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CommandHandler implements CommandExecutor {
    private static final String NO_PERMISSION = ChatColor.RED + "You do not have permission to run this command!";
    private static final String TOO_MANY_ARGS = ChatColor.RED + "Too many arguments!";
    private static final String LITTLE_ARGS = ChatColor.RED + "Not enough arguments!";
    private static final String MUST_BE_PLAYER = ChatColor.RED + "You must be a player!";
    private static final String INVALID_UUID = ChatColor.RED + "Invalid UUID!";
    private final ContinuityBoost plugin;

    public CommandHandler(ContinuityBoost plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(LITTLE_ARGS);
            return true;
        }
        if (args.length > 2) {
            if (!args[0].equalsIgnoreCase("add")) {
                //noinspection SpellCheckingInspection
                if (!args[0].equalsIgnoreCase("giveitem")) {
                    sender.sendMessage(TOO_MANY_ARGS);
                    return true;
                }
                if (args.length > 3) {
                    sender.sendMessage(TOO_MANY_ARGS);
                    return true;
                }
            }
        }
        switch (args[0].toLowerCase(Locale.ENGLISH)) {
            case "help":
                showHelp(sender, label);
                return true;
            //noinspection SpellCheckingInspection
            case "currentboosts":
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
                if (args.length > 1) {
                    sender.sendMessage(TOO_MANY_ARGS);
                    return true;
                }
                sender.sendMessage(ChatColor.GREEN + "Boosts:");
                System.out.println(plugin.getBoostManager().getBoosts());
                if (sender instanceof Player) {
                    if (NumberUtils.percentChance(80)) {
                        sender.sendMessage(ChatColor.GREEN + "Tip: You can click one of the UUIDs to copy it! (The uuid will show in the chat)");
                    }
                }
                Set<UUID> uuids = new HashSet<>();
                for (Boost boost : plugin.getBoostManager().getBoosts()) {
                    if (!uuids.contains(boost.getUniqueId())) {
                        showUUIDUsingComponents(sender, boost);
                        uuids.add(boost.getUniqueId());
                        continue;
                    }
                    plugin.getBoostManager().removeBoost(boost);
                }
                return true;
            case "info":
                if (args.length < 2) {
                    sender.sendMessage(LITTLE_ARGS);
                    return true;
                }
                try {
                    //noinspection ResultOfMethodCallIgnored
                    UUID.fromString(args[1]);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(INVALID_UUID);
                    return true;
                }
                final UUID uuid = UUID.fromString(args[1]);
                Boost boost = null;
                for (Boost b : plugin.getBoostManager().getBoosts()) {
                    if (b.getUniqueId().equals(uuid)) {
                        boost = b;
                        break;
                    }
                }
                if (boost == null) {
                    sender.sendMessage(INVALID_UUID);
                    return true;
                }
                try {
                    sender.sendMessage(ChatColor.GOLD + "Info:");
                    sender.sendMessage(ChatColor.GOLD + "BoostType: " + ChatColor.AQUA + boost.getType().name());
                    if (plugin.getBoostManager().getCurrentBoosts().containsKey(boost)) {
                        final long startedTime = plugin.getBoostManager().getCurrentBoosts().get(boost);
                        final long endTime = plugin.getBoostManager().getCurrentBoosts().get(boost) + (boost.getDuration() * 1000L);
                        sender.sendMessage(ChatColor.GOLD + "Time started: "
                                + ChatColor.AQUA + formatNumbers((-(startedTime - System.currentTimeMillis())) / 1000) + " ago");
                        sender.sendMessage(ChatColor.GOLD + "Time when expires: "
                                + ChatColor.AQUA + formatNumbers((endTime - System.currentTimeMillis()) / 1000));
                    }
                    sender.sendMessage(ChatColor.GOLD + "Total time of boost: " + ChatColor.AQUA + formatNumbers(boost.getDuration()));
                    sender.sendMessage(ChatColor.GOLD + "Item: " + ChatColor.AQUA + formatString(boost.getBoostingItem().getType().name()));
                    sender.sendMessage(ChatColor.GOLD + "Item name: " + ChatColor.AQUA + ((boost.getBoostingItem().getItemMeta() == null)
                            ? "" : Objects.requireNonNull(boost.getBoostingItem().getItemMeta()).getDisplayName()));
                    final TextComponent message = new TextComponent(ChatColor.GOLD + "UUID: " + ChatColor.AQUA + boost.getUniqueId().toString());
                    message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, boost.getUniqueId().toString()));
                    if (sender instanceof Player) {
                        ((Player) sender).spigot().sendMessage(message);
                        return true;
                    }
                    sender.sendMessage(ChatColor.GOLD + "UUID: " + ChatColor.AQUA + boost.getUniqueId().toString());
                } catch (NullPointerException ignored) {
                    plugin.getBoostsFile().getConfig().set(boost.getUniqueId().toString(), null);
                    plugin.getBoostManager().getBoosts().remove(boost);
                    plugin.getBoostManager().getCurrentBoosts().remove(boost);
                    plugin.getBoostManager().forceSave();
                }
                return true;
            case "reload":
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
                if (args[1].equalsIgnoreCase("all")) {
                    sender.sendMessage(ChatColor.RED + "Stopping all boosts!");
                    final int size = plugin.getBoostManager().getCurrentBoosts().size();
                    for (Boost boostStop : new HashSet<>(plugin.getBoostManager().getCurrentBoosts().keySet())) {
                        plugin.getBoostManager().forceStopBoost(boostStop);
                    }
                    sender.sendMessage(ChatColor.GOLD + "Successfully stopped all boosts! (" + size + ")");
                    return true;
                }
                try {
                    final Boost UUIDBoost = plugin.getBoostManager().getBoostByUUID(UUID.fromString(args[1]));
                    if (UUIDBoost != null) {
                        plugin.getBoostManager().forceStopBoost(UUIDBoost);
                        sender.sendMessage(ChatColor.GOLD + "Successfully stopped the boost!");
                        return true;
                    }
                } catch (IllegalArgumentException ignored) {}

                final Boost.BoostType boostType = Boost.BoostType.match(args[1]);
                if (boostType == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid boost type! (EXP_MULTIPLIER, ITEM_DROP_MULTIPLIER, EFFECT, or VILLAGER)");
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
                if (args[1].equalsIgnoreCase("all")) {
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
                    final Boost UUIDBoost = plugin.getBoostManager().getBoostByUUID(UUID.fromString(args[1]));
                    if (UUIDBoost != null) {
                        plugin.getBoostManager().forceStopBoost(UUIDBoost);
                        plugin.getBoostManager().removeBoost(UUIDBoost);
                        sender.sendMessage(ChatColor.GOLD + "Successfully removed the boost!");
                        return true;
                    }
                } catch (IllegalArgumentException ignored) {}

                final Boost.BoostType boostTypeRemove = Boost.BoostType.match(args[1]);
                if (boostTypeRemove == null) {
                    sender.sendMessage(ChatColor.RED + "Invalid boost type! (EXP_MULTIPLIER, ITEM_DROP_MULTIPLIER, EFFECT, or VILLAGER)");
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
                if (!(sender instanceof Player)) {
                    sender.sendMessage(MUST_BE_PLAYER);
                    return true;
                }
                if (args.length < 5) {
                    sender.sendMessage(LITTLE_ARGS);
                    return true;
                }
                final Player player = (Player) sender;
                final int duration;
                try {
                    duration = Integer.parseInt(args[1]);
                } catch (NumberFormatException ignored) {
                    sender.sendMessage(ChatColor.RED + args[1] + " is not a number!");
                    return true;
                }
                final Boost.BoostType type = Boost.BoostType.match(args[2]);
                if (type == null) {
                    sender.sendMessage(ChatColor.RED + args[2] + " is not a valid type! (EXP_MULTIPLIER, ITEM_DROP_MULTIPLIER, EFFECT, or VILLAGER)");
                    return true;
                }
                final int multiplier;
                try {
                    multiplier = Integer.parseInt(args[3]);
                } catch (NumberFormatException ignored) {
                    sender.sendMessage(ChatColor.RED + args[3] + " is not a number!");
                    return true;
                }
                final PotionEffectType effectType = PotionEffectType.getByName(args[4]);
                final Set<Material> selectedMaterials = new HashSet<>();
                final Set<String> invalidMaterials = new HashSet<>();
                if (args[4].contains(",")) {
                    for (String s : args[4].split(",")) {
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
                    final Material mat = Material.matchMaterial(args[4].toUpperCase());
                    if (mat != null) {
                        if (mat.isBlock()) {
                            selectedMaterials.add(mat);
                        } else {
                            invalidMaterials.add(args[4]);
                        }
                    } else {
                        invalidMaterials.add(args[4]);
                    }
                }
                final String boostMessage;
                UUID boostUUID;
                switch (type) {
                    case EFFECT:
                        if (effectType == null) {
                            sender.sendMessage(ChatColor.RED + args[4] + " is not a potion effect!");
                            return true;
                        }
                        boostMessage = getRestArgs(args, 5);
                        final PotionEffect effect = new PotionEffect(effectType, duration * 20, multiplier - 1);
                        Boost newBoost = new Boost(player.getInventory().getItemInMainHand(), duration, type, boostMessage, effect, multiplier, null, null);
                        plugin.getBoostManager().addBoost(newBoost);
                        boostUUID = newBoost.getUniqueId();
                        break;
                    case ITEM_DROP_MULTIPLIER:
                        if (invalidMaterials.size() > 0) {
                            sender.sendMessage(ChatColor.RED + "The following were not valid Materials:");
                            for (String s : invalidMaterials) {
                                sender.sendMessage(ChatColor.GOLD + s);
                            }
                            return true;
                        }
                        boostMessage = getRestArgs(args, 5);
                        Boost newB = new Boost(player.getInventory().getItemInMainHand(), duration, type, boostMessage, null, multiplier, null, selectedMaterials);
                        plugin.getBoostManager().addBoost(newB);
                        boostUUID = newB.getUniqueId();
                        break;
                    default:
                        boostMessage = getRestArgs(args, 4);
                        Boost nBoost = new Boost(player.getInventory().getItemInMainHand(), duration, type, boostMessage, null, multiplier, null, null);
                        plugin.getBoostManager().addBoost(nBoost);
                        boostUUID = nBoost.getUniqueId();
                        break;
                }
                sender.sendMessage(ChatColor.GREEN + "Successfully created a boost! (UUID: " + boostUUID + ")");
                return true;
            //noinspection SpellCheckingInspection
            case "giveitem":
                if (args.length < 2) {
                    sender.sendMessage(LITTLE_ARGS);
                    return true;
                }
                if (args.length == 2) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(MUST_BE_PLAYER);
                        return true;
                    }
                    final Player p = (Player) sender;
                    final Boost boostToGive = plugin.getBoostManager().getBoostByUUID(args[1]);
                    if (boostToGive == null) {
                        p.sendMessage(INVALID_UUID);
                        return true;
                    }
                    final boolean dropped = giveItem(p, boostToGive.getBoostingItem());
                    p.sendMessage(ChatColor.GREEN + "You have been given the item!"
                            + (dropped ? "\nYour inventory is full so it was dropped on the ground." : ""));
                    return true;
                }
                final Player playerToGive = PlayerUtils.getFromAll(args[1]);
                final Boost boostToGive = plugin.getBoostManager().getBoostByUUID(args[2]);
                if (playerToGive == null) {
                    sender.sendMessage(ChatColor.RED + "That is not a valid username or UUID!");
                    return true;
                }
                if (boostToGive == null) {
                    sender.sendMessage(INVALID_UUID);
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
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(MUST_BE_PLAYER);
                        return true;
                    }
                    final Player togglePlayer = (Player) sender;
                    final boolean toggled = plugin.getBoostManager().togglePlayer(togglePlayer.getUniqueId());
                    if (toggled) {
                        sender.sendMessage(ChatColor.GREEN + "You have now toggled on the boosts!");
                        for (Boost toggleBoost : plugin.getBoostManager().getBoostsPerType(Boost.BoostType.EFFECT)) {
                            if (toggleBoost.getEffect() != null) {
                                togglePlayer.addPotionEffect(toggleBoost.getEffect());
                            }
                        }
                        return true;
                    }
                    sender.sendMessage(ChatColor.GOLD + "You have now toggled off the boosts!");
                    for (Boost toggleBoost : plugin.getBoostManager().getBoostsPerType(Boost.BoostType.EFFECT)) {
                        if (toggleBoost.getEffect() != null) {
                            togglePlayer.removePotionEffect(toggleBoost.getEffect().getType());
                        }
                    }
                    return true;
                }
                final Player togglePlayer = PlayerUtils.getFromAll(args[1]);
                if (togglePlayer == null) {
                    sender.sendMessage(ChatColor.RED + "That is not a valid username or UUID!");
                    return true;
                }
                final boolean toggled = plugin.getBoostManager().togglePlayer(togglePlayer.getUniqueId());
                if (toggled) {
                    sender.sendMessage(ChatColor.GREEN + "You have now toggled on the boosts!");
                    togglePlayer.sendMessage(ChatColor.GREEN + "Your boost has been toggled on by someone else!");
                    for (Boost toggleBoost : plugin.getBoostManager().getBoostsPerType(Boost.BoostType.EFFECT)) {
                        if (toggleBoost.getEffect() != null) {
                            togglePlayer.addPotionEffect(toggleBoost.getEffect());
                        }
                    }
                    return true;
                }
                sender.sendMessage(ChatColor.GOLD + "You have no toggled off the boosts!");
                togglePlayer.sendMessage(ChatColor.GOLD + "Your boost has been toggled off by someone else!");
                for (Boost toggleBoost : plugin.getBoostManager().getBoostsPerType(Boost.BoostType.EFFECT)) {
                    if (toggleBoost.getEffect() != null) {
                        togglePlayer.removePotionEffect(toggleBoost.getEffect().getType());
                    }
                }
                return true;
            default:
                sender.sendMessage(ChatColor.RED + "Invalid argument!");
                return true;
        }
    }

    private boolean giveItem(Player p, ItemStack item) {
        if (p.getInventory().firstEmpty() == -1) {
            p.getWorld().dropItemNaturally(p.getLocation(), item);
            return true;
        }
        p.getInventory().addItem(item);
        return false;
    }

    private void showUUIDUsingComponents(@NotNull CommandSender sender, Boost boost) {
        final TextComponent message = new TextComponent(ChatColor.GREEN + "- " + boost.getUniqueId().toString());
        message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, boost.getUniqueId().toString()));
        if (sender instanceof Player) {
            ((Player) sender).spigot().sendMessage(message);
            return;
        }
        sender.sendMessage(ChatColor.GREEN + "- " + boost.getUniqueId().toString());
    }

    private void showHelp(CommandSender sender, String label) {
        sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "ContinuityBoost Commands");
        sendFormatted(sender, label, "help", "Show this help page. <param> = required, [param] = optional");
        sendFormatted(sender, label, "currentBoosts", "Show all boosts currently happening");
        sendFormatted(sender, label, "boosts", "Show all boosts saved in the file");
        sendFormatted(sender, label, "info <uuid>", "See more information about a boost");
        sendFormatted(sender, label, "reload", "Reload the configuration files");
        sendFormatted(sender, label, "stop <type|all|uuid>", "Stop all boosts, or a specific type");
        sendFormatted(sender, label, "remove <type|all|uuid>", "Remove all boosts, or a specific type");
        sendFormatted(sender, label, "add <time in seconds> <type> <multiplier> <effect (only if the type is EFFECT), or Material,Material,Material etc> <boost message>",
                "Add the current item");
        //noinspection SpellCheckingInspection
        sendFormatted(sender, label, "giveitem <uuid> [player]", "Give the item to a player");
        sendFormatted(sender, label, "toggle [player]", "Toggle the boost for a player");
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

    @NotNull
    private static String formatString(final String s) {
        String formatted = s;
        formatted = formatted.replace("_", " ");
        formatted = WordUtils.capitalize(formatted);
        return formatted;
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
            return minutes + " minute" + (minutes == 1 ? "" : "s") + ", " + seconds + " second" + (seconds == 1 ? "" : "s");
        }
        return hours + " hour" + (hours == 1 ? "" : "s") + ", " + minutes + " minute" + (minutes == 1 ? "" : "s") + ", " + seconds + " second" + (seconds == 1 ? "" : "s");
    }
}