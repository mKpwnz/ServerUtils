package dev.mkpwnz.serverUtils;

import dev.mkpwnz.api.commands.CommandManager;
import dev.mkpwnz.serverUtils.commands.ServerInfoCommand;
import dev.mkpwnz.serverUtils.commands.ServerUtilsCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class ServerUtils extends JavaPlugin {

    @Override
    public void onEnable() {
        CommandManager commandManager = new CommandManager(this);
        ServerUtilsCommand serverUtilsCommand = new ServerUtilsCommand(commandManager);
        
        commandManager.registerCommand(serverUtilsCommand);
        commandManager.registerCommand(new ServerInfoCommand(this));

        getLogger().info("ServerUtils wurde aktiviert!");
    }

    @Override
    public void onDisable() {
        getLogger().info("ServerUtils wurde deaktiviert!");
    }
}