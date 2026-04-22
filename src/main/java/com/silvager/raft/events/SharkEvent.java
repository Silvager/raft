package com.silvager.raft.events;

import com.silvager.raft.Raft;
import com.silvager.raft.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Dolphin;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.silvager.raft.GameManager.raftWorld;

public class SharkEvent implements Listener {
    private static SharkEvent instance;

    private static SharkEvent getInstance() {
        if (instance == null) instance = new SharkEvent();
        return instance;

    }

    private static final int MOVE_DELAY = 5;
    private static final int EAT_DELAY = 10;
    private static Vector moveVector = new Vector(1, 0, 0);
    private static Dolphin dolphin;
    private static boolean isEating = false;
    private static BukkitTask itteratorTask;
    static private ArrayList<BlockFace> allBlockFaces = new ArrayList<>(List.of(new BlockFace[]{
            BlockFace.UP, BlockFace.DOWN, BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH
    }));
    public static void startSharkEvent() {
        isEating = false;
        if (dolphin != null) {
            if (!dolphin.isDead()) {
                dolphin.setHealth(0);
                HandlerList.unregisterAll(getInstance());
            }
        }

        if (itteratorTask != null) {
            if (!itteratorTask.isCancelled()) {
                itteratorTask.cancel();
            }
        }
        Raft.registerListener(getInstance());

        int currentX = -51;
        Location startLocation = new Location(raftWorld, currentX, 29.5, Raft.random.nextInt(-21, 35));
        Dolphin dolphin = raftWorld.spawn(startLocation, Dolphin.class);
        dolphin.setAI(false);
        dolphin.setCollidable(false);
        dolphin.setGravity(false);
        dolphin.setRotation(-90, 0);
        dolphin.setCanPickupItems(false);
        dolphin.setMaximumAir(10000);
        dolphin.setRemainingAir(10000);
        SharkEvent.dolphin = dolphin;
        dolphin.setVelocity(new Vector(5, 0, 0));
        Utils.runLater(SharkEvent::dolphinItterator, MOVE_DELAY);
    }
    private static void dolphinItterator() {
        if (dolphin.isDead()) {
            HandlerList.unregisterAll(getInstance());
            return;
        }
        if (isEating) {
            Collections.shuffle(allBlockFaces);
            for (BlockFace allBlockFace : allBlockFaces) {
                Block neiboor = raftWorld.getBlockAt(dolphin.getLocation()).getRelative(allBlockFace, 1);
                if (neiboor.getType() != Material.AIR && neiboor.getType() != Material.WATER && neiboor.getType() != Material.BEDROCK) {
                    Location newLocation = neiboor.getLocation().toCenterLocation();
                    Vector direction = newLocation.toVector().subtract(dolphin.getLocation().toVector());
                    newLocation.setDirection(direction);
                    dolphin.teleport(newLocation);
                    raftWorld.getBlockAt(newLocation).breakNaturally();
                    raftWorld.playSound(dolphin.getLocation(), Sound.ENTITY_PLAYER_BURP, 2f, 1f);
                    itteratorTask = Utils.runLater(SharkEvent::dolphinItterator, EAT_DELAY);
                    return;
                }
            }
            // It did not find any more tasty morsels
            isEating = false;
            dolphin.setRotation(-90, 0);
            if (dolphin.getLocation().getY() != 29.5) {
                Location downLocation = dolphin.getLocation();
                downLocation.setY(29.5);
                dolphin.teleport(downLocation);
            }
            itteratorTask = Utils.runLater(SharkEvent::dolphinItterator, MOVE_DELAY);
            return;
        } else {
            // This is if its not currently eating.
            Block block = raftWorld.getBlockAt(dolphin.getLocation());
            if (block.getType() != Material.AIR && block.getType() != Material.WATER && block.getType() != Material.BEDROCK) {
                isEating = true;
                block.breakNaturally();
                raftWorld.playSound(dolphin.getLocation(), Sound.ENTITY_PLAYER_BURP, 2f, 1f);
                itteratorTask = Utils.runLater(SharkEvent::dolphinItterator, EAT_DELAY);
            } else {
                dolphin.teleport(dolphin.getLocation().add(moveVector));
                itteratorTask = Utils.runLater(SharkEvent::dolphinItterator, MOVE_DELAY);
            }
        }
    }
    public static void stopSharkEventIfRunning() {
        if (dolphin == null) return;
        if (dolphin.isDead()) return;
        dolphin.remove();
    }
    @EventHandler
    public static void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.DOLPHIN) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.SUFFOCATION) return;
        event.setCancelled(true);
    }
}
