package com.silvager.raft;

import com.silvager.raft.events.*;

import java.util.*;

public class RaftEvents {
    private static boolean runAllThenRepeat = true;
    private static long gracePeriod = 20*60L;
    private static long minDelay = 20*30L;
    private static long maxDelay = 20*80L;
    private static final HashMap<String, Runnable> eventsMap = new HashMap<>();
    private static final HashMap<String, Runnable> eventsAllowed = new HashMap<>();
    // If runAllThenRepeat is false, it will never get emptied
    private static final ArrayList<Runnable> eventsLeftToRun = new ArrayList<>();
    public static void initializeEvents() {
        runAllThenRepeat = Raft.getInstance().getConfig().getBoolean("run-all-events-then-repeat");
        int tempGracePeriod = Raft.getInstance().getConfig().getInt("starting-grace-period");
        if (tempGracePeriod < 1) tempGracePeriod = 1;
        if (tempGracePeriod > 3600) tempGracePeriod = 3600;
        gracePeriod = tempGracePeriod * 20L;
        int tempMinDelay = Raft.getInstance().getConfig().getInt("min-event-delay");
        if (tempMinDelay < 1) tempMinDelay = 1;
        if (tempMinDelay > 3600) tempMinDelay = 3600;
        minDelay = tempMinDelay * 20L;
        int tempMaxDelay = Raft.getInstance().getConfig().getInt("max-event-delay");
        if (tempMaxDelay < tempMinDelay) tempMaxDelay = tempMinDelay;
        if (tempMaxDelay > 3600) tempMaxDelay = 3600;
        maxDelay = tempMaxDelay * 20L;

        eventsMap.put("pirates", MiniEvents::startPiratesEvent);
        eventsMap.put("creepers", MiniEvents::startCreepersEvent);
        eventsMap.put("drowned", MiniEvents::startDrownedEvent);
        eventsMap.put("megaSniper", MiniEvents::startMegaSniper);
        eventsMap.put("tsunami", TsunamiEvent::startTsunamiEvent);
        eventsMap.put("fireball", FireballEvent::startFireballEvent);
        eventsMap.put("castaway", MiniEvents::startCastawayEvent);
        eventsMap.put("chatTroll", MiniEvents::chatTrollEvent);
        eventsMap.put("amongUs", AmongUsEvent::startAmongUs);
        eventsMap.put("armorKit", ArmorKitEvent::startArmorKitEvent);
        eventsMap.put("sandFall", MiniEvents::sandFallEvent);
        eventsMap.put("wish", WishEvent::wishEvent);
        eventsMap.forEach((name, runnable) -> {
            if (Raft.getInstance().getConfig().getBoolean("enable-event."+name)) {
                eventsAllowed.put(name, runnable);
            }
        });

        refillEventsLeftToRun();

    }
    public static HashMap<String, Runnable> getEventsMap() {
        return eventsMap;
    }
    private static void refillEventsLeftToRun() {
        eventsLeftToRun.clear();
        eventsAllowed.forEach((name, runnable) -> {
            eventsLeftToRun.add(runnable);
        });
    }
    public static void startEvents() {
        Raft.scheduler.runTaskLater(Raft.getInstance(), RaftEvents::eventsIterator, gracePeriod);
    }
    private static void eventsIterator() {
        if (!GameManager.getIsRunning()) return;
        if (runAllThenRepeat) {
            if (eventsLeftToRun.isEmpty()) {
                refillEventsLeftToRun();
            }
            Runnable eventToRun = eventsLeftToRun.get(Raft.random.nextInt(0, eventsLeftToRun.size()));
            eventsLeftToRun.remove(eventToRun);
            eventToRun.run();
        } else {
            Runnable eventToRun = eventsLeftToRun.get(Raft.random.nextInt(0, eventsLeftToRun.size()));
            eventToRun.run();
        }

        long nextEventDelay = Raft.random.nextLong(minDelay, maxDelay);
        Raft.scheduler.runTaskLater(Raft.getInstance(), RaftEvents::eventsIterator, nextEventDelay);
    }
}
