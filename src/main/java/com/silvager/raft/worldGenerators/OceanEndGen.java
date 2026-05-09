package com.silvager.raft.worldGenerators;

import org.bukkit.Material;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class OceanEndGen extends ChunkGenerator {
    final static int dist = 20;
    final static int num = 5;
    @Override
    public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                chunkData.setBlock(x, 32, z, Material.BEDROCK);
                chunkData.setBlock(x, 33, z, Material.SAND);
                for (int y=34; y<64; y++) {
                    chunkData.setBlock(x, y, z, Material.WATER);
                }

            }
        }
    }
}
