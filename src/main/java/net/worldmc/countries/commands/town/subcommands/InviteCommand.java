package net.worldmc.countries.commands.town.subcommands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.worldmc.countries.managers.CacheManager;
import net.worldmc.countries.managers.InviteManager;
import net.worldmc.countries.objects.Citizen;
import net.worldmc.countries.objects.Town;
import net.worldmc.countries.util.FormattingUtils;
import net.worldmc.countries.util.MessageUtils;
import net.worldmc.countries.util.ObjectComponents;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class InviteCommand {
    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return Commands.literal("invite")
                .then(Commands.argument("name", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            Bukkit.getOnlinePlayers().forEach(player -> builder.suggest(player.getName()));
                            return builder.buildFuture();
                        })
                        .executes(this::execute));
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Citizen citizen = CacheManager.getInstance().getCitizen(player);
        if (citizen == null || citizen.getTown() == null) {
            MessageUtils.sendMessage(player, FormattingUtils.general("You are not part of any town and cannot invite players."));
            return 1;
        }

        Town town = citizen.getTown();
        if (!town.isTrusted(citizen)) {
            MessageUtils.sendMessage(player, FormattingUtils.general("Only the town leader, deputies, or elders can invite players."));
            return 1;
        }

        String inviteeName = context.getArgument("name", String.class);
        OfflinePlayer inviteePlayer = Bukkit.getOfflinePlayer(inviteeName);

        if (!inviteePlayer.hasPlayedBefore()) {
            MessageUtils.sendMessage(player, FormattingUtils.fail("The player \"" + inviteeName + "\" has never played on the server!"));
            return 1;
        }

        Citizen inviteeCitizen = CacheManager.getInstance().getCitizen(inviteePlayer.getUniqueId());
        if (inviteeCitizen == null) {
            MessageUtils.sendMessage(player, FormattingUtils.fail("There was an error retrieving the player's data. Please try again later."));
            return 1;
        }

        if (!inviteePlayer.isOnline()) {
            MessageUtils.sendMessage(player, FormattingUtils.fail("The player " + ObjectComponents.citizenComponent(inviteeCitizen) + " is not online!"));
            return 1;
        }

        Player onlineInvitee = (Player) inviteePlayer;
        if (town.getMembers().contains(inviteeCitizen)) {
            MessageUtils.sendMessage(player, FormattingUtils.fail("Player is already a citizen of your town!"));
            return 1;
        }

        if (InviteManager.hasPendingInvite(onlineInvitee, town)) {
            MessageUtils.sendMessage(player, FormattingUtils.fail("Player already has a pending invite to join your town!"));
            return 1;
        }

        InviteManager.addInvite(onlineInvitee, town);
        MessageUtils.sendMessage(player, FormattingUtils.general("Invitation sent to " + ObjectComponents.citizenComponent(inviteeCitizen) + " to join " + ObjectComponents.groupComponent(town)));
        return 1;
    }
}
