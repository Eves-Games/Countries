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

public class DepositCommand {
    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return Commands.literal("deposit")
                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                        .executes(context -> execute(context, context.getArgument("amount", Integer.class))));
    }

    private int execute(@NotNull CommandContext<CommandSourceStack> context, int amount) {
        Player player = (Player) context.getSource().getSender();
        Citizen citizen = CacheManager.getInstance().getCitizen(player);
        if (citizen == null || citizen.getTown() == null) {
            MessageUtils.sendMessage(player, FormattingUtils.general("You are not part of any town and cannot deposit money."));
            return 1;
        }

        Town town = citizen.getTown();
        if (!town.isTrusted(citizen)) {
            MessageUtils.sendMessage(player, FormattingUtils.general("Only the town leader or a deputy can withdraw money."));
            return 1;
        }

        BigDecimal depositAmount = BigDecimal.valueOf(amount);

        EconomyResponse takeDeposit = EconomyManager.getEconomy().withdraw(
                Countries.getInstance().getName(), citizen.getUUID(), depositAmount
        );

        if (!takeDeposit.transactionSuccess()) {
            MessageUtils.sendMessage(player, FormattingUtils.fail("Failed to withdraw money from your account: " + takeDeposit.errorMessage));
            return 1;
        }

        EconomyResponse giveDeposit = EconomyManager.getEconomy().deposit(
                Countries.getInstance().getName(), town.getUUID(), depositAmount
        );


        if (!giveDeposit.transactionSuccess()) {
            EconomyResponse refundResponse = EconomyManager.getEconomy().deposit(
                    Countries.getInstance().getName(), citizen.getUUID(), depositAmount
            );

            if (!refundResponse.transactionSuccess()) {
                MessageUtils.sendMessage(player, FormattingUtils.fail("Critical error: Unable to deposit money to the town, and rollback failed. Contact support."));
                return 1;
            }

            MessageUtils.sendMessage(player, FormattingUtils.fail("Failed to deposit money to the town: " + giveDeposit.errorMessage)
                    .append(FormattingUtils.fail("The amount has been refunded to your account.")));
            return 1;
        }

        MessageUtils.sendMessage(player, FormattingUtils.general("Successfully deposited ")
                        .append(FormattingUtils.balance(depositAmount.intValue()))
                        .append(FormattingUtils.general(" to the town's bank account."))
        );

        return 1;
    }
}
