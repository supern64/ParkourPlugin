package com.mooo.corporatepoophole.ParkourPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.UUID;

public class Listeners implements Listener {
    private HashMap<String, HashMap> context;
    public Listeners(HashMap<String, HashMap> ctx) {
        context = ctx;
    }
    // handling block detection
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        HashMap<UUID, Boolean> isInMode = context.get("isInParkourMode");
        HashMap<UUID, Boolean> isInCourse = context.get("isInCourse");
        HashMap<UUID, Number> attempts = context.get("attempts");
        HashMap<UUID, Number> times = context.get("times");
        HashMap respawnPoints = context.get("respawnPoints");
        HashMap<UUID, Number> eventCooldown = context.get("eventCooldown");
        Player player = event.getPlayer();
        UUID playerID = player.getUniqueId();
        if (isInMode.get(playerID)) {
            Block standingBlock = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
            if (standingBlock.getType() == Material.IRON_BLOCK) {
                if ((int)eventCooldown.get(playerID) > 0) {
                    return;
                }
                if (!isInCourse.get(playerID)) {
                    isInCourse.put(playerID, true);

                    attempts.put(playerID, 0);
                    respawnPoints.put(playerID, player.getLocation());

                    PlayerInventory inventory = player.getInventory();
                    inventory.clear();

                    ItemStack cp = new ItemStack(Material.COMPASS);
                    ItemMeta meta = cp.getItemMeta();
                    meta.setDisplayName("Return to Checkpoint");
                    cp.setItemMeta(meta);

                    ItemStack cp2 = new ItemStack(Material.ACACIA_DOOR_ITEM);
                    ItemMeta meta2 = cp2.getItemMeta();
                    meta2.setDisplayName("Quit current Parkour");
                    cp2.setItemMeta(meta2);

                    inventory.setItem(0, cp);
                    inventory.setItem(1, cp2);
                    player.sendMessage(ChatColor.AQUA + "You started the parkour!");
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 10, 1);
                } else {
                    player.sendMessage(ChatColor.AQUA + "You've already started the parkour!");
                }
                eventCooldown.put(playerID, 2);
            } else if (standingBlock.getType() == Material.GOLD_BLOCK) {
                if ((int)eventCooldown.get(playerID) > 0) {
                    return;
                }
                if (isInCourse.get(playerID)) {
                    respawnPoints.put(playerID, player.getLocation());
                    player.sendMessage(ChatColor.AQUA + "You got a checkpoint!");
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 10, 1);
                } else {
                    player.sendMessage(ChatColor.AQUA + "You aren't in a parkour course!");
                }
                eventCooldown.put(playerID, 2);
            } else if (standingBlock.getType() == Material.DIAMOND_BLOCK) {
                if ((int)eventCooldown.get(playerID) > 0) {
                    return;
                }
                if (isInCourse.get(playerID)) {
                    int time = (int) times.get(playerID);
                    String interpretedTime;
                    int hour = time / 60;
                    int minute = time % 60;
                    interpretedTime = String.format("%02d", hour) + ":" + String.format("%02d", minute);
                    player.getInventory().clear();
                    player.sendMessage(ChatColor.AQUA + "You finished the parkour in " + interpretedTime + " and " + attempts.get(playerID) + " fails!");
                    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 10, 1);
                    Utils.resetCourse(context, player);
                } else {
                    player.sendMessage(ChatColor.AQUA + "You aren't in a parkour course!");
                }
                eventCooldown.put(playerID, 2);
            }
        }
    }
    // anti drop
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        HashMap<UUID, Boolean> isInCourse = context.get("isInCourse");
        HashMap<UUID, Boolean> isInPracticeMode = context.get("isInPracticeMode");
        Player player = event.getPlayer();
        UUID playerID = player.getUniqueId();
        ItemStack item = event.getItemDrop().getItemStack();
        String itemName = item.getItemMeta().getDisplayName();
        if (isInCourse.get(playerID)) {
            if ((itemName.equals("Return to Checkpoint") && item.getType() == Material.COMPASS) || (itemName.equals("Quit current Parkour") && item.getType() == Material.ACACIA_DOOR_ITEM)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.AQUA + "Please exit the course or Parkour Mode to get rid of this!");
            }
        } else if (isInPracticeMode.get(playerID)) {
            if ((itemName.equals("Return to spawnpoint") && item.getType() == Material.COMPASS) || (itemName.equals("Set spawnpoint") && item.getType() == Material.WORKBENCH)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.AQUA + "Please exit Practice Mode to get rid of this!");
            }
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HashMap<UUID, Boolean> isInCourse = context.get("isInCourse");
        HashMap<UUID, Boolean> isInPracticeMode = context.get("isInPracticeMode");
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player)event.getWhoClicked();
            UUID playerID = player.getUniqueId();
            ItemStack item = event.getCurrentItem();
            if (item == null || item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null) {
                return;
            }
            String itemName = item.getItemMeta().getDisplayName();
            if (isInCourse.get(playerID)) {
                if ((itemName.equals("Return to Checkpoint") && item.getType() == Material.COMPASS) || (itemName.equals("Quit current Parkour") && item.getType() == Material.ACACIA_DOOR_ITEM)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.AQUA + "Please exit the course or Parkour Mode to get rid of this!");
                }
            } else if (isInPracticeMode.get(playerID)) {
                if ((itemName.equals("Return to spawnpoint") && item.getType() == Material.COMPASS) || (itemName.equals("Set spawnpoint") && item.getType() == Material.WORKBENCH)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.AQUA + "Please exit Practice Mode to get rid of this!");
                }
            }
        }
    }
    // commit die and respawn
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        HashMap<UUID, Boolean> isInCourse = context.get("isInCourse");
        HashMap<UUID, Boolean> isInPracticeMode = context.get("isInPracticeMode");
        HashMap<UUID, Number> attempts = context.get("attempts");
        if (event.getEntity() instanceof Player) {
            Player player = (Player)event.getEntity();
            UUID playerID = player.getUniqueId();
            if ((player.getHealth() - event.getFinalDamage()) <= 0) {
                if (isInCourse.get(playerID) || isInPracticeMode.get(playerID)) {
                    event.setCancelled(true);
                    player.setHealth(20);
                    player.setSaturation(20);
                    HashMap respawnPoints = context.get("respawnPoints");
                    player.teleport((Location) respawnPoints.get(playerID));
                    player.setFireTicks(0);
                    if (isInCourse.get(playerID)) {
                        player.sendMessage(ChatColor.AQUA + "You fell! Returning to checkpoint.");
                        attempts.put(playerID, (int) attempts.get(playerID) + 1);
                    } else {
                        player.sendMessage(ChatColor.AQUA + "You fell! Returning to spawnpoint.");
                    }

                }
            }
        }
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        HashMap<UUID, Boolean> isInCourse = context.get("isInCourse");
        HashMap<UUID, Boolean> isInPracticeMode = context.get("isInPracticeMode");
        HashMap<UUID, Number> attempts = context.get("attempts");
        Player player = event.getEntity();
        UUID playerID = player.getUniqueId();
        if (isInCourse.get(playerID)) {
            player.sendMessage(ChatColor.AQUA + "You died! Returning to checkpoint.");
            attempts.put(playerID, (int)attempts.get(playerID) + 1);
        } else if (isInPracticeMode.get(playerID)) {
            player.sendMessage(ChatColor.AQUA + "You died! Returning to spawnpoint.");
        }
    }
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID playerID = player.getUniqueId();
        HashMap<UUID, Boolean> isInCourse = context.get("isInCourse");
        HashMap<UUID, Boolean> isInPracticeMode = context.get("isInPracticeMode");
        HashMap respawnPoints = context.get("respawnPoints");
        if (isInCourse.get(playerID) || isInPracticeMode.get(playerID)) {
            event.setRespawnLocation((Location) respawnPoints.get(playerID));
        }
    }
    // hotbar
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        HashMap<UUID, Number> attempts = context.get("attempts");
        HashMap<UUID, Number> eventCooldown = context.get("eventCooldown");
        HashMap<UUID, Boolean> isInCourse = context.get("isInCourse");
        HashMap<UUID, Boolean> isInPracticeMode = context.get("isInPracticeMode");
        HashMap respawnPoints = context.get("respawnPoints");
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        UUID playerID = player.getUniqueId();
        if (event.hasItem() && (int)eventCooldown.get(playerID) == 0) {
            String itemName = item.getItemMeta().getDisplayName();
            if (itemName == null) {
                return;
            }
            if (isInCourse.get(playerID)) {
                if (item.getType() == Material.COMPASS && itemName.equals("Return to Checkpoint")) {
                    event.setCancelled(true);
                    if (respawnPoints.get(playerID) == null) {
                        player.sendMessage(ChatColor.AQUA + "You shouldn't be able to see this!");
                    } else {
                        attempts.put(playerID, (int) attempts.get(playerID) + 1);
                        player.teleport((Location) respawnPoints.get(playerID));
                    }
                } else if (item.getType() == Material.ACACIA_DOOR_ITEM && itemName.equals("Quit current Parkour")) {
                    event.setCancelled(true);
                    Utils.resetCourse(context, player);
                    player.getInventory().clear();
                    player.sendMessage(ChatColor.AQUA + "You quit the current parkour!");
                }
            } else if (isInPracticeMode.get(playerID)) {
                if (item.getType() == Material.WORKBENCH && itemName.equals("Set spawnpoint")) {
                    event.setCancelled(true);
                    Location pLocation = player.getLocation();
                    respawnPoints.put(playerID, pLocation);
                    player.sendMessage(ChatColor.AQUA + "Set spawnpoint to " + pLocation.getBlockX() + " " + pLocation.getBlockY() + " " + pLocation.getBlockZ() + " with a yaw of " + ((Math.round(pLocation.getYaw()) * 10.0)/10.0) + " degrees.");
                } else if (item.getType() == Material.COMPASS && itemName.equals("Return to spawnpoint")) {
                    event.setCancelled(true);
                    if (respawnPoints.get(playerID) == null) {
                        player.sendMessage(ChatColor.AQUA + "You shouldn't be able to see this!");
                    } else {
                        player.teleport((Location) respawnPoints.get(playerID));
                    }
                }
            }
            eventCooldown.put(playerID, 1);
        }
    }
    // setting playerdata
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Utils.initPlayerData(context, player);
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        HashMap<UUID, Boolean> isInCourse = context.get("isInCourse");
        HashMap<UUID, Boolean> isInPracticeMode = context.get("isInPracticeMode");
        if (isInCourse.get(player.getUniqueId()) || isInPracticeMode.get(player.getUniqueId())) {
            player.getInventory().clear();
        }
        Utils.initPlayerData(context, player);
    }
}
