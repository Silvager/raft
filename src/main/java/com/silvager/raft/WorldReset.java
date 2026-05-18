package com.silvager.raft;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Comparator;

public class WorldReset {
    public static void resetWorld() {
        GameManager.stopSystems();
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        File raftWorldFolder =GameManager.raftWorld.getWorldFolder();
        File raftEndWorldFolder =GameManager.raftEndWorld.getWorldFolder();
        players.forEach((player -> {
            player.setGameMode(GameMode.SPECTATOR);
            player.teleportAsync(GameManager.oceanSpawn);
            player.getInventory().clear();
            player.kick(Component.text("Raft is resetting- rejoin shortly"));
        }));
        Utils.runLater(() -> {
            boolean raftWorldUnloaded = Bukkit.unloadWorld(GameManager.raftWorld, false);
            boolean endWorldUnloaded = Bukkit.unloadWorld(GameManager.raftEndWorld, false);
            if (!raftWorldUnloaded || !endWorldUnloaded) {
                Utils.logInfo("FAILED TO DELETE WORLDS");
            }
            GameManager.raftWorld = null;
            GameManager.raftEndWorld = null;
        }, 5);
        // Run later to give time for world unloading
        Utils.runLater(() -> {
            deleteWorld(raftWorldFolder);
            deleteWorld(raftEndWorldFolder);
            if (raftWorldFolder.exists() || raftEndWorldFolder.exists()) {
                Utils.logInfo("FAILED TO DELETE WORLD FOLDERS");
            }
        }, 10);
        Utils.runLater(() -> {
            GameManager.setupWorlds();
            // Re-get the pdc of the new world
            PlayerJoining.setupPlayerJoining();
            // Make it so people can get fishing rod again
            PlayerJoining.resetListOfFishingRodRecievers();
        }, 15);
    }

    public static void deleteWorld(File path) {
        try {
            Files.walk(path.toPath())
                    .sorted(Comparator.reverseOrder()) // delete files before directories
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            Bukkit.getLogger().warning("Failed to delete " + p + ": " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            Bukkit.getLogger().severe("Error walking world directory: " + e.getMessage());
        }
    }

    public static void deleteWorld(World world) {
        WorldReset.deleteWorld(world.getWorldFolder());
    }

}
