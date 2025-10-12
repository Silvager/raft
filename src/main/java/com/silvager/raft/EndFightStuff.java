package com.silvager.raft;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class EndFightStuff {
    public static void endFightStarted() {
     startEndCurrentSystem();
    }
    static void startEndCurrentSystem() {
        final Vector currentVector = new Vector(0, -0.5, 0.0);
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
        int quantity = 10;
        int dist = 40;
        double increaseAmount = Math.PI*2 / quantity;
        for (double angle =0;angle<Math.PI*2; angle+=increaseAmount) {
            int x = (int) Math.floor(Math.sin(angle)*dist);
            int z = (int) Math.floor(Math.cos(angle)*dist);
            int y = Raft.random.nextInt(66, 90);
            GameManager.raftEndWorld.getBlockAt(x, y, z).setType(Material.OBSIDIAN);
            GameManager.raftEndWorld.spawn(new Location(GameManager.raftWorld, x+0.5, y+2, z+0.5), EnderCrystal.class);
        }
    }
}
