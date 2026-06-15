package com.silvager.raft.events;

import com.silvager.raft.GameManager;
import com.silvager.raft.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class PurgeEvent {

    private static final int ARROW_ITTERATOR_COUNT = 6;

    private static ArrayList<Arrow> arrows;
    public static void runPurgeEvent() {
        List<Player> players = GameManager.raftWorld.getPlayers();
        players.forEach(player -> {
            player.playSound(player.getLocation(), Sound.ITEM_GOAT_HORN_SOUND_5, 4f, 1f);
            player.sendMessage(Component.text("Purge incoming...").color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.ITALIC));
        });
        arrows = new ArrayList<>();
        Utils.runLater(() -> {
            arrowPurgeItterator(1);
            Utils.runLater(() -> {
                arrows.forEach(Arrow::remove);
                arrows.clear();
            }, 15*20);
        }, 4*20);

    }
    private static void arrowPurgeItterator(int index) {
        List<LivingEntity> targets = GameManager.raftWorld.getLivingEntities();
        List<Player> players = GameManager.raftWorld.getPlayers();
        players.forEach(player -> {
            player.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1f, 1f);
        });
        targets.forEach(target -> {
            Location arrowSpawn = target.getLocation();
            arrowSpawn.add(0, 10, 0);
            Arrow arrow = GameManager.raftWorld.spawnArrow(arrowSpawn, new Vector(0, -5, 0), 1.2f, 0.1f);
            arrow.setDamage(5);
            arrows.add(arrow);
        });
        index++;
        if (index <= ARROW_ITTERATOR_COUNT) {
            int finalIndex = index;
            Utils.runLater(()->{
                arrowPurgeItterator(finalIndex);
            }, 30);
        }
    }
}
