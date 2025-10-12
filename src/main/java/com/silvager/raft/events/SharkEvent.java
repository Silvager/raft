package com.silvager.raft.events;

import com.silvager.raft.GameManager;
import com.silvager.raft.Raft;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Dolphin;

public class SharkEvent {
    static Song skibidiSong;
    static Dolphin dolphin;
    static ArmorStand stand;
    public static void startSharkEvent() {
    }
    private static void playSkibidiSong() {
        RadioSongPlayer rsp = new RadioSongPlayer(skibidiSong);
        GameManager.raftWorld.getPlayers().forEach((rsp::addPlayer));
        rsp.setPlaying(true);
    }
    public static void preloadSkibidiSong() {
        skibidiSong = NBSDecoder.parse(Raft.getInstance().getResource("songs/skibidi.nbs"));
    }
}
