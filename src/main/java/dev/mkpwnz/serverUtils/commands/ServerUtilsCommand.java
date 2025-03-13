package dev.mkpwnz.serverUtils.commands;

import dev.mkpwnz.api.commands.Command;
import dev.mkpwnz.api.commands.CommandData;
import dev.mkpwnz.api.commands.CommandManager;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

// This Class is just to test the CommandAPI

public class ServerUtilsCommand {

    private final CommandManager commandManager;

    public ServerUtilsCommand(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    @Command(
            name = "serverutils",
            description = "Zeigt alle verfügbaren ServerUtils Commands",
            permission = "serverutils.help"
    )
    public void onServerUtils(CommandSender sender) {
        Map<String, CommandData> commands = commandManager.getRegisteredCommands();

        sender.sendMessage("§6=== ServerUtils Commands ===");

        // Gruppiere Commands nach ihren Basis-Commands
        Map<String, List<CommandData>> groupedCommands = new TreeMap<>();

        for (Map.Entry<String, CommandData> entry : commands.entrySet()) {
            String[] parts = entry.getKey().split("\\.");
            String baseCommand = parts[0];

            groupedCommands.computeIfAbsent(baseCommand, k -> new ArrayList<>())
                    .add(entry.getValue());
        }

        // Ausgabe der gruppierten Commands
        for (Map.Entry<String, List<CommandData>> group : groupedCommands.entrySet()) {
            sender.sendMessage("");
            sender.sendMessage("§e/" + group.getKey() + "§7:");

            for (CommandData cmd : group.getValue()) {
                // Erstelle den vollen Command-Pfad
                String fullCommand = cmd.fullName().replace(".", " ");

                // Füge Parameter hinzu
                String parameters = cmd.getParametersAsString();
                String fullUsage = parameters.isEmpty() ? fullCommand : fullCommand + " " + parameters;

                // Zeige Command mit Beschreibung
                sender.sendMessage("  §7/" + fullUsage);
                if (!cmd.description().isEmpty()) {
                    sender.sendMessage("    §8➜ " + cmd.description());
                }
                if (!cmd.permission().isEmpty()) {
                    sender.sendMessage("    §8⚡ " + cmd.permission());
                }
            }
        }
    }
}
