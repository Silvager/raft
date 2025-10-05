package com.silvager.raft;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class OceanWorldGen extends ChunkGenerator {
    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                chunkData.setBlock(x, 0, z, Material.BEDROCK);
                chunkData.setBlock(x, 1, z, Material.SAND);
                for (int y=2; y<30; y++) {
                    if (y == 29) {
                        if (chunkX == 0 && chunkZ == 0) {
                            if (x > 4 && x < 10 && z > 4 && z < 10) {
                                chunkData.setBlock(x, y, z, Material.OAK_PLANKS);
                                break;
                            }

                        }
                    }
                    chunkData.setBlock(x, y, z, Material.WATER);

                }
            }
        }
    }
}
