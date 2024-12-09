package net.worldmc.countries.commands.plot.subcommands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.worldmc.countries.Countries;
import net.worldmc.countries.economy.EconomyManager;
import net.worldmc.countries.managers.ConfirmationManager;
import net.worldmc.countries.managers.CacheManager;
import net.worldmc.countries.objects.Business;
import net.worldmc.countries.objects.Citizen;
import net.worldmc.countries.objects.Plot;
import net.worldmc.countries.objects.Town;
import net.worldmc.countries.util.FormattingUtils;
import net.worldmc.countries.util.MessageUtils;
import net.worldmc.countries.util.ObjectComponents;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class BuyCommand {
    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return Commands.literal("buy")
                .executes(context -> execute(context, "personal")) // Default to personal purchase if no parameter provided
                .then(Commands.argument("type", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            builder.suggest("business");
                            builder.suggest("town");
                            return builder.buildFuture();
                        })
                        .executes(context -> execute(context, context.getArgument("type", String.class))));
    }

    private int execute(@NotNull CommandContext<CommandSourceStack> context, String purchaseType) {
        Player player = (Player) context.getSource().getSender();
        Citizen citizen = CacheManager.getInstance().getCitizen(player);

        Plot plot = CacheManager.getInstance().getPlot(player.getLocation().getChunk());
        if (plot == null || !plot.isForSale()) {
            MessageUtils.sendMessage(player, FormattingUtils.general("This plot is not for sale."));
            return 1;
        }

        Town town = plot.getTown();
        if (town == null) {
            MessageUtils.sendMessage(player, FormattingUtils.general("This plot does not belong to any town."));
            return 1;
        }

        Business business = null;
        switch (purchaseType.toLowerCase()) {
            case "business":
                business = citizen.getBusiness();
                if (business == null || !business.isExtraTrusted(citizen)) {
                    MessageUtils.sendMessage(player, FormattingUtils.general("Only the business owner or managers can buy plots on behalf of the business."));
                    return 1;
                }
                break;

            case "town":
                town = citizen.getTown();
                if (town == null || !town.isExtraTrusted(citizen)) {
                    MessageUtils.sendMessage(player, FormattingUtils.general("Only the town leader or deputies can buy plots on behalf of the town."));
                    return 1;
                }
                break;
        }

        int price = plot.getPrice();
        Business finalBusiness = business;
        MessageUtils.sendMessage(player, FormattingUtils.general("This will cost")
                .appendSpace()
                .append(FormattingUtils.balance(price))
        );

        ConfirmationManager.addConfirmation(player, confirmedPlayer -> {
            if ("town".equalsIgnoreCase(purchaseType)) {
                if (plot.getOwner() instanceof Citizen owner) {
                    EconomyManager.getEconomy().deposit(Countries.getInstance().getName(), owner.getUUID(), BigDecimal.valueOf(price));
                } else if (plot.getOwner() instanceof Business businessOwner) {
                    EconomyManager.getEconomy().deposit(Countries.getInstance().getName(), businessOwner.getUUID(), BigDecimal.valueOf(price));
                    //businessOwner.setBalance(businessOwner.getBalance() + price);
                }
                plot.setOwner(null);
                plot.setPriorSalePrice(0);
                MessageUtils.sendMessage(confirmedPlayer, FormattingUtils.general("The town has successfully bought the plot back for")
                        .appendSpace()
                        .append(FormattingUtils.balance(price))
                        .appendNewline()
                        .append(FormattingUtils.general("The plot is now unowned."))
                );
            } else if (finalBusiness != null) {
                //finalBusiness.setBalance(finalBusiness.getBalance() - price);
                plot.setOwner(finalBusiness);
                plot.setPriorSalePrice(price);
                finalBusiness.addPlot(plot);
                MessageUtils.sendMessage(confirmedPlayer, FormattingUtils.general("You have successfully bought the plot for")
                        .appendSpace()
                        .append(FormattingUtils.balance(price))
                        .appendSpace()
                        .append(FormattingUtils.general("as"))
                        .appendSpace()
                        .append(ObjectComponents.groupComponent(finalBusiness))
                );
            } else {
                //citizen.setBalance(citizen.getBalance() - price);
                plot.setOwner(citizen);
                plot.setPriorSalePrice(price);
                citizen.addPlot(plot);
                MessageUtils.sendMessage(confirmedPlayer, FormattingUtils.general("You have successfully bought the plot for")
                        .appendSpace()
                        .append(FormattingUtils.balance(price))
                        .appendSpace()
                        .append(FormattingUtils.general("as yourself."))
                );
            }

            plot.setForSale(false);

            if (plot.getTown() != null) {
                MessageUtils.sendMessage(plot.getTown().getLeader(), FormattingUtils.general("A plot in your town has been bought for")
                        .appendSpace()
                        .append(FormattingUtils.balance(price))
                );
            }
        });

        return 1;
    }
}
