package net.worldmc.countries.commands.town.subcommands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.worldmc.countries.managers.CacheManager;
import net.worldmc.countries.objects.Citizen;
import net.worldmc.countries.objects.Town;
import net.worldmc.countries.util.FormattingUtils;
import net.worldmc.countries.util.MessageUtils;
import net.worldmc.countries.util.ObjectComponents;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class InfoCommand {
    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return Commands.literal("info")
                .executes(context -> execute(context, null)) // Default execution when no name argument is provided
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .suggests((context, builder) -> {
                            CacheManager.getInstance().getTowns().forEach(town -> builder.suggest(town.getName()));
                            return builder.buildFuture();
                        })
                        .executes(context -> execute(context, context.getArgument("name", String.class))));
    }

    private int execute(@NotNull CommandContext<CommandSourceStack> context, String townName) {
        Player player = (Player) context.getSource().getSender();
        Town town;
        if (townName == null || townName.trim().isEmpty()) {
            Citizen citizen = CacheManager.getInstance().getCitizen(player);
            if (citizen == null || citizen.getTown() == null) {
                MessageUtils.sendMessage(player, FormattingUtils.general("You are not part of any town and need to specify a town name."));
                return 1;
            }
            town = citizen.getTown();
        } else {
            town = CacheManager.getInstance().getTown(townName.trim());
            if (town == null) {
                MessageUtils.sendMessage(player, FormattingUtils.general("A town with the name \"" + townName + "\" was not found."));
                return 1;
            }
        }

        Component info = FormattingUtils.object(town.getName())
                .appendNewline()
                .append(FormattingUtils.key("Leader: "))
                .append(ObjectComponents.citizenComponent(town.getLeader()))
                .appendNewline()
                .append(FormattingUtils.key("Bank Account: "))
                .append(FormattingUtils.balance(town.getBankAccount().getBalance()))
                .appendNewline()
                .append(FormattingUtils.key("Citizens: "))
                .append(FormattingUtils.general(town.getMemberCount()))
                .appendNewline()
                .append(FormattingUtils.key("Plots: "))
                .append(FormattingUtils.general(town.getPlotCount()));

        MessageUtils.sendMessage(player, info);
        return 1;
    }
}
