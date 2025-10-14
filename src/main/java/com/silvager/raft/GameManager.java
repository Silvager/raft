package com.silvager.raft;

import com.mojang.brigadier.Command;
import com.silvager.raft.events.TsunamiEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.*;
import org.bukkit.event.HandlerList;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class GameManager {
    public static World raftWorld;
    public static World raftEndWorld;
    public static ArrayList<BukkitTask> tasks = new ArrayList<>();
    static final Material[] bannedMaterials =
            {Material.AIR, Material.OBSIDIAN, Material.CRYING_OBSIDIAN, Material.LAVA_BUCKET,
                    Material.END_PORTAL_FRAME, Material.END_PORTAL_FRAME, Material.NETHER_PORTAL, Material.ENDER_DRAGON_SPAWN_EGG};

    static Material[] allMaterials = Arrays.stream(Material.values())
            .filter(Material::isItem)
            .filter(m -> Arrays.stream(bannedMaterials).noneMatch(b -> b == m))
            .toArray(Material[]::new);
    public static Location oceanSpawn;
    private static boolean isRunning = false;
    private static RaftListeners raftListeners;
    public static void startGame() {
        if (isRunning) {
            return;
        }
        tasks.forEach(bukkitTask -> bukkitTask.cancel());
        tasks.clear();
    startCurrentSystem();
    startItemDropping();
    setupPlayers();
    raftListeners = new RaftListeners();
    Raft.registerListener(raftListeners);
    // NEED TO REACTIVATE
//    RaftEvents.startEvents();
    GameManager.isRunning = true;
    }
    public static boolean getIsRunning() {
        return isRunning;
    }
    public static void stopSystems() {
        tasks.forEach(bukkitTask -> bukkitTask.cancel());
        tasks.clear();
        // Remove the raft listeners
        HandlerList.unregisterAll(raftListeners);
        TsunamiEvent.cancelTsunamiIfRunning();
        GameManager.isRunning = false;
    }
    static void setupPlayers() {
        //Move all server players into the world and set them up
        Collection<Player> players = (Collection<Player>) Raft.getInstance().getServer().getOnlinePlayers();
        players.forEach((player -> {
            player.teleportAsync(oceanSpawn);
            player.setHealth(20);
            player.setGameMode(GameMode.SURVIVAL);

        }));
    }
    //Ran on server start
    static void setupWorlds() {
        raftWorld = null;
        // If there is a world currently saved, delete it
        World foundWorld = Bukkit.getWorld("raftWorld");
        if (foundWorld == null) {
            WorldCreator wc = new WorldCreator("raftWorld");
            wc.generator(new OceanWorldGen());
            wc.biomeProvider(new SingleBiomeProvidor(Biome.OCEAN));
            wc.environment(World.Environment.NORMAL);
            raftWorld = Bukkit.createWorld(wc);
        }

        assert raftWorld != null;
        raftWorld.setAutoSave(true);
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

        raftEndWorld = null;
        World foundEndWorld = Bukkit.getWorld("raftEndWorld");
        if (foundEndWorld == null) {
            WorldCreator wc = new WorldCreator("raftEndWorld");
            wc.generator(new OceanEndGen());
            wc.biomeProvider(new SingleBiomeProvidor(Biome.THE_END));
            wc.environment(World.Environment.THE_END);
            raftEndWorld = Bukkit.createWorld(wc);
            EndFightStuff.setupEndCrystals();
        }
        assert raftEndWorld != null;
        raftEndWorld.setAutoSave(true);
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
