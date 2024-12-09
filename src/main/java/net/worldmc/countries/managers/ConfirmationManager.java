package net.worldmc.countries.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.worldmc.countries.util.FormattingUtils;
import net.worldmc.countries.util.MessageUtils;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ConfirmationManager {
    private static final Map<UUID, Consumer<Player>> pendingConfirmations = new HashMap<>();

    public static void addConfirmation(Player player, Consumer<Player> action) {
        pendingConfirmations.put(player.getUniqueId(), action);
        MessageUtils.sendMessage(player, FormattingUtils.general("Please type ")
                .append(Component.text("/confirm").color(NamedTextColor.GREEN))
                .append(FormattingUtils.general(" to proceed or "))
                .append(Component.text("/cancel").color(NamedTextColor.RED))
                .append(FormattingUtils.general(" to dismiss."))
        );
    }

    public static void confirm(Player player) {
        Consumer<Player> action = pendingConfirmations.remove(player.getUniqueId());
        if (action != null) {
            action.accept(player);
        } else {
            MessageUtils.sendMessage(player, FormattingUtils.fail("You have no pending confirmations!"));
        }
    }

    public static void cancel(Player player) {
        if (pendingConfirmations.remove(player.getUniqueId()) != null) {
            MessageUtils.sendMessage(player, FormattingUtils.general("Confirmation cancelled."));
        } else {
            MessageUtils.sendMessage(player, FormattingUtils.fail("You have no pending confirmations!"));
        }
    }
}
