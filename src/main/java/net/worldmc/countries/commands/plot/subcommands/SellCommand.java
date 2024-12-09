package net.worldmc.countries.commands.plot.subcommands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.worldmc.countries.managers.CacheManager;
import net.worldmc.countries.objects.Citizen;
import net.worldmc.countries.objects.Plot;
import net.worldmc.countries.objects.Town;
import net.worldmc.countries.objects.Business;
import net.worldmc.countries.util.FormattingUtils;
import net.worldmc.countries.util.MessageUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SellCommand {
    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return Commands.literal("sell")
                .then(Commands.argument("price", IntegerArgumentType.integer(1))
                        .executes(this::execute));
    }

    private int execute(@NotNull CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Citizen citizen = CacheManager.getInstance().getCitizen(player);
        int price = context.getArgument("price", Integer.class);

        if (citizen.getTown() == null) {
            MessageUtils.sendMessage(player, FormattingUtils.general("You must be part of a town to use this command."));
            return 1;
        }

        Plot land = CacheManager.getInstance().getPlot(player.getLocation().getChunk());
        if (land == null) {
            MessageUtils.sendMessage(player, FormattingUtils.general("You must be standing in a claimed plot to sell it."));
            return 1;
        }

        Town town = citizen.getTown();
        if (land.getOwner() == null) {
            if (!town.isTrusted(citizen)) {
                MessageUtils.sendMessage(player, FormattingUtils.general("Only the town leader, deputies, or elders can sell unowned plots."));
                return 1;
            }
        } else {
            if (land.getOwner() instanceof Citizen owner) {
                if (!owner.equals(citizen)) {
                    MessageUtils.sendMessage(player, FormattingUtils.general("Only the owner of this plot can sell it."));
                    return 1;
                }
            } else if (land.getOwner() instanceof Business businessOwner) {
                if (!businessOwner.isExtraTrusted(citizen)) {
                    MessageUtils.sendMessage(player, FormattingUtils.general("Only the business owner or managers can sell this plot."));
                    return 1;
                }
            }

            MessageUtils.sendMessage(player, FormattingUtils.general("Note: 50% of the sale proceeds will go to the town's treasury."));
        }

        land.setForSale(true);
        land.setPrice(price);

        MessageUtils.sendMessage(player, FormattingUtils.general("Plot set for sale at")
                .appendSpace()
                .append(FormattingUtils.balance(price)));

        return 1;
    }
}
