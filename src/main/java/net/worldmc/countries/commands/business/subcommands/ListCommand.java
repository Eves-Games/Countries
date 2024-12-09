package net.worldmc.countries.commands.business.subcommands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.worldmc.countries.managers.CacheManager;
import net.worldmc.countries.objects.Business;
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
    private static final int BUSINESSES_PER_PAGE = 10;

    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return Commands.literal("list")
                .executes(context -> execute(context, 1)) // Default to page 1 if no page number is provided
                .then(Commands.argument("page", IntegerArgumentType.integer(1))
                        .executes(context -> execute(context, context.getArgument("page", Integer.class))));
    }

    private int execute(@NotNull CommandContext<CommandSourceStack> context, int page) {
        Player player = (Player) context.getSource().getSender();

        List<Business> businesses = new ArrayList<>(CacheManager.getInstance().getBusinesses());
        if (businesses.isEmpty()) {
            MessageUtils.sendMessage(player, FormattingUtils.general("There are no businesses to display."));
            return 1;
        }

        businesses.sort(Comparator.comparingInt(Business::getMemberCount).reversed());

        int totalBusinesses = businesses.size();
        int totalPages = (int) Math.ceil((double) totalBusinesses / BUSINESSES_PER_PAGE);

        if (page > totalPages || page < 1) {
            MessageUtils.sendMessage(player, FormattingUtils.fail("Invalid page number. There are " + totalPages + " pages available."));
            return 1;
        }

        int startIndex = (page - 1) * BUSINESSES_PER_PAGE;
        int endIndex = Math.min(startIndex + BUSINESSES_PER_PAGE, totalBusinesses);

        Component info = FormattingUtils.general("Business List ").append(FormattingUtils.data("Page " + page + "/" + totalPages));

        for (int i = startIndex; i < endIndex; i++) {
            Business business = businesses.get(i);
            Component townInfo = Component.newline()
                    .append(FormattingUtils.key(String.valueOf((i + 1)))).appendSpace()
                    .append(ObjectComponents.groupComponent(business)).appendSpace()
                    .append(FormattingUtils.data(business.getMemberCount()));

            info = info.append(townInfo);
        }

        MessageUtils.sendMessage(player, info);
        return 1;
    }
}
