package com.silvager.raft;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;

public class RaftCommands {

    public static void registerCommands() {
        LiteralArgumentBuilder<CommandSourceStack> startNode = Commands.literal("startRaft")
                .executes(ctx -> {
                    GameManager.startGame();
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
        });
    }
}
