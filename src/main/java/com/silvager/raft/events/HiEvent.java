package com.silvager.raft.events;

import com.silvager.raft.GameManager;

public class HiEvent {
    public static void runHiEvent() {
        GameManager.raftWorld.getPlayers().forEach(player -> player.sendMessage("HI PLAYER"));
    }
}
