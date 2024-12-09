package net.worldmc.countries.commands.town.subcommands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.worldmc.countries.managers.CacheManager;
import net.worldmc.countries.objects.Town;
import net.worldmc.countries.util.FormattingUtils;
import net.worldmc.countries.util.MessageUtils;
import net.worldmc.countries.util.ObjectComponents;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListCommand {
    private static final int TOWNS_PER_PAGE = 10;

    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return Commands.literal("list")
                .executes(context -> execute(context, 1)) // Default to page 1 if no page number is provided
                .then(Commands.argument("page", IntegerArgumentType.integer(1))
                        .executes(context -> execute(context, context.getArgument("page", Integer.class))));
    }

    private int execute(@NotNull CommandContext<CommandSourceStack> context, int page) {
        Player player = (Player) context.getSource().getSender();
        List<Town> towns = new ArrayList<>(CacheManager.getInstance().getTowns());
        if (towns.isEmpty()) {
            MessageUtils.sendMessage(player, FormattingUtils.general("There are no towns to display."));
            return 1;
        }

        towns.sort(Comparator.comparingInt(Town::getPlotCount).reversed());

        int totalTowns = towns.size();
        int totalPages = (int) Math.ceil((double) totalTowns / TOWNS_PER_PAGE);

        if (page > totalPages || page < 1) {
            MessageUtils.sendMessage(player, FormattingUtils.fail("Invalid page number. There are " + totalPages + " pages available."));
            return 1;
        }

        int startIndex = (page - 1) * TOWNS_PER_PAGE;
        int endIndex = Math.min(startIndex + TOWNS_PER_PAGE, totalTowns);

        Component info = FormattingUtils.general("Town List ").append(FormattingUtils.data("Page " + page + "/" + totalPages));

        for (int i = startIndex; i < endIndex; i++) {
            Town town = towns.get(i);
            Component townInfo = Component.newline()
                    .append(FormattingUtils.key(String.valueOf((i + 1)))).appendSpace()
                    .append(ObjectComponents.groupComponent(town)).appendSpace()
                    .append(FormattingUtils.data(town.getMemberCount()));

            info = info.append(townInfo);
        }

        MessageUtils.sendMessage(player, info);
        return 1;
    }
}
