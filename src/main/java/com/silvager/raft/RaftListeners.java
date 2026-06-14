package com.silvager.raft;

import com.silvager.raft.islandDungeons.DungeonEvent;
import io.papermc.paper.event.entity.EntityPortalReadyEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.PortalType;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.util.List;

public class RaftListeners implements Listener {

    // The listener for player respawn is in PlayerJoining just in case someone manages to die before the game starts

    @EventHandler
    public static void onPlayerFish(PlayerFishEvent event) {

        if (event.getState() != PlayerFishEvent.State.CAUGHT_ENTITY) {
            return;
        }
        final Entity caughtEntity = event.getCaught();
        if (caughtEntity == null) {
            return;
        }

        if (!(caughtEntity instanceof Item caughtItem)) {
            return;
        }

        Vector direction = event.getPlayer().getLocation().toVector().subtract(caughtItem.getLocation().toVector());
        direction = direction.multiply(0.1f);
        if (direction.getY() < 0.2f) {
            direction.setY(0.2f);
        }
        caughtItem.setVelocity(direction);
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
        if (event.getEntity().getWorld() == GameManager.raftWorld) {
            event.setTargetWorld(GameManager.raftEndWorld);
        } else if (event.getEntity().getWorld() == GameManager.raftEndWorld){
            event.setTargetWorld(GameManager.raftWorld);
        }

    }
    @EventHandler
    public static void onPlayerDeath(PlayerDeathEvent event) {
        var player = event.getPlayer();
        var location = player.getLocation();
        // Check if they died in dungeon world
        World dungeonWorld = DungeonEvent.getDungeonWorld();
        if (dungeonWorld != null) {
            if (location.getWorld() == dungeonWorld) {
                event.deathMessage(Component.text(player.getName()+" will not be returning from the expedition...").color(NamedTextColor.BLUE));
                return;
            }
        }
        //They didn't die in dungeon world
        if (location.getWorld() != GameManager.raftWorld) return;
        if (location.getX() <= 58) return;

        event.deathMessage(Component.text(player.getName()+" was lost at sea").color(NamedTextColor.BLUE));
    }
    @EventHandler
    public static void onEntityDie(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (entity.getType() == EntityType.ITEM) return;
        var componentCustomName = entity.customName();
        if (componentCustomName == null) return;
        String customName = PlainTextComponentSerializer.plainText().serialize(componentCustomName);

        ItemStack toAdd = switch (customName) {
            case "Sniper" -> randObby(1, 4);
            case "Short John Silver", "Big Boomer" -> randObby(-1, 3);
            case "Boomer" -> randObby(-2, 3);
            case "Aquaman" -> randObby(-2, 2);
            default -> null;
        };
        if (toAdd == null) return;
        if (toAdd.getAmount() == 0) return;

        event.getDrops().add(toAdd);
    }
    private static ItemStack randObby(int floor, int ceil) {
        int num = Raft.random.nextInt(floor, ceil);
        if (num < 1) return null;
        return new ItemStack(Material.OBSIDIAN, num);
    }
}
