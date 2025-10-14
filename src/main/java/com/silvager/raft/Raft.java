package com.silvager.raft;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Random;

public final class Raft extends JavaPlugin {

    private static Raft instance;
    public static BukkitScheduler scheduler;
    public static Random random = new Random();
    public void onEnable() {
        // Plugin startup logic
        Raft.instance = this;
        scheduler = this.getServer().getScheduler();
        //Events need to be initialized before commands
        RaftMusic.preloadMusic();
        RaftEvents.initializeEvents();
        RaftCommands.registerCommands();
        GameManager.setupWorlds();
        PlayerJoining.setupPlayerJoining();
        Raft.registerListener(new PlayerJoining());
    }
    public static Raft getInstance() {
        return Raft.instance;
    }
    public static void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, Raft.getInstance());
    }
    @Override
    public void onDisable() {
        if (GameManager.getIsRunning()) {
            GameManager.stopSystems();
        }
    }
}
