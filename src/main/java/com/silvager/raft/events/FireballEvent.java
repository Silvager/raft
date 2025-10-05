package com.silvager.raft.events;

import com.silvager.raft.GameManager;
import com.silvager.raft.Raft;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class FireballEvent {
    private static int timesRun = 0;
    private static int timesToRun = 10;
    public static void startFireballEvent() {
        timesToRun = Raft.random.nextInt(3, 11);
        Raft.scheduler.runTaskLater(Raft.getInstance(), FireballEvent::fireballItterator, 20L);
    }
    private static void fireballItterator() {
        if (timesRun > timesToRun || !GameManager.getIsRunning()) {
            return;
        }
        World raftWorld = GameManager.raftWorld;
        List<Player> players = raftWorld.getPlayers();
        Location spawn = players.get(Raft.random.nextInt(players.size())).getLocation().clone();
        spawn.setY(100);
        Fireball fireball = raftWorld.spawn(spawn, Fireball.class);
        fireball.setDirection(new Vector(0, -1, 0));
        fireball.setAcceleration(new Vector(0, -0.5, 0));


        timesRun++;
        Raft.scheduler.runTaskLater(Raft.getInstance(), FireballEvent::fireballItterator, Raft.random.nextInt(20, 200));
    }
}
