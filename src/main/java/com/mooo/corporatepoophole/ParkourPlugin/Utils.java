package com.mooo.corporatepoophole.ParkourPlugin;

import io.github.theluca98.textapi.ActionBar;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class Utils {
    public static void initPlayerData(HashMap<String, HashMap> context, Player player) {
        HashMap<UUID, Boolean> isInMode = context.get("isInParkourMode");
        HashMap<UUID, Boolean> isInPracticeMode = context.get("isInPracticeMode");
        HashMap<UUID, Boolean> isInCourse = context.get("isInCourse");
        HashMap<UUID, Number> attempts = context.get("attempts");
        HashMap<UUID, Number> times = context.get("times");
        HashMap respawnPoints = context.get("respawnPoints");
        HashMap<UUID, Number> eventCooldown = context.get("eventCooldown");

        UUID playerID = player.getUniqueId();
        isInMode.put(playerID, false);
        isInPracticeMode.put(playerID, false);
        isInCourse.put(playerID, false);
        times.put(playerID, 0);
        respawnPoints.put(playerID, null);
        attempts.put(playerID, 0);
        eventCooldown.put(playerID, 0);
        Utils.clearActionBar(player);
    }
    public static void resetCourse(HashMap<String, HashMap> context, Player player) {
        HashMap<UUID, Boolean> isInCourse = context.get("isInCourse");
        HashMap<UUID, Number> attempts = context.get("attempts");
        HashMap<UUID, Number> times = context.get("times");
        HashMap respawnPoints = context.get("respawnPoints");

        UUID playerID = player.getUniqueId();
        isInCourse.put(playerID, false);
        times.put(playerID, 0);
        respawnPoints.put(playerID, null);
        attempts.put(playerID, 0);
        Utils.clearActionBar(player);
    }
    public static void clearActionBar(Player player) {
        ActionBar bar = new ActionBar("");
        bar.send(player);
    }
}
