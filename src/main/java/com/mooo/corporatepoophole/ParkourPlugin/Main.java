package com.mooo.corporatepoophole.ParkourPlugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class Main extends JavaPlugin {
    private HashMap<String, HashMap> context;
    @Override
    public void onEnable() {
        context = new HashMap<String, HashMap>();
        context.put("isInParkourMode", new HashMap<UUID, Boolean>());
        context.put("isInPracticeMode", new HashMap<UUID, Boolean>());
        context.put("isInCourse", new HashMap<UUID, Boolean>());
        context.put("attempts", new HashMap<UUID, Number>());
        context.put("times", new HashMap<UUID, Number>());
        context.put("respawnPoints", new HashMap<>());
        context.put("eventCooldown", new HashMap<UUID, Number>());
        getServer().getPluginManager().registerEvents(new Listeners(context), this);
        BukkitTask task = new TimerTask(this, context).runTaskTimerAsynchronously(this, 0, 20);

        for (Player p : Bukkit.getOnlinePlayers()) {
            Utils.initPlayerData(context, p);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return Commands.onCommand(sender, cmd, label, args, this, context);
    }
}
