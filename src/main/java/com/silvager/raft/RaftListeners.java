package com.silvager.raft;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RaftListeners implements Listener {
    @EventHandler
    public static void onPlayerRespawn(PlayerRespawnEvent event) {
        if (GameManager.oceanSpawn != null) {
            event.setRespawnLocation(GameManager.oceanSpawn);
        }

    }
}
