package com.silvager.raft.events;

import com.silvager.raft.GameManager;
import com.silvager.raft.PlayerJoining;
import com.silvager.raft.Raft;
import com.silvager.raft.Utils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class FireballEvent {
    private static int timesRun = 0;
    private static int timesToRun = 10;
    public static void startFireballEvent() {
        if (GameManager.raftWorld.getPlayers().isEmpty()) return;
        timesRun = 0;
        timesToRun = Raft.random.nextInt(2, 5);
        Raft.scheduler.runTaskLater(Raft.getInstance(), FireballEvent::fireballItterator, 20L);
    }
    private static void fireballItterator() {
        if (timesRun > timesToRun || !GameManager.getIsRunning()) {
            return;
        }
        World raftWorld = GameManager.raftWorld;
        List<Player> players = raftWorld.getPlayers();
        if (players.isEmpty()) {
            return;
        }
        Player targetPlayer = players.get(Raft.random.nextInt(players.size()));
        targetPlayer.playSound(targetPlayer.getLocation(), Sound.BLOCK_TRIAL_SPAWNER_SPAWN_MOB, 2f, 2f);

        Location spawn = targetPlayer.getLocation().clone();
        spawn.setY(100);
        Fireball fireball = raftWorld.spawn(spawn, Fireball.class);
        fireball.setDirection(new Vector(0, -1, 0));
        fireball.setAcceleration(new Vector(0, -0.5, 0));


        timesRun++;
        Raft.scheduler.runTaskLater(Raft.getInstance(), FireballEvent::fireballItterator, Raft.random.nextInt(20, 200));
    }
}
