package dev.mkpwnz.serverUtils.commands;

import dev.mkpwnz.api.arguments.PlayerArg;
import dev.mkpwnz.api.arguments.StringArg;
import dev.mkpwnz.api.arguments.StringListArg;
import dev.mkpwnz.api.commands.Command;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

// This class is just to test the CommandAPI

public class ServerInfoCommand {
    private final JavaPlugin plugin;

    public ServerInfoCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Command(
            name = "serverinfo",
            description = "Zeigt Serverinformationen an",
            permission = "serverutils.serverinfo"
    )
    public void onServerInfo(CommandSender sender) {
        Server server = plugin.getServer();

        sender.sendMessage("§6=== Server Informationen ===");
        sender.sendMessage("§7Server Version: §f" + server.getVersion());
        sender.sendMessage("§7Bukkit Version: §f" + server.getBukkitVersion());
        sender.sendMessage("§7Online Spieler: §f" + server.getOnlinePlayers().size() + "/" + server.getMaxPlayers());
        sender.sendMessage("§7Server Port: §f" + server.getPort());
        sender.sendMessage("§7Spielmodus: §f" + server.getDefaultGameMode());
        sender.sendMessage("§7Schwierigkeit: §f" + server.getWorlds().getFirst().getDifficulty());

        double tps = server.getServerTickManager().getTickRate();
        sender.sendMessage("§7TPS: §f" + String.format("%.2f", tps));
    }

    @Command(
            name = "performance",
            parent = {"serverinfo"},
            description = "Zeigt Performance-Informationen an",
            permission = "serverutils.serverinfo.performance"
    )
    public void onServerInfoPerformance(
            CommandSender sender,
            @StringListArg(name = "detail", description = "Detail Level (basic/full)", required = false, allowedValues = {"basic", "full"}) String detail
    ) {
        sender.sendMessage("§6=== Performance Test ===");
        sender.sendMessage("§7Detail Level: §f" + (detail != null ? detail : "basic"));
        sender.sendMessage("§7Test Parameter wurde übergeben: §f" + (detail != null));
    }

    @Command(
            name = "player",
            parent = {"serverinfo"},
            description = "Zeigt Spieler-Informationen an",
            permission = "serverutils.serverinfo.player"
    )
    public void onServerInfoPlayer(
            CommandSender sender,
            @PlayerArg(name = "target", description = "Zielspieler") Player target,
            @StringArg(
                    name = "infoType",
                    description = "Art der Information",
                    minLength = 3,
                    maxLength = 10
            ) String infoType
    ) {
        sender.sendMessage("§6=== Spieler Information ===");
        sender.sendMessage("§7Spieler: §f" + target.getName());
        sender.sendMessage("§7Info Typ: §f" + infoType);
        sender.sendMessage("§7Test Parameter wurden übergeben!");
    }

    // Beispiel für einen Sub-Subcommand
    @Command(
            name = "inventory",
            parent = {"serverinfo", "player"},
            description = "Zeigt Spieler-Inventar an",
            permission = "serverutils.serverinfo.player.inventory"
    )
    public void onServerInfoPlayerInventory(
            CommandSender sender,
            @PlayerArg(name = "target", description = "Zielspieler") Player target
    ) {
        sender.sendMessage("§6=== Spieler Inventar ===");
        sender.sendMessage("§7Spieler: §f" + target.getName());
    }
}