package com.silvager.raft.events;

import com.silvager.raft.Raft;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ChestBoat;
import org.bukkit.entity.boat.AcaciaChestBoat;
import org.bukkit.entity.boat.SpruceBoat;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import static com.silvager.raft.GameManager.raftWorld;

public class ArmorKitEvent {
    public static void startArmorKitEvent() {
        Location spawnLocation = new Location(raftWorld, -51, 30, Raft.random.nextDouble(-51, 65));
        SpruceBoat boat = raftWorld.spawn(spawnLocation, SpruceBoat.class);
        boat.setRotation(-90f, 0f);
        ArmorStand stand = raftWorld.spawn(spawnLocation, ArmorStand.class);
        stand.setRotation(-90f, 0f);
        boat.addPassenger(stand);

        EntityEquipment equipment = stand.getEquipment();

        int rand = Raft.random.nextInt(0, 4);
        switch (rand) {
            case 0 -> {
                setKit(equipment, Material.DIAMOND_AXE, Material.SHIELD, Material.LEATHER_BOOTS, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_CHESTPLATE, Material.SKELETON_SKULL);
            }
            case 1 -> {
                setKit(equipment, Material.BOW, Material.ARROW, Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS, Material.LEATHER_CHESTPLATE, Material.TARGET);
                equipment.setItemInOffHand(new ItemStack(Material.ARROW, 32));
            }
            case 2 -> {
                setKit(equipment, Material.NETHERITE_PICKAXE, Material.LAVA_BUCKET, Material.IRON_BOOTS, Material.IRON_LEGGINGS, Material.IRON_CHESTPLATE, Material.BLAST_FURNACE);
            }
            case 3 -> {
                setKit(equipment, Material.TRIDENT, Material.ENCHANTED_BOOK, Material.DIAMOND_BOOTS, Material.DIAMOND_LEGGINGS, Material.DIAMOND_CHESTPLATE, Material.SEA_LANTERN);
                ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
                meta.addStoredEnchant(Enchantment.RIPTIDE, 1, true);
                book.setItemMeta(meta);
                equipment.setItemInOffHand(book);
            }
        }
    }
    private static void setKit(EntityEquipment equipment, Material mainHand, Material offhand, Material boots, Material leggings, Material chestplate, Material helmet) {
        equipment.setItemInMainHand(new ItemStack(mainHand));
        equipment.setItemInOffHand(new ItemStack(offhand));
        equipment.setBoots(new ItemStack(boots));
        equipment.setLeggings(new ItemStack(leggings));
        equipment.setChestplate(new ItemStack(chestplate));
        equipment.setHelmet(new ItemStack(helmet));
    }
}
