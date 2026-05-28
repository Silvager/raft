package com.silvager.raft.islandDungeons;

import com.silvager.raft.Raft;
import com.silvager.raft.WorldReset;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DungeonUtils {
    public static void loadResetDungeonWorld() {
        Path worldRoot = Raft.getInstance().getServer().getLevelDirectory().resolve("dimensions/raft");
//        Path serverRoot = Bukkit.getServer().getWorldContainer().toPath();
        Path worldDestination = worldRoot.resolve("dungeonworld");
        if (Files.exists(worldDestination)) {
            WorldReset.deleteWorld(worldDestination.toFile());
        }
        try {
            extractZip("worlds/dungeonworld.zip", worldRoot);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        WorldCreator creator = new WorldCreator(new NamespacedKey("raft", "dungeonworld"));
        World world = Bukkit.createWorld(creator);
        if (world != null) {
            world.setViewDistance(4);
            world.setSimulationDistance(4);
            world.setSpawnFlags(false, false);
        }
    }

    private static void extractZip(String resourceName, Path destDir) throws IOException {
        try (InputStream in = Raft.getInstance().getResource(resourceName);
             ZipInputStream zip = new ZipInputStream(in)) {

            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                Path outPath = destDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(outPath);
                } else {
                    Files.createDirectories(outPath.getParent());
                    Files.copy(zip, outPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zip.closeEntry();
            }
        }
    }

}
