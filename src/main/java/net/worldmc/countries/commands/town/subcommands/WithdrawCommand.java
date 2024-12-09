package net.worldmc.countries.commands.town.subcommands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.milkbowl.vault2.economy.EconomyResponse;
import net.worldmc.countries.Countries;
import net.worldmc.countries.economy.EconomyManager;
import net.worldmc.countries.managers.CacheManager;
import net.worldmc.countries.objects.Citizen;
import net.worldmc.countries.objects.Town;
import net.worldmc.countries.util.FormattingUtils;
import net.worldmc.countries.util.MessageUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class WithdrawCommand {
    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return Commands.literal("withdraw")
                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(context -> execute(context, context.getArgument("amount", Integer.class))));
    }

    private int execute(@NotNull CommandContext<CommandSourceStack> context, int amount) {
        Player player = (Player) context.getSource().getSender();
        Citizen citizen = CacheManager.getInstance().getCitizen(player);
        if (citizen == null || citizen.getTown() == null) {
            MessageUtils.sendMessage(player, FormattingUtils.general("You are not part of any town and cannot withdraw money."));
            return 1;
        }

        Town town = citizen.getTown();
        if (!town.isTrusted(citizen)) {
            MessageUtils.sendMessage(player, FormattingUtils.general("Only the town leader or a deputy can withdraw money."));
            return 1;
        }

        BigDecimal withdrawAmount = BigDecimal.valueOf(amount);

        EconomyResponse response = EconomyManager.getEconomy().withdraw(
                Countries.getInstance().getName(), town.getUUID(), withdrawAmount
        );

        if (!response.transactionSuccess()) {
            MessageUtils.sendMessage(player, FormattingUtils.fail("Failed to withdraw money from the town: " + response.errorMessage));
            return 1;
        }

        EconomyResponse depositResponse = EconomyManager.getEconomy().deposit(
                Countries.getInstance().getName(), citizen.getUUID(), withdrawAmount
        );

        if (!depositResponse.transactionSuccess()) {
            EconomyResponse rollbackResponse = EconomyManager.getEconomy().deposit(
                    Countries.getInstance().getName(), town.getUUID(), withdrawAmount
            );

            if (!rollbackResponse.transactionSuccess()) {
                MessageUtils.sendMessage(player, FormattingUtils.fail("Critical error: Unable to deposit money to your account, and rollback failed. Contact support."));
                return 1;
            }

            MessageUtils.sendMessage(player, FormattingUtils.fail("Failed to deposit money to your account: " + depositResponse.errorMessage)
                    .append(FormattingUtils.fail("The amount has been refunded to the town's account.")));
            return 1;
        }

        MessageUtils.sendMessage(player, FormattingUtils.general("Successfully withdrew ")
                .append(FormattingUtils.balance(withdrawAmount.intValue()))
                .append(FormattingUtils.general(" from the town's bank account."))
        );

        return 1;
    }
}
