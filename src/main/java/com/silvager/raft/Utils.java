package com.silvager.raft;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

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
}
