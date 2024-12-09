package net.worldmc.countries.commands.citizen.subcommands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.worldmc.countries.managers.CacheManager;
import net.worldmc.countries.objects.Citizen;
import net.worldmc.countries.util.FormattingUtils;
import net.worldmc.countries.util.MessageUtils;
import net.worldmc.countries.util.ObjectComponents;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class InfoCommand {
    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return Commands.literal("info")
                .executes(context -> execute(context, null)) // Default execution when no name argument is provided
                .then(Commands.argument("name", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            Bukkit.getOnlinePlayers().forEach(player -> builder.suggest(player.getName()));
                            return builder.buildFuture();
                        })
                        .executes(context -> execute(context, context.getArgument("name", String.class))));
    }

    private int execute(@NotNull CommandContext<CommandSourceStack> context, String playerName) {
        Player player = (Player) context.getSource().getSender();

        Citizen citizen;
        if (playerName == null || playerName.trim().isEmpty()) {
            citizen = CacheManager.getInstance().getCitizen(player);
            if (citizen == null) {
                MessageUtils.sendMessage(player, FormattingUtils.fail("Critical error: You are not registered as a citizen! Please contact support."));
                return 1;
            }
        } else {
            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(playerName.trim());
            citizen = CacheManager.getInstance().getCitizen(targetPlayer.getUniqueId());
            if (citizen == null) {
                MessageUtils.sendMessage(player, FormattingUtils.general("A player with the name '" + playerName + "' was not found."));
                return 1;
            }
        }

        Component info = FormattingUtils.object(citizen.getName())
                .appendNewline()
                .append(FormattingUtils.key("Town: "))
                .append(ObjectComponents.groupComponent(citizen.getTown()))
                .appendNewline()
                .append(FormattingUtils.key("Business: "))
                .append(ObjectComponents.groupComponent(citizen.getBusiness()))
                .appendNewline()
                .append(FormattingUtils.key("Land: "))
                .append(FormattingUtils.general(citizen.getPlotCount()));

        MessageUtils.sendMessage(player, info);
        return 1;
    }
}
