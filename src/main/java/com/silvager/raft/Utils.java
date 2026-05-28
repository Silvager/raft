package com.silvager.raft;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static BukkitTask runLater(Runnable fxn, long delay) {
        return Raft.scheduler.runTaskLater(Raft.getInstance(), fxn, delay);
    }
    public static void logInfo(String info) {
        Raft.getInstance().getLogger().info(info);
    }
    public static Player getPlayerFromAudience(Audience audience) {
        String playerName = audience.get(Identity.NAME).orElse("");
        return Raft.getInstance().getServer().getPlayer(playerName); // I know this might be null
    }
    public static Player getRandomPlayerOrNull() {
        if (Bukkit.getOnlinePlayers().isEmpty()) {
            return null;
        }
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        Player player;
        if (players.size() > 1) {
            player = players.get(Raft.random.nextInt(players.size()));
        } else {
            player = players.getFirst();
        }
        return player;
    }
    public static boolean doesWorldExistOnDisk(NamespacedKey worldKey) {
        if (Bukkit.getWorld(worldKey) != null) {
            return true;
        }
        File worldFolder = new File(Bukkit.getServer().getLevelDirectory().resolve("dimensions").resolve(worldKey.getNamespace()).toFile(), worldKey.value());
        return worldFolder.exists();
    }
}
