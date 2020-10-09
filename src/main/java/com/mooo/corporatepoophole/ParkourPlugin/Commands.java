package com.mooo.corporatepoophole.ParkourPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class Commands {
    public static boolean onCommand(CommandSender sender, Command cmd, String label, String[] args, JavaPlugin plugin, HashMap<String, HashMap> context) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This plugin's commands can only be ran by a player.");
            return true;
        }
        Player player = (Player) sender;
        HashMap<UUID, Boolean> isInMode = (HashMap)context.get("isInParkourMode");
        HashMap<UUID, Boolean> isInPracticeMode = context.get("isInPracticeMode");
        HashMap<UUID, Boolean> isInCourse = (HashMap)context.get("isInCourse");
        HashMap<UUID, Number> attempts = (HashMap)context.get("attempts");
        HashMap<UUID, Number> times = (HashMap)context.get("times");
        HashMap respawnPoints = (HashMap)context.get("respawnPoints");
        HashMap<UUID, Number> eventCooldown = context.get("eventCooldown");
        UUID playerID = player.getUniqueId();
        if (cmd.getName().equalsIgnoreCase("parkour")) {
            if (isInPracticeMode.get(playerID)) {
                isInPracticeMode.put(playerID, false);
            }
            if (isInMode.get(playerID)) {
                isInMode.put(playerID, false);
                Utils.resetCourse(context, player);
                eventCooldown.put(playerID, 0);
                player.getInventory().clear();
                sender.sendMessage(ChatColor.AQUA + "You've exited Parkour Mode!");
            } else {
                isInMode.put(playerID, true);
                player.getInventory().clear();
                sender.sendMessage(ChatColor.AQUA + "You've entered Parkour Mode!");
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("practice")) {
            if (isInMode.get(playerID)) {
                isInMode.put(playerID, false);
            }
            if (isInPracticeMode.get(playerID)) {
                isInPracticeMode.put(playerID, false);
                Utils.resetCourse(context, player);
                player.getInventory().clear();
                sender.sendMessage(ChatColor.AQUA + "You've exited Practice Mode!");
            } else {
                isInPracticeMode.put(playerID, true);
                Utils.resetCourse(context, player);
                player.getInventory().clear();

                PlayerInventory inventory = player.getInventory();
                ItemStack cp = new ItemStack(Material.WORKBENCH);
                ItemMeta meta = cp.getItemMeta();
                meta.setDisplayName("Set spawnpoint");
                cp.setItemMeta(meta);

                ItemStack cp2 = new ItemStack(Material.COMPASS);
                ItemMeta meta2 = cp2.getItemMeta();
                meta2.setDisplayName("Return to spawnpoint");
                cp2.setItemMeta(meta2);

                inventory.setItem(0, cp);
                inventory.setItem(1, cp2);
                respawnPoints.put(playerID, player.getLocation());
                sender.sendMessage(ChatColor.AQUA + "You've entered Practice Mode!");
            }
            return true;
        }
        return false;
    }
}
