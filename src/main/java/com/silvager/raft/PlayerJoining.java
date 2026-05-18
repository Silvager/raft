package com.silvager.raft;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class PlayerJoining implements Listener {
    private static PersistentDataContainer pdc;
    static NamespacedKey key = new NamespacedKey(Raft.getInstance(), "prev-players");

    public static void setupPlayerJoining() {
        pdc = GameManager.raftWorld.getPersistentDataContainer();

    }
    @EventHandler
    public static void onPlayerConnect(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (GameManager.raftWorld == null) {
            player.kick(Component.text("Raft is setting up- wait a sec"));
        }

        List<String> prevPlayerNames = pdc.get(key, PersistentDataType.LIST.strings());
        // IF its the first player on a brand new world, or they are not listed, clear them and give rod
        if (prevPlayerNames == null || !prevPlayerNames.contains(player.getName())) {
            player.getInventory().clear();
            player.give(new ItemStack(Material.FISHING_ROD));
            // Add them to the list so they will not be cleared if they join again unless raft is reset
            if (prevPlayerNames == null) {
                pdc.set(key, PersistentDataType.LIST.strings(), List.of(player.getName()));
            } else {
                prevPlayerNames.add(player.getName());
                pdc.set(key, PersistentDataType.LIST.strings(), prevPlayerNames);
            }
        }
        
        if (GameManager.getIsRunning()) {
            player.setGameMode(GameMode.SURVIVAL);
            player.teleportAsync(GameManager.oceanSpawn);
        } else {
            player.setGameMode(GameMode.SPECTATOR);
            player.teleportAsync(GameManager.oceanSpawn);
        }
    }
    public static void resetListOfFishingRodRecievers() {
        pdc.set(key, PersistentDataType.LIST.strings(), new ArrayList<>());
    }
}
