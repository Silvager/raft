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
        // Copy the default config if no config preset
        saveDefaultConfig();

        boolean noteblockApiInstalled = true;
        if (!Bukkit.getPluginManager().isPluginEnabled("NoteBlockAPI")) {
            getLogger().warning("*** NoteBlockAPI is not installed or is not enabled- Raft will work fine, but you won't hear music ***");
            noteblockApiInstalled = false;
        }
        RaftMusic.preloadMusic(noteblockApiInstalled);

        //Events need to be initialized before commands
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
