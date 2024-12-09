package net.worldmc.countries.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.worldmc.countries.objects.Citizen;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageUtils {
    public static Component prefixed(Component message) {
        return Component.text("[Countries]").color(NamedTextColor.GOLD).appendSpace().append(message);
    }

    public static void sendMessage(CommandSender sender, Component message) {
        if (sender != null) {
            sender.sendMessage(prefixed(message));
        }
    }

    public static void sendMessage(Player player, Component message) {
        if (player != null && player.isOnline()) {
            player.sendMessage(prefixed(message));
        }
    }

    public static void sendMessage(Citizen citizen, Component message) {
        if (citizen != null) {
            Player player = Bukkit.getPlayer(citizen.getUUID());
            sendMessage(player, message);
        }
    }
}
