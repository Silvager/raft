package com.silvager.raft;

import com.silvager.raft.events.*;

import java.util.*;

public class RaftEvents {
    private static final long GRACE_PERIOD = 20*10L;
    private static final long MIN_DELAY = 20*10L;
    private static final long MAX_DELAY = 20*30L;
    private static final HashMap<String, Runnable> eventsMap = new HashMap<>();
    private static final ArrayList<Runnable> eventsLeftToRun = new ArrayList<>();
    public static void initializeEvents() {
    eventsMap.put("hi", HiEvent::runHiEvent);
        eventsMap.put("pirates", MiniEvents::startPiratesEvent);
        eventsMap.put("creepers", MiniEvents::startCreepersEvent);
        eventsMap.put("drowned", MiniEvents::startDrownedEvent);
        eventsMap.put("megaSniper", MiniEvents::startMegaSniper);
        eventsMap.put("tsunami", TsunamiEvent::startTsunamiEvent);
        eventsMap.put("fireball", FireballEvent::startFireballEvent);
        eventsMap.put("castaway", MiniEvents::startCastawayEvent);
        eventsMap.put("chatTroll", MiniEvents::chatTrollEvent);


        refillEventsLeftToRun();
    }
    public static HashMap<String, Runnable> getEventsMap() {
        return eventsMap;
    }
    private static void refillEventsLeftToRun() {
        eventsLeftToRun.clear();
        eventsMap.forEach((name, runnable) -> {
            eventsLeftToRun.add(runnable);
        });
    }
    public static void startEvents() {
        Raft.scheduler.runTaskLater(Raft.getInstance(), RaftEvents::eventsItterator, GRACE_PERIOD);
    }
    private static void eventsItterator() {
        if (!GameManager.getIsRunning()) return;
        if (eventsLeftToRun.isEmpty()) {
            refillEventsLeftToRun();
        }
        Runnable eventToRun = eventsLeftToRun.get(Raft.random.nextInt(0, eventsLeftToRun.size()));
        eventsLeftToRun.remove(eventToRun);
        eventToRun.run();

        long nextEventDelay = Raft.random.nextLong(MIN_DELAY, MAX_DELAY);
        Raft.scheduler.runTaskLater(Raft.getInstance(), RaftEvents::eventsItterator, nextEventDelay);
    }
}
