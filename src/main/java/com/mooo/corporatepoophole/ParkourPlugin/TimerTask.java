package com.mooo.corporatepoophole.ParkourPlugin;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import io.github.theluca98.textapi.ActionBar;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TimerTask extends BukkitRunnable {
    private final JavaPlugin plugin;
    private HashMap<String, HashMap> context;
    public TimerTask(JavaPlugin plugin, HashMap<String, HashMap> context) {
        this.plugin = plugin;
        this.context = context;
    }
    @Override
    public void run() {
        HashMap<UUID, Number> times = (HashMap<UUID, Number>)context.get("times");
        HashMap<UUID, Number> attempts = (HashMap<UUID, Number>)context.get("attempts");
        HashMap<UUID, Boolean> isInCourse = (HashMap<UUID, Boolean>)context.get("isInCourse");
        HashMap<UUID, Number> eventCooldown = context.get("eventCooldown");

        ActionBar bar = new ActionBar("");
        bar.sendToAll();
        for (Map.Entry<UUID, Number> time : times.entrySet()) {
            if (isInCourse.get(time.getKey())) {
                time.setValue((int)time.getValue() + 1);
                String interpretedTime;
                int minute = (int)time.getValue() / 60;
                int second = (int)time.getValue() % 60;
                interpretedTime = String.format("%02d", minute) + ":" + String.format("%02d", second);

                bar = new ActionBar(ChatColor.AQUA + "Time spent: " + interpretedTime + " | Fails: " + attempts.get(time.getKey()));
                bar.send(plugin.getServer().getPlayer(time.getKey()));
            }
        }
        for (Map.Entry<UUID, Number> users : eventCooldown.entrySet()) {
            if ((int)users.getValue() > 0) {
                users.setValue((int)users.getValue() - 1);
            }
        }
    }
}
