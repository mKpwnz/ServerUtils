package dev.mkpwnz.api.arguments;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerArgValidator extends ArgumentValidator<Player> {
    private final boolean onlineOnly;

    public PlayerArgValidator(String name, String description, boolean required, boolean onlineOnly) {
        super(name, description, required);
        this.onlineOnly = onlineOnly;
    }

    @Override
    public ValidationResult<Player> validate(String input, CommandSender sender) {
        Player player = Bukkit.getPlayer(input);
        if (player == null && onlineOnly) {
            return ValidationResult.error("Spieler ist nicht online");
        }
        return ValidationResult.success(player);
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }
}

