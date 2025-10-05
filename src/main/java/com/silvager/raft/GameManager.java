package com.silvager.raft;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class GameManager {
    public static World raftWorld;
    public static ArrayList<BukkitTask> tasks = new ArrayList<>();
    static Material[] allMaterials = Arrays.stream(Material.values())
            .filter(Material::isItem)
            .filter(m -> m != Material.AIR)
            .toArray(Material[]::new);
    public static Location oceanSpawn;
    private static boolean isRunning = false;
    public static void startGame() {
    setupWorld();
    startCurrentSystem();
    startItemDropping();
    Raft.registerListener(new RaftListeners());
//    RaftEvents.startEvents();
    GameManager.isRunning = true;
    }
    public static boolean getIsRunning() {
        return isRunning;
    }
    static void setupWorld() {
        raftWorld = null;
        tasks.forEach(bukkitTask -> bukkitTask.cancel());
        tasks.clear();
        // If there is a world currently saved, delete it
        World foundWorld = Bukkit.getWorld("raftWorld");
        if (foundWorld != null) {
            // Find the spawn of the normal world
            World mainWorld = Bukkit.getWorld("world");
            foundWorld.getPlayers().forEach((player -> player.teleportAsync(mainWorld.getSpawnLocation())));
            Bukkit.getServer().unloadWorld("raftWorld", false);
        }
        //Create or recreate the world
        WorldCreator wc = new WorldCreator("raftWorld");
        wc.generator(new OceanWorldGen());
        wc.biomeProvider(new OceanBiomeProvidor());
        raftWorld = Bukkit.createWorld(wc);
        raftWorld.setAutoSave(false);
        raftWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        raftWorld.setViewDistance(5);
        raftWorld.setSimulationDistance(5);
        raftWorld.setTime(1000L);
        oceanSpawn = new Location(raftWorld, 7, 32, 7);
        raftWorld.setSpawnLocation(oceanSpawn);
        raftWorld.setDifficulty(Difficulty.HARD);
        WorldBorder worldBorder = raftWorld.getWorldBorder();
        worldBorder.setCenter(oceanSpawn);
        worldBorder.setSize(120);
        worldBorder.setDamageAmount(2);
        worldBorder.setDamageBuffer(3);
        //Move all server players into the world and set them up
        Collection<Player> players = (Collection<Player>) Raft.getInstance().getServer().getOnlinePlayers();
        players.forEach((player -> {
            player.teleportAsync(oceanSpawn);
            player.setHealth(20);
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().clear();
            player.give(new ItemStack(Material.FISHING_ROD));
        }));
    }
    static void startCurrentSystem() {
        final Vector currentVector = new Vector(0.1, 0, 0.0);
        final Vector playerVector = new Vector(0.1, 0.0, 0.0);
        tasks.add(Raft.scheduler.runTaskTimer(Raft.getInstance(), () -> {
            // Iterate through every world loaded on the server
                // For each world, iterate through all entities within it
                raftWorld.getEntities().forEach(entity -> {
                    // Check if the entity is in water. This works for all entity types.
                    if (entity.isInWater()) {
                        if (entity.getType() == EntityType.FISHING_BOBBER || entity.getType() == EntityType.DROWNED) {
                            return;
                        }
                        if (entity.getType() != EntityType.PLAYER) {
                            entity.setVelocity(currentVector);
                        } else {
                            Player player = (Player) entity;
                            if (player.getGameMode() == GameMode.SURVIVAL) {
                                Vector velocity = entity.getVelocity();

                                entity.setVelocity(velocity.add(playerVector));
                            }
                        }
                        if (entity.getType() == EntityType.ITEM) {
                            // Equal to a minute and a half
                            if (entity.getTicksLived() > 1800) {
                                entity.remove();
                            }
                        }
                    }
                    if (entity.getLocation().getX() > 58) {
                        if (entity.getType().isAlive()) {
                            LivingEntity livingEntity = (LivingEntity) entity;
                            livingEntity.damage(3);
                        }
                        else {
                            entity.remove();
                        }
                    }
                });
        }, 0L, 5L));
    }
    static void startItemDropping() {
        tasks.add(Raft.scheduler.runTaskTimer(Raft.getInstance(), () -> {
            Location spawnLocation = new Location(raftWorld, -51, 30, Raft.random.nextDouble(-51, 65));
            Item item = raftWorld.spawn(spawnLocation, Item.class);
            Material itemMaterial = allMaterials[Raft.random.nextInt(0, allMaterials.length)];
            try {
                int amount = itemMaterial.getMaxStackSize() == 1 ? 1 : Raft.random.nextInt(1, 5);
                item.setItemStack(new ItemStack(itemMaterial, amount));
            } catch (IllegalArgumentException ignored){}

        }, 0L, 15L));
    }
}
