package net.worldmc.countries.managers;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.worldmc.countries.objects.Citizen;
import net.worldmc.countries.objects.Town;
import net.worldmc.countries.util.FormattingUtils;
import net.worldmc.countries.util.MessageUtils;
import net.worldmc.countries.util.ObjectComponents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class InviteManager {
    private static final Map<UUID, Town> pendingInvites = new HashMap<>();

    public static void addInvite(Player player, Town town) {
        pendingInvites.put(player.getUniqueId(), town);
        MessageUtils.sendMessage(player, FormattingUtils.general("You have been invited to join the town ")
                .append(ObjectComponents.groupComponent(town))
                .appendNewline()
                .append(FormattingUtils.general("Please type "))
                .append(Component.text("/accept").color(NamedTextColor.GREEN))
                .append(FormattingUtils.general(" to join or "))
                .append(Component.text("/deny").color(NamedTextColor.RED))
                .append(FormattingUtils.general(" to refuse.")));
    }

    public static void acceptInvite(Player player, String townName) {
        UUID playerUUID = player.getUniqueId();
        Town town = pendingInvites.get(playerUUID);

        if (town != null && town.getName().equalsIgnoreCase(townName)) {
            pendingInvites.remove(playerUUID);
            town.addMember(CacheManager.getInstance().getCitizen(player));
            MessageUtils.sendMessage(player, FormattingUtils.general("You have accepted the invitation to join the town ")
                            .append(ObjectComponents.groupComponent(town)));

            for (Citizen citizen : town.getMembers()) {
                Player townPlayer = Bukkit.getPlayer(citizen.getUUID());
                if (townPlayer != null && townPlayer.isOnline()) {
                    MessageUtils.sendMessage(townPlayer, FormattingUtils.general(player.getName() + " has joined your town."));
                }
            }
        } else {
            MessageUtils.sendMessage(player, FormattingUtils.fail("You have no pending invites!"));
        }
    }

    public static void denyInvite(Player player, String townName) {
        UUID playerUUID = player.getUniqueId();
        Town town = pendingInvites.get(playerUUID);

        if (town != null && town.getName().equalsIgnoreCase(townName)) {
            pendingInvites.remove(playerUUID);
            MessageUtils.sendMessage(player, FormattingUtils.general("You have denied the invitation to join the town ")
                    .append(ObjectComponents.groupComponent(town)));
        } else {
            MessageUtils.sendMessage(player, FormattingUtils.fail("You have no pending invites!"));
        }
    }

    public static boolean hasPendingInvite(Player player, Town town) {
        UUID playerUUID = player.getUniqueId();
        return pendingInvites.containsKey(playerUUID) && pendingInvites.get(playerUUID).equals(town);
    }

    public static List<String> getPendingInviteNames(Player player) {
        return pendingInvites.entrySet().stream()
                .filter(entry -> entry.getKey().equals(player.getUniqueId()))
                .map(entry -> entry.getValue().getName())
                .collect(Collectors.toList());
    }
}
