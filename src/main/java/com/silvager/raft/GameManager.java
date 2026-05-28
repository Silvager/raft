package com.silvager.raft;

import com.silvager.raft.events.BubbleEvent;
import com.silvager.raft.events.SharkEvent;
import com.silvager.raft.events.TsunamiEvent;
import com.silvager.raft.worldGenerators.OceanEndGen;
import com.silvager.raft.worldGenerators.OceanWorldGen;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.entity.*;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.checkerframework.checker.units.qual.N;

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
    if (Raft.getInstance().getConfig().getBoolean("enable-events")) {
        RaftEvents.startEvents();
    }
    GameManager.isRunning = true;
    }
    public static boolean getIsRunning() {
        return isRunning;
    }
    public static void stopSystems() {
        tasks.forEach(BukkitTask::cancel);
        tasks.clear();
        // Remove the raft listeners
        HandlerList.unregisterAll(raftListeners);
        // Clean up events that won't cleanly end
        TsunamiEvent.cancelTsunamiIfRunning();
        SharkEvent.stopSharkEventIfRunning();
        BubbleEvent.endBubbleEventIfRunning();
        GameManager.isRunning = false;
    }
    static void setupPlayers() {
        //Move all server players into the world and set them up
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        players.forEach((player -> {
            player.teleportAsync(oceanSpawn);
            player.setHealth(20);
            player.setGameMode(GameMode.SURVIVAL);
        }));
    }
    //Ran on server start
    //The server runs it one tick later so worlds will actually be loaded
    static void setupWorlds() {
        NamespacedKey raftWorldKey = new NamespacedKey("raft", "raftworld");
        raftWorld = Bukkit.getWorld(raftWorldKey);
        WorldCreator wc = new WorldCreator(raftWorldKey);
        wc.generator(new OceanWorldGen());
        wc.biomeProvider(new SingleBiomeProvidor(Biome.OCEAN));
        wc.environment(World.Environment.NORMAL);
        raftWorld = Bukkit.createWorld(wc);
        assert raftWorld != null;

        raftWorld.setAutoSave(true);
        raftWorld.setGameRule(GameRules.SPAWN_MOBS, false);
        raftWorld.setGameRule(GameRules.SPAWN_MONSTERS, false);
        raftWorld.setGameRule(GameRules.SPAWN_PHANTOMS, false);
        raftWorld.setViewDistance(5);
        raftWorld.setSimulationDistance(5);
        raftWorld.setTime(1000L);
        oceanSpawn = new Location(raftWorld, 7.5, 32, 7.5);
        raftWorld.setSpawnLocation(oceanSpawn);
        raftWorld.setDifficulty(Difficulty.HARD);
        raftWorld.setStorm(false);
        raftWorld.setThundering(false);
        raftWorld.setClearWeatherDuration(20*20);
        WorldBorder worldBorder = raftWorld.getWorldBorder();
        worldBorder.setCenter(oceanSpawn);
        worldBorder.setSize(120);
        worldBorder.setDamageAmount(2);
        worldBorder.setDamageBuffer(3);


        NamespacedKey raftEndWorldKey = new NamespacedKey("raft", "raftendworld");
        boolean isEndWorldAlreadyCreated = Utils.doesWorldExistOnDisk(raftEndWorldKey);
        raftEndWorld = Bukkit.getWorld(raftEndWorldKey);
        WorldCreator wce = new WorldCreator(raftEndWorldKey);
        wce.generator(new OceanEndGen());
        wce.biomeProvider(new SingleBiomeProvidor(Biome.THE_END));
        wce.environment(World.Environment.THE_END);
        raftEndWorld = Bukkit.createWorld(wce);
        assert raftEndWorld != null;
        raftEndWorld.setAutoSave(true);
        raftEndWorld.setGameRule(GameRules.SPAWN_PHANTOMS, false);

        //This should only run if the world is being loaded/created for the first time
        if (!isEndWorldAlreadyCreated) {
            EndFightStuff.setupEndCrystals();
        }

    }

    static void startCurrentSystem() {
        double currentSpeed = Raft.getInstance().getConfig().getDouble("ocean-current-speed");
        if (currentSpeed < 0.01) currentSpeed = 0.01;
        if (currentSpeed > 100) currentSpeed = 100;
        final Vector currentVector = new Vector(currentSpeed/10.0, 0, 0.0);
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

                                entity.setVelocity(velocity.add(currentVector));
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
        double itemSpawnFrequency = Raft.getInstance().getConfig().getDouble("item-spawn-frequency");
        if (itemSpawnFrequency < 0.01) itemSpawnFrequency = 0.01;
        if (itemSpawnFrequency > 100.0) itemSpawnFrequency = 100.0;
        tasks.add(Raft.scheduler.runTaskTimer(Raft.getInstance(), () -> {
            Location spawnLocation = new Location(raftWorld, -51, 30, Raft.random.nextDouble(-51, 65));
            Item item = raftWorld.spawn(spawnLocation, Item.class);
            Material itemMaterial = allMaterials[Raft.random.nextInt(0, allMaterials.length)];
            try {
                int amount = itemMaterial.getMaxStackSize() == 1 ? 1 : Raft.random.nextInt(1, 5);
                item.setItemStack(new ItemStack(itemMaterial, amount));
            } catch (IllegalArgumentException ignored){}
        // Because a spawn every 15 ticks is the default, it will multiply the config number by 15
        }, 0L, (Math.round(itemSpawnFrequency * 15))));
    }
}
