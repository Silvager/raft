# Raft
Raft is a Minecraft paper plugin that adds a minigame survival experience loosely based on the video game Raft
## Gameplay
- You start on a raft in the middle of the ocean, gathering supplies that the current brings you to survive.
However, that same current can mean your doom so don't fall in!
- Survive random events that range from helpful, to silly, to deadly.
- Head out on expeditions to explore mysterious islands and wrecks, but be back before the boat leaves...
- Defeat monsters in events to gather enough obsidian to build a nether portal, which will bring you to a boss fight to win the game!

## Setup/usage
- Download the JAR and put it into your server's `plugins` folder.
- Set `allow-flight=true` in your `server.properties` file to avoid kicking players
- It is recommended to install `NoteBlockAPI` as certain events have music, but the game will run without it
- Once in the game, run `/raft start` to begin! (op needed) If you stop the server and then restart it, you will need to run `/raft start` again but the world and player inventories will be saved
- You can use `/raft event` to manually trigger an event
- You can use `/raft resetWorld` to fully reset the game and player inventories
- You can change game settings in the plugin's `config.yml`
## Notes
Raft is meant to be a standalone experience and not part of a larger server.

Specifically, it will automatically put all players on the server into the world the plugin made
and override some other things. 

However, the plugin won't mess with other worlds or settings on the server
so if you install Raft and then uninstall it, your server will not be affected.