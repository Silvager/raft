package com.silvager.raft.events;

import com.silvager.raft.GameManager;
import com.silvager.raft.Raft;
import com.silvager.raft.RaftMusic;
import com.silvager.raft.RaftSongs;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.time.Duration;

public class AmongUsEvent {

    public static void startAmongUs() {
        //For later
        Title title = Title.title(
                Component.text("Crewmate").color(NamedTextColor.BLUE),
                Component.text("Do your tasks").color(net.kyori.adventure.text.format.NamedTextColor.GRAY),
                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(500))
        );
        RaftMusic.playSong(RaftSongs.AMONGUS);
        Raft.scheduler.runTaskLater(Raft.getInstance(), ()-> {
            GameManager.raftWorld.getPlayers().forEach((player -> {
                player.sendMessage(Component.text("The imposter will be chosen in 10 seconds...").color(NamedTextColor.RED));
            }));
        }, 40L);
        Raft.scheduler.runTaskLater(Raft.getInstance(), ()-> {
            GameManager.raftWorld.getPlayers().forEach((player -> {
                player.showTitle(title);
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
            }));
        }, 240L);
    }

}
