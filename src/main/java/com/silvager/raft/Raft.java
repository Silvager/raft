package com.silvager.raft;

import com.silvager.raft.events.AmongUsEvent;
import com.silvager.raft.events.SharkEvent;
import io.papermc.paper.event.entity.EntityPushedByEntityAttackEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

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
        RaftEvents.initializeEvents();
        RaftCommands.registerCommands();
        AmongUsEvent.preloadSussySong();
        SharkEvent.preloadSkibidiSong();
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
