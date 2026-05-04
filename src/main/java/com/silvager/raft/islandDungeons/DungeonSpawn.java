package com.silvager.raft.islandDungeons;

import net.kyori.adventure.text.Component;
import org.joml.Vector3i;

public record DungeonSpawn(Vector3i spawnPosition, Component infoMsg, int minExploreTime, int maxExploreTime) {
}
