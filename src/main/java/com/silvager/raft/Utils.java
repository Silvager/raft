package com.silvager.raft;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import org.bukkit.entity.Player;

public class Utils {
    public static void runLater(Runnable fxn, long delay) {
        Raft.scheduler.runTaskLater(Raft.getInstance(), fxn, delay);
    }
    public static Player getPlayerFromAudience(Audience audience) {
        String playerName = audience.get(Identity.NAME).orElse("");
        return Raft.getInstance().getServer().getPlayer(playerName); // I know this might be null
    }
}
