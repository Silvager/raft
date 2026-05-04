package com.silvager.raft.events;


import com.silvager.raft.*;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.silvager.raft.GameManager.raftWorld;

public class WishEvent implements Listener {
    static WishEvent instance;
    static Player wishPlayer;
    private static WishEvent getInstance() {
        if (instance == null) {
            instance = new WishEvent();
        }
        return instance;
    }
    public static void wishEvent() {
        Player player = Utils.getRandomPlayerOrNull();
        if (player == null) return;

        RaftMusic.playSong(RaftSongs.UPONRAINBOW, player);

        player.playNote(player.getLocation(), Instrument.BELL, Note.natural(1, Note.Tone.E));
        player.sendMessage(Component.text("Pssst... You get a wish!").color(NamedTextColor.AQUA));
        Raft.scheduler.runTaskLater(Raft.getInstance(), () -> {
            player.playNote(player.getLocation(), Instrument.BELL, Note.natural(1, Note.Tone.B));
            player.sendMessage(Component.text("Type an item name perfectly into chat").color(NamedTextColor.LIGHT_PURPLE));
            wishPlayer = player;
            Raft.registerListener(WishEvent.getInstance());
        }, 40L);
        Raft.scheduler.runTaskLater(Raft.getInstance(), () -> {
            if (!GameManager.getIsRunning()) return;
            //Make this only run if the player never said anything
            if (wishPlayer != player) return;

            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 1f, 1f);
            player.sendMessage(Component.text("Time's up :)").color(NamedTextColor.RED));
            wishPlayer = null;
            HandlerList.unregisterAll(WishEvent.getInstance());
        }, Raft.random.nextLong(5*20L, 25*20L));
    }
    @EventHandler
    public static void onChatMessage(AsyncChatEvent event) {
        if (!GameManager.getIsRunning()) {
            wishPlayer = null;
            HandlerList.unregisterAll(WishEvent.getInstance());
        }
        Player player = event.getPlayer();
        if (player != wishPlayer) return;

        String msg = PlainTextComponentSerializer.plainText().serialize(event.message());
        msg = msg.trim().toUpperCase().replace(" ", "_");
        Material material = Material.matchMaterial(msg);
        if (material == null) {
            Raft.scheduler.runTaskLater(Raft.getInstance(), () -> {
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 1f, 1f);
                player.sendMessage(Component.text("Thats not a valid item, your wish is over >:)").color(NamedTextColor.RED));
            }, 100L);
        } else {
            Raft.scheduler.runTaskLater(Raft.getInstance(), () -> {
                player.playNote(player.getLocation(), Instrument.BELL, Note.natural(1, Note.Tone.G));
                player.sendMessage(Component.text("As you desire").color(NamedTextColor.GREEN));
                player.give(new ItemStack(material, 1));
            }, 100L);
        }
        wishPlayer = null;
        HandlerList.unregisterAll(WishEvent.getInstance());
    }

}
