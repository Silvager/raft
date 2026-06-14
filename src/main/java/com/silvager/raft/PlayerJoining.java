package com.silvager.raft;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerJoining implements Listener {
    private static PersistentDataContainer pdc;
    static NamespacedKey key = new NamespacedKey(Raft.getInstance(), "prev-players");

    public static void setupPlayerJoining() {
        pdc = GameManager.raftWorld.getPersistentDataContainer();

    }
    // The respawn is here just in case someone manages to die before the game starts
    @EventHandler
    public static void onPlayerRespawn(PlayerRespawnEvent event) {
        if (GameManager.oceanSpawn != null) {
            event.setRespawnLocation(GameManager.oceanSpawn);
        }
    }
    @EventHandler
    public static void onPlayerConnect(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (GameManager.raftWorld == null) {
            player.kick(Component.text("Raft is setting up- wait a sec"));
            return;
        }

        List<String> prevPlayerNames = new ArrayList<>();
        List<String> pdcValue = pdc.get(key, PersistentDataType.LIST.strings());
        if (pdcValue != null) {
            prevPlayerNames.addAll(pdcValue);
        }
        // IF its the first player on a brand new world, or they are not listed, clear them and give rod
        if (prevPlayerNames.isEmpty() || !prevPlayerNames.contains(player.getName())) {
            player.getInventory().clear();
            player.give(new ItemStack(Material.FISHING_ROD));
            player.setHealth(Objects.requireNonNull(player.getAttribute(Attribute.MAX_HEALTH)).getBaseValue());
            player.setFoodLevel(20);
            player.setSaturation(20f);

            // Add them to the list so they will not be cleared if they join again unless raft is reset
            if (prevPlayerNames.isEmpty()) {
                List<String> newList = new ArrayList<>();
                newList.add(player.getName());
                pdc.set(key, PersistentDataType.LIST.strings(), newList);
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
