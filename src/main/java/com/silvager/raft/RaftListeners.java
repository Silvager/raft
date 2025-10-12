package com.silvager.raft;

import io.papermc.paper.event.entity.EntityPortalReadyEvent;
import org.bukkit.PortalType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.util.Vector;

public class RaftListeners implements Listener {
    @EventHandler
    public static void onPlayerRespawn(PlayerRespawnEvent event) {
        if (GameManager.oceanSpawn != null) {
            event.setRespawnLocation(GameManager.oceanSpawn);
        }
    }
    @EventHandler
    public static void onPlayerFish(PlayerFishEvent event) {

        if (event.getState() != PlayerFishEvent.State.CAUGHT_ENTITY) {
            return;
        }
        final Entity caughtEntity = event.getCaught();
        if (caughtEntity == null) {
            return;
        }

        Raft.getInstance().getLogger().info(caughtEntity.getName());
        if (!(caughtEntity instanceof Item)) {
            return;
        }

        Item caughtItem = (Item) caughtEntity;
        Vector direction = event.getPlayer().getLocation().toVector().subtract(caughtItem.getLocation().toVector());
        direction = direction.multiply(0.1f);
        if (direction.getY() < 0.2f) {
            direction.setY(0.2f);
        }
        caughtItem.setVelocity(direction);
    }
    @EventHandler
    public static void onPortalBuilt(PortalCreateEvent event) {
//        if (event.getReason() == PortalCreateEvent.CreateReason.FIRE) {
//            event.setCancelled(true);
//            assert event.getEntity() != null;
//            GameManager.raftWorld.createExplosion(event.getEntity().getLocation(), 1f);
//        }
    }
    static boolean hasStartedEndFight = false;
    @EventHandler
    public static void onEntityReadyPortal(EntityPortalReadyEvent event) {
        if (event.getPortalType() != PortalType.NETHER) {
            return;
        }
        if (!hasStartedEndFight) {
            EndFightStuff.endFightStarted();
            hasStartedEndFight = true;
        }
        event.setTargetWorld(GameManager.raftEndWorld);
    }
}
