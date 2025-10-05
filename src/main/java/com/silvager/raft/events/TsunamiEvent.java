package com.silvager.raft.events;

import com.silvager.raft.GameManager;
import com.silvager.raft.Raft;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

public class TsunamiEvent {
    private static final long WATER_DELAY = 20L;
    private static int currentX = -51;
    public static void startTsunamiEvent() {
        currentX = -51;
        // Location spawnLocation = new Location(raftWorld, -51, 30, Raft.random.nextDouble(-51, 65));
        //x 58 is the max
        GameManager.raftWorld.setStorm(true);
        Raft.scheduler.runTaskLater(Raft.getInstance(), TsunamiEvent::tsunamiItterate, 1L);
    }
    private static void tsunamiItterate() {
        if (currentX <= 58) {
            // Place water
            for (int z=-51; z<65; z++) {
                GameManager.raftWorld.getBlockAt(currentX, 50, z).setType(Material.WATER);
            }
        }
        //remove water behind
        //Note: removes 1 wider than placing cause water goes on the side
        if (currentX > -50) {
            //remove water behind
            for (int z=-52; z<66; z++) {
                for (int y=50; y>30; y--) {
                    for (int x=currentX-9; x>currentX -13; x--) {
                        Block block = GameManager.raftWorld.getBlockAt(x, y, z);
                        if (block.getType() == Material.WATER) {
                            block.setType(Material.AIR);
                        }
                    }
                }

            }
        }
        if (currentX != 68) {
            currentX++;
            Raft.scheduler.runTaskLater(Raft.getInstance(), TsunamiEvent::tsunamiItterate, WATER_DELAY);
            GameManager.raftWorld.setStorm(false);
        }

    }
}
