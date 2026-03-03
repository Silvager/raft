package com.silvager.raft.events;

import com.silvager.raft.GameManager;
import com.silvager.raft.Raft;
import com.silvager.raft.RaftMusic;
import com.silvager.raft.RaftSongs;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.entity.boat.AcaciaChestBoat;
import org.bukkit.entity.boat.SpruceBoat;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootTable;
import org.bukkit.util.Vector;

import java.util.List;

import static com.silvager.raft.GameManager.raftWorld;

public class MiniEvents {
    // 6 13 7
    public static void startDrownedEvent() {
        Player player = GameManager.raftWorld.getPlayers().getFirst();
        final int dist = 15;
        final int num = 10;
        for (int i=0; i<num; i++) {
            double radRot = ((double) i / (double) num) * Math.PI * 2;
            Location spawnLocation = new Location(GameManager.raftWorld, 6+Math.sin( radRot)*dist, 20, 7+Math.cos(radRot)*dist);
            Drowned newDrowned = GameManager.raftWorld.spawn(spawnLocation, Drowned.class);
            newDrowned.setTarget(player);
            newDrowned.setAggressive(true);
            newDrowned.setVelocity(new Vector(0, 3, 0));
            newDrowned.customName(Component.text("Aquaman"));
        }
    }
    public static void startCreepersEvent() {
        // Location spawnLocation = new Location(raftWorld, -51, 30, Raft.random.nextDouble(-51, 65));
        for (int z=-51; z<65; z+=5) {
            Location spawnLocation = new Location(raftWorld, -51, 30, z);
            Creeper creeper = raftWorld.spawn(spawnLocation, Creeper.class);
            boolean isPowered = Raft.random.nextInt(0, 4) == 0;
            creeper.setPowered(isPowered);
            creeper.customName(Component.text(isPowered ? "Big Boomer" : "Boomer"));
        }
    }
    public static void startPiratesEvent() {
        RaftMusic.playSong(RaftSongs.PIRATES);
        // z of 5 to 10 is fine
        for (double x=-48; x<-44; x++) {
            Location spawnLocation = new Location(GameManager.raftWorld, x, 30, 7);
            Boat boat = GameManager.raftWorld.spawn(spawnLocation, SpruceBoat.class);
            boat.setRotation(-90f, 0f);
            Pillager pillager = GameManager.raftWorld.spawn(spawnLocation, Pillager.class);
            pillager.customName(Component.text("Short John Silver").color(NamedTextColor.RED));
            pillager.setRotation(-90f, 0f);

            boat.addPassenger(pillager);
        }
    }
    public static void startMegaSniper() {
        Location spawnLocation = new Location(GameManager.raftWorld, 7, 80, 8);
        Chicken chicken = GameManager.raftWorld.spawn(spawnLocation, Chicken.class);
        Skeleton skeleton = GameManager.raftWorld.spawn(spawnLocation, Skeleton.class);
        chicken.addPassenger(skeleton);

        chicken.setEggLayTime(20);
        skeleton.setShouldBurnInDay(false);
        ItemStack enchantedBow = new ItemStack(Material.BOW);
        enchantedBow.addUnsafeEnchantment(Enchantment.PUNCH, 5);
        skeleton.getEquipment().setItemInMainHand(enchantedBow);
        skeleton.customName(Component.text("Sniper"));

    }
    public static void startCastawayEvent() {
        Location spawnLocation = new Location(raftWorld, -51, 30, Raft.random.nextDouble(-51, 65));
        ChestBoat chestBoat = raftWorld.spawn(spawnLocation, AcaciaChestBoat.class);
        chestBoat.setRotation(-90f, 0f);
        Inventory inv = chestBoat.getInventory();
        inv.addItem(new ItemStack(Material.DIRT, 16));
        switch (Raft.random.nextInt(0, 5)) {
            case 0 -> inv.addItem(new ItemStack(Material.FISHING_ROD, 1));
            case 1 -> inv.addItem(new ItemStack(Material.DIAMOND, 7));
            case 2 -> inv.addItem(new ItemStack(Material.ENDER_PEARL, 9));
            case 3 -> inv.addItem(new ItemStack(Material.POTATO, 30));
            case 4 -> inv.addItem(new ItemStack(Material.EMERALD, 20));
        }
        if (Raft.random.nextBoolean()) {
            inv.addItem(new ItemStack(Material.OBSIDIAN, Raft.random.nextInt(1, 3)));
        }

        Villager villager = raftWorld.spawn(spawnLocation, Villager.class);
        chestBoat.addPassenger(villager);
        villager.setProfession(Villager.Profession.FISHERMAN);
        String playerName = raftWorld.getPlayers().get(Raft.random.nextInt(raftWorld.getPlayerCount())).getName();
        villager.customName(Component.text(playerName+"'s Cousin").color(NamedTextColor.BLUE));
    }
    private static final String[] messages = {"Click here for FREE robux!",
            "New PTP album just dropped!!! Click to listen",
            "New Mechs album just dropped, CLICK HERE!",
            "Click here for EXCLUSIVE interview with God"
    };
    private static final String[] links = {"https://www.google.com/search?q=how+to+tell+my+parents+i+think+im+straight&sei=pnTiaKGEAb__ptQPvvXuuQo",
            "https://media.istockphoto.com/id/501027387/photo/not-there-yet.jpg?s=1024x1024&w=is&k=20&c=7HxKHQgqgSJA-Uypzl3OSMtSQ1ipw_csPM6Ao4VVwUQ=",
            "https://youtu.be/JrjL82ZBeQ0?si=nuQKV87exI2LX5GZ",
            "https://youtu.be/dQw4w9WgXcQ?si=D0_zusIgUTjHVqMT"
    };
    public static void chatTrollEvent() {
        int index = Raft.random.nextInt(0, messages.length);
    raftWorld.getPlayers().forEach((player -> {
        player.sendMessage(Component.text(messages[index]).color(NamedTextColor.BLUE)
                        .decorate(TextDecoration.UNDERLINED)
                .clickEvent(ClickEvent.openUrl(links[index])));
        player.playNote(player.getLocation(), Instrument.BELL, Note.natural(1, Note.Tone.A));
    }));
    }
    public static void sandFallEvent() {
        List<Player> players = raftWorld.getPlayers();
        Location spawn = players.get(Raft.random.nextInt(players.size())).getLocation().clone();
        spawn.setY(spawn.getY() + 30);
        for (int x=-1; x<2; x++) {
            for (int z=-1; z<2;z++) {
                raftWorld.getBlockAt((int) spawn.getX()+x, (int)spawn.getY(), (int)spawn.getZ()+z).setType(Material.SAND);
            }
        }
    }

}
