package com.silvager.raft.islandDungeons;

import com.silvager.raft.Raft;
import org.bukkit.Bukkit;
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
    public static void loadDungeonWorld() {
        Path worldRoot = Raft.getInstance().getServer().getLevelDirectory().resolve("dimensions/minecraft");
//        Path serverRoot = Bukkit.getServer().getWorldContainer().toPath();
        Path worldDestination = worldRoot.resolve("dungeonWorld");
        if (!Files.exists(worldDestination)) {
            try {
                extractZip("worlds/dungeonWorld.zip", worldDestination);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        WorldCreator creator = new WorldCreator("dungeonWorld");
        World world = creator.createWorld();
        world.setAutoSave(false);
        world.setViewDistance(4);
        world.setSimulationDistance(4);
        world.setSpawnFlags(false, false);
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
