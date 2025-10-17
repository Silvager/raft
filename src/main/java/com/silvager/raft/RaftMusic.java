package com.silvager.raft;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;
import org.bukkit.entity.Player;

public class RaftMusic {
    private static Song sussySong;
    private static Song piratesSong;
    private static Song uponRainbow;
    private static Song rickroll;
    private static boolean isNoteblockApiInstalled;
    public static void preloadMusic(boolean noteblockApiInstalled) {
        isNoteblockApiInstalled = noteblockApiInstalled;
        if (!isNoteblockApiInstalled) return;
        sussySong = NBSDecoder.parse(Raft.getInstance().getResource("songs/sussy.nbs"));
        piratesSong = NBSDecoder.parse(Raft.getInstance().getResource("songs/pirates.nbs"));
        uponRainbow = NBSDecoder.parse(Raft.getInstance().getResource("songs/upon-rainbow.nbs"));
        rickroll = NBSDecoder.parse(Raft.getInstance().getResource("songs/rickroll.nbs"));
    }
    public static void playSong(RaftSongs raftSong) {
        if (!isNoteblockApiInstalled) return;
        RadioSongPlayer rsp = new RadioSongPlayer(getSong(raftSong));
        GameManager.raftWorld.getPlayers().forEach((rsp::addPlayer));
        GameManager.raftEndWorld.getPlayers().forEach((rsp::addPlayer));
        rsp.setPlaying(true);
    }
    public static void playSong(RaftSongs raftSong, Player player) {
        if (!isNoteblockApiInstalled) return;
        RadioSongPlayer rsp = new RadioSongPlayer(getSong(raftSong));
        rsp.addPlayer(player);
        rsp.setPlaying(true);
    }
    private static Song getSong(RaftSongs raftSong) {
        Song song = null;
        if (raftSong == RaftSongs.AMONGUS) song = sussySong;
        else if (raftSong == RaftSongs.PIRATES) song = piratesSong;
        else if (raftSong == RaftSongs.UPONRAINBOW) song = uponRainbow;
        else if (raftSong == RaftSongs.RICKROLL) song = rickroll;
        return song;
    }

}
