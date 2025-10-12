package com.silvager.raft;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class RaftCommands {
    static HashSet<CommandSender> inProgressReset = new HashSet<>();
    public static void registerCommands() {
        LiteralArgumentBuilder<CommandSourceStack> startNode = Commands.literal("startRaft")
                .executes(ctx -> {
                    GameManager.startGame();
                    return Command.SINGLE_SUCCESS;
                });
        LiteralArgumentBuilder<CommandSourceStack> resetNode = Commands.literal("resetRaft")
                .executes(ctx -> {
                    if (!inProgressReset.contains(ctx.getSource().getSender())) {
                        ctx.getSource().getSender().sendMessage("Type the command again to confirm reset");
                        inProgressReset.add(ctx.getSource().getSender());
                        Raft.scheduler.runTaskLater(Raft.getInstance(), () -> {
                            if (inProgressReset.contains(ctx.getSource().getSender())) {
                                inProgressReset.remove(ctx.getSource().getSender());
                                ctx.getSource().getSender().sendMessage("Raft reset timed out");
                            }
                        }, 20*10L);
                    } else {
                        inProgressReset.remove(ctx.getSource().getSender());
                        WorldReset.resetWorld();
                    }

                    return Command.SINGLE_SUCCESS;
                });

        LiteralArgumentBuilder<CommandSourceStack> runEventNode = Commands.literal("runEvent");
        RaftEvents.getEventsMap().forEach((name, runnable) -> {
            runEventNode.then(Commands.literal(name).executes(ctx -> {
                runnable.run();
                return Command.SINGLE_SUCCESS;
            }));
        });
        Raft.getInstance().getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            commands.registrar().register(startNode.build());
            commands.registrar().register(runEventNode.build());
            commands.registrar().register(resetNode.build());
        });
    }
}
