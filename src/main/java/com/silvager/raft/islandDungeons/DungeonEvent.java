package com.silvager.raft.islandDungeons;

import com.silvager.raft.GameManager;
import com.silvager.raft.Raft;
import com.silvager.raft.Utils;
import com.silvager.raft.WorldReset;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.Sound;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.joml.Vector3i;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;

import static com.silvager.raft.GameManager.raftWorld;

public class DungeonEvent {
    private static final int EXPEDITION_DELAY_TIME = 20*15;
    private static final DungeonSpawn[] dungeonSpawns = {
      new DungeonSpawn(new Vector3i(8, -47, 8), Component.text("You spot a battered raft in the distance.").color(NamedTextColor.GOLD), 50, 110),
            new DungeonSpawn(new Vector3i(-146, -47, -22), Component.text("You see the top of a mysterious pyramid peeking over the horizon.").color(NamedTextColor.LIGHT_PURPLE), 100, 170),
            new DungeonSpawn(new Vector3i(-367, -47, -34), Component.text("A lush green island is spotted off to the west.").color(NamedTextColor.GREEN), 140, 230),
            new DungeonSpawn(new Vector3i(210, -47, -13), Component.text("You can see a small desert island a decent ways off.").color(NamedTextColor.YELLOW), 140, 290),
            new DungeonSpawn(new Vector3i(446, -47, -31), Component.text("You see a crashed plane floating in the water").color(NamedTextColor.DARK_AQUA), 70, 130),
    };
    private static ArrayList<DungeonSpawn> spawnsLeftToVisit = new ArrayList<>();
    private static boolean isDungeonRunning = false;
    private static ArrayList<Player> playersWaitingForExpedition = new ArrayList<>();
    private static World dungeonWorld;
    public static World getDungeonWorld() {
        return dungeonWorld;
    }


    public static void setupDungeonEvent() {
        spawnsLeftToVisit.addAll(Arrays.asList(dungeonSpawns));
        World dungeonWorld = Raft.getInstance().getServer().getWorld("dungeonworld");
        if (dungeonWorld != null) {
            WorldReset.deleteWorld(dungeonWorld);
        }
        DungeonUtils.loadResetDungeonWorld();
    }

    public static void runDungeonEvent() {
        if (isDungeonRunning) {
            raftWorld.getPlayers().forEach(player -> {
                player.sendMessage(Component.text("You gaze out at the horizon but don't see anything").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC));
            });
            return;
        }
        isDungeonRunning = true;
        dungeonWorld = Raft.getInstance().getServer().getWorld("dungeonworld");

        //reset the world if all spawns were visited
        if (spawnsLeftToVisit.isEmpty()) {
            if (!dungeonWorld.getPlayers().isEmpty()) {
                dungeonWorld.getPlayers().forEach(player -> {
                    player.teleport(GameManager.oceanSpawn);
                });
            }
            Bukkit.unloadWorld(dungeonWorld, false);
            DungeonUtils.loadResetDungeonWorld();
            spawnsLeftToVisit.addAll(Arrays.asList(dungeonSpawns));
            dungeonWorld = Raft.getInstance().getServer().getWorld("dungeonworld");
        }
        // set up the spawn location
        DungeonSpawn dungeonSpawn = spawnsLeftToVisit.remove(Raft.random.nextInt(0, spawnsLeftToVisit.size()));
        Vector3i spawnPos = dungeonSpawn.spawnPosition();
        Location dungeonSpawnLocation = new Location(dungeonWorld, spawnPos.x, spawnPos.y, spawnPos.z);


        playersWaitingForExpedition.clear();
        //Notify people of starting
        raftWorld.getPlayers().forEach(player -> {
            player.sendMessage(dungeonSpawn.infoMsg().decorate(TextDecoration.ITALIC));
            player.playSound(player.getLocation(), Sound.ITEM_SPYGLASS_USE, 3f, 1f);
        });
        Utils.runLater(() -> {
            raftWorld.getPlayers().forEach(player -> {
                player.sendMessage(Component.text("An expedition will be leaving shortly. ").color(NamedTextColor.AQUA)
                        .append(Component.text("Click to join expedition").color(NamedTextColor.BLUE).decorate(TextDecoration.UNDERLINED)
                                .clickEvent(ClickEvent.callback((Audience audience) -> {
                                    Player clickerPlayer = Utils.getPlayerFromAudience(audience);
                                    if (clickerPlayer == null) return;
                                    onPlayerJoinExpedition(clickerPlayer, Bukkit.getCurrentTick());
                                }))));
                player.playNote(player.getLocation(), Instrument.BELL, Note.natural(1, Note.Tone.B));
            });
        }, 60L);
        //Send the expedition people on their way
        Utils.runLater(() -> {
            playersWaitingForExpedition.forEach(player -> {
                player.teleportAsync(dungeonSpawnLocation);
            });
        }, 60L+EXPEDITION_DELAY_TIME);

