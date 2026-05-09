package com.silvager.raft;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
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
        players.forEach((player -> {
            player.getInventory().clear();
            player.kick(Component.text("Raft is resetting- rejoin shortly"));
        }));

        Bukkit.unloadWorld(GameManager.raftWorld, false);
        Bukkit.unloadWorld(GameManager.raftEndWorld, false);
        deleteWorld(GameManager.raftWorld);
        deleteWorld(GameManager.raftEndWorld);

        GameManager.setupWorlds();
        // Re-get the pdc of the new world
        PlayerJoining.setupPlayerJoining();
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
