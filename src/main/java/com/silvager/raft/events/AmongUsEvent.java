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
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

public class AmongUsEvent {
    private static final Title crewmateTitle = Title.title(
            Component.text("Crewmate").color(NamedTextColor.BLUE),
            Component.text("Do your tasks").color(NamedTextColor.GRAY),
            Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(500))
    );
    private static final Title imposterTitle = Title.title(
            Component.text("Imposter").color(NamedTextColor.RED),
            Component.text("Be sus").color(NamedTextColor.GRAY),
            Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofMillis(500))
    );

    public static void startAmongUs() {
        boolean imposterExists = Raft.random.nextInt(0, 4) == 0;
        Player imposter;
        if (imposterExists) {
            List<Player> players = GameManager.raftWorld.getPlayers();
            imposter = players.get(Raft.random.nextInt(0, players.size()));
        } else {
            imposter = null;
        }
        //For later

        RaftMusic.playSong(RaftSongs.AMONGUS);
        Raft.scheduler.runTaskLater(Raft.getInstance(), ()-> {
            GameManager.raftWorld.getPlayers().forEach((player -> {
                player.sendMessage(Component.text("The imposter will be chosen in 10 seconds...").color(NamedTextColor.RED));
            }));
        }, 40L);
        Raft.scheduler.runTaskLater(Raft.getInstance(), ()-> {
            GameManager.raftWorld.getPlayers().forEach((player -> {
                if (imposterExists && player == imposter) {
                    player.showTitle(imposterTitle);

                    player.give(getImposterKnife());
                } else {
                    player.showTitle(crewmateTitle);
                }
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);

            }));
        }, 240L);
    }
    private static ItemStack getImposterKnife() {
        ItemStack imposterKnife = new ItemStack(Material.IRON_SWORD);
        ItemMeta meta = imposterKnife.getItemMeta();
        if (meta instanceof Damageable damageable) {
            damageable.setDamage(imposterKnife.getType().getMaxDurability() - 3 );
        }
        meta.itemName(Component.text("Imposter Knife").color(NamedTextColor.RED));
        meta.addEnchant(Enchantment.KNOCKBACK, 20, true);
        meta.setEnchantmentGlintOverride(false);


        imposterKnife.setItemMeta(meta);
        return imposterKnife;
    }

}