        // Notify the remaining people that the expedition has left. A bit after to make sure they left.
        // Also give the folks in the expedition the update.
        // This also triggers the end expedition timer
        long expeditionTime = Raft.random.nextLong(dungeonSpawn.minExploreTime()* 20L, dungeonSpawn.maxExploreTime()* 20L);
        World finalDungeonWorld = dungeonWorld;
        Utils.runLater(() -> {
            raftWorld.getPlayers().forEach(player -> {
                player.sendMessage(Component.text("The expedition has departed.").color(NamedTextColor.AQUA));
                player.playNote(player.getLocation(), Instrument.GUITAR, Note.natural(1, Note.Tone.F));
            });
            // If nobody actually joined the expedition, stop the event. This also stops the return event from happening.
            if (finalDungeonWorld.getPlayers().isEmpty()) {
                isDungeonRunning = false;
                return;
            }
            finalDungeonWorld.getPlayers().forEach(player -> {
                player.sendMessage(Component.text("Boat will return to main raft before it has drifted too far away").color(NamedTextColor.DARK_RED));
                player.sendMessage(Component.text(
                        "Be in boat before then or be stranded").color(NamedTextColor.RED));
                player.sendMessage(Component.text("(warning will be given)").color(NamedTextColor.RED).decorate(TextDecoration.ITALIC));
                player.playNote(player.getLocation(), Instrument.BELL, Note.natural(1, Note.Tone.G));
            });
            // Start water damage in dungeon world
            waterDamageItterator();
            // Start the timer ending sequence. This is INSIDE the other one so that if the first one exits early (no one joins expedition) will not run ending sequence
            startExpeditionEndingSequence(finalDungeonWorld, expeditionTime, dungeonSpawnLocation);

        }, 65L+EXPEDITION_DELAY_TIME);
    }
    private static void onPlayerJoinExpedition(Player player, int tickWhenMessageSent) {
        if ((Bukkit.getCurrentTick() - tickWhenMessageSent) > EXPEDITION_DELAY_TIME) return;
        if (playersWaitingForExpedition.contains(player)) return;

        player.sendMessage(Component.text("You join the expedition").color(NamedTextColor.GRAY).decorate(TextDecoration.ITALIC));
        player.playNote(player.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.C));
        playersWaitingForExpedition.add(player);
    }
    private static void startExpeditionEndingSequence(World dungeonWorld, long expeditionTime, Location dungeonSpawnLocation) {
        // Actually end
        if (expeditionTime > 20*45L) {
            Utils.runLater(() -> {
                dungeonWorld.getPlayers().forEach(player -> {
                    player.sendMessage(Component.text("45 seconds until boat leaves").color(NamedTextColor.RED));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_SPLASH, 1f, 1.5f);
                });
            }, expeditionTime- (20*45L));
        }
        Utils.runLater(() -> {
            dungeonWorld.getPlayers().forEach(player -> {
                player.sendMessage(Component.text("10 seconds until boat leaves").color(NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_SPLASH, 1f, 0.8f);
            });
        }, expeditionTime - (20*10L));
        Utils.runLater( () -> {
            if (!dungeonWorld.getPlayers().isEmpty()) {
                raftWorld.getPlayers().forEach(player -> {
                    player.sendMessage(Component.text("The expedition has returned.").color(NamedTextColor.GOLD));
                });
                dungeonWorld.getPlayers().forEach(player -> {
                    // If player on boat, send back
                    if (player.getLocation().distance(dungeonSpawnLocation) < 3) {
                        player.teleportAsync(GameManager.oceanSpawn);
                    } else {
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 2f, 1f);
                        Title leftBehindTitle = Title.title(
                                net.kyori.adventure.text.Component.text("You were left behind").color(NamedTextColor.RED),
                                net.kyori.adventure.text.Component.text("You did not get on the boat in time").color(NamedTextColor.DARK_RED),
                                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(8), Duration.ofMillis(500))
                        );
                        player.showTitle(leftBehindTitle);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 500, 2, true, false));
                        Utils.runLater(() -> {
                            player.damage(1000);
                        }, 8*20L);
                    }

                });
            }
            isDungeonRunning = false;

        }, expeditionTime);
    }
    private static void waterDamageItterator() {
        World dungeonWorld = getDungeonWorld();
        dungeonWorld.getEntities().forEach(entity -> {
            if (entity.isInWater()) {
                // Delete boats
                if (entity instanceof Boat) {
                    entity.remove();
                } else if (entity.getType() == EntityType.PLAYER) {
                    Player player = (Player) entity;
                    player.damage(2);
                }
            }
        });
        if (isDungeonRunning) {
            Utils.runLater(DungeonEvent::waterDamageItterator, 20L);
        }
    }

}
