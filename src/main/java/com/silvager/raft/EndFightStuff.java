package com.silvager.raft;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.awt.*;
import java.time.Duration;
import java.util.List;

public class EndFightStuff implements Listener {
    private static EndFightStuff instance;
    public static void endFightStarted() {
        instance = new EndFightStuff();
     startEndCurrentSystem();
     Raft.registerListener(instance);
    }
    static void startEndCurrentSystem() {
        final Vector currentVector = new Vector(0, 1.0, 0.0);
        GameManager.tasks.add(Raft.scheduler.runTaskTimer(Raft.getInstance(), () -> {
            // Iterate through every world loaded on the server
            // For each world, iterate through all entities within it
            GameManager.raftEndWorld.getEntities().forEach(entity -> {
                // Check if the entity is in water. This works for all entity types.
                if (entity.isInWater()) {
                    if (entity.getType() == EntityType.FISHING_BOBBER || entity.getType() == EntityType.DROWNED) {
                        return;
                    }
                    if (entity.getType() == EntityType.PLAYER) {
                        if (((Player) entity).getGameMode() != GameMode.SURVIVAL) {
                            return;
                        }
                    }
                    Vector newVelocity = entity.getVelocity();
                    newVelocity.add(currentVector);
                    entity.setVelocity(newVelocity);
                    if (entity.getType().isAlive()) {
                        LivingEntity livingEntity = (LivingEntity) entity;
                        livingEntity.damage(2);
                    }
                    else {
                        entity.remove();
                    }

                }
            });
        }, 0L, 5L));
    }
    static void setupEndCrystals() {
        int quantity = 8;
        int crystalDistance = 40;
        double increaseAmount = Math.PI*2 / quantity;
        for (double angle =0;angle<Math.PI*2; angle+=increaseAmount) {
            int x = (int) Math.floor(Math.sin(angle)*crystalDistance);
            int z = (int) Math.floor(Math.cos(angle)*crystalDistance);
            int y = Raft.random.nextInt(66, 90);
            GameManager.raftEndWorld.getBlockAt(x, y, z).setType(Material.OBSIDIAN);
            GameManager.raftEndWorld.spawn(new Location(GameManager.raftWorld, x+0.5, y+2, z+0.5), EnderCrystal.class);
            // Put glass protection around
            if (Raft.random.nextInt(0, 3) == 0) {
                Material glassMaterial = switch (Raft.random.nextInt(0, 3)) {
                    case 0 -> Material.BLUE_STAINED_GLASS;
                    case 1 -> Material.PURPLE_STAINED_GLASS;
                    case 2 -> Material.LIGHT_BLUE_STAINED_GLASS;
                    default -> Material.PINK_STAINED_GLASS;
                };
                Vector midPos = new Vector(x, y, z);
                for (int x2 = x-5; x2<x+5; x2++) {
                    for (int y2 = y-5; y2<y+5; y2++) {
                        for (int z2 = z-5; z2 <z+5; z2++) {
                            Vector pos = new Vector(x2, y2, z2);
                            double dist = pos.distance(midPos);
                            if (dist > 4 && dist < 6) {
                                GameManager.raftEndWorld.getBlockAt(x2, y2, z2).setType(glassMaterial);
                            }
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public static void onEntityDie(EntityDeathEvent event) {
        if (event.getEntity().getType() != EntityType.ENDER_DRAGON) return;
        // Show the win screen
        List<Player> players = GameManager.raftWorld.getPlayers();
        players.addAll(GameManager.raftEndWorld.getPlayers());
        Title title = Title.title(
                net.kyori.adventure.text.Component.text("YOU WIN!!!").color(NamedTextColor.GREEN),
                net.kyori.adventure.text.Component.text("Coungradulations on beating Raft").color(NamedTextColor.GOLD),
                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(7), Duration.ofMillis(500))
        );
        ItemStack riprideTrident = new ItemStack(Material.TRIDENT);
        riprideTrident.addUnsafeEnchantment(Enchantment.RIPTIDE, 2);
        ItemMeta meta = riprideTrident.getItemMeta();
        meta.itemName(Component.text("Victory Trident").color(NamedTextColor.LIGHT_PURPLE));
        riprideTrident.setItemMeta(meta);
        players.forEach(player -> {
            player.showTitle(title);
            player.give(riprideTrident);
        });
        RaftMusic.playSong(RaftSongs.RICKROLL);
        HandlerList.unregisterAll(instance);
    }
}
