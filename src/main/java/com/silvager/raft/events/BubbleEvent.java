package com.silvager.raft.events;

import com.silvager.raft.Raft;
import com.silvager.raft.Utils;
import org.bukkit.Input;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import static com.silvager.raft.GameManager.raftWorld;

public class BubbleEvent {
    private static boolean isRunning = false;
    private static BlockDisplay innerGlass = null;
    private static BlockDisplay outerGlass = null;
    private static Player bubblePlayer = null;
    private static final int MIN_DURATION =30; // both in seconds
    private static final int MAX_DURATION = 120;

    private static final int ITTERATOR_DELAY = 3;

    private static int durationInTicks = 0;
    private static int ticksEventRunFor = 0;

    public static void bubbleEvent() {
        if (isRunning) {
            endBubbleEventIfRunning();
        }
        isRunning = true;
        ticksEventRunFor = 0;
        durationInTicks = Raft.random.nextInt(MIN_DURATION, MAX_DURATION)*20;
        bubblePlayer = Utils.getRandomPlayerOrNull();
        if (bubblePlayer == null) return;
        bubblePlayer.setGravity(false);
        bubblePlayer.setPose(Pose.SITTING);
        bubblePlayer.setRiptiding(false);
        Location glassLocation = bubblePlayer.getLocation();
        glassLocation.setRotation(45f, 0f);
        innerGlass = raftWorld.spawn(glassLocation, BlockDisplay.class);
        innerGlass.setBlock(Material.GLASS.createBlockData());
        innerGlass.setTransformation(new Transformation(
                new Vector3f(1.5f, 2.5f, 1.5f), // no translation
                new AxisAngle4f(), // no left rotation
                new Vector3f(-3, -3, -3), // scale up by a factor of 3 (inside out)
                new AxisAngle4f() // no right rotation
        ));
        outerGlass = raftWorld.spawn(glassLocation, BlockDisplay.class);
        outerGlass.setBlock(Material.GLASS.createBlockData());
        outerGlass.setTransformation(new Transformation(
                new Vector3f(-1.5f, -0.5f, -1.5f), // no translation
                new AxisAngle4f(), // no left rotation
                new Vector3f(3, 3, 3), // scale up by a factor of 3 on all axes
                new AxisAngle4f() // no right rotation
        ));
        PotionEffect effect = new PotionEffect(PotionEffectType.LEVITATION, 40, 3);
        bubblePlayer.addPotionEffect(effect);
        bubbleItterator();
    }
    private static void bubbleItterator() {
        if (ticksEventRunFor > durationInTicks || bubblePlayer == null || bubblePlayer.isDead() || !isRunning) {
            endBubbleEventIfRunning();
            return;
        }

        innerGlass.setTeleportDuration(ITTERATOR_DELAY+1);
        outerGlass.setTeleportDuration(ITTERATOR_DELAY+1);

        Location newLocation = bubblePlayer.getLocation();
        newLocation.setRotation(45f, 0f);

        innerGlass.teleport(newLocation);
        outerGlass.teleport(newLocation);

        Input currentInput = bubblePlayer.getCurrentInput();
        if (currentInput.isSneak()) {
            Location downLocation = bubblePlayer.getLocation();
            downLocation.setY(bubblePlayer.getY()-0.5f);
            bubblePlayer.teleport(downLocation);
        }
        if (currentInput.isJump()) {
            Location upLocation = bubblePlayer.getLocation();
            upLocation.setY(bubblePlayer.getY()+0.5f);
            bubblePlayer.teleport(upLocation);
        }


        ticksEventRunFor += ITTERATOR_DELAY;
        Utils.runLater(BubbleEvent::bubbleItterator, ITTERATOR_DELAY);
    }
    public static void endBubbleEventIfRunning() {
        if (!isRunning) return;
        if (innerGlass != null) {
            innerGlass.remove();
            innerGlass = null;
        }
        if (outerGlass != null) {
            outerGlass.remove();
            outerGlass = null;
        }
        bubblePlayer.getWorld().playSound(bubblePlayer.getLocation(), Sound.BLOCK_GLASS_BREAK, 5f, 0.8f);
        bubblePlayer.setGravity(true);
        isRunning = false;
    }
}
