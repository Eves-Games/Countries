package net.worldmc.countries.commands.business.subcommands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.worldmc.countries.managers.CacheManager;
import net.worldmc.countries.objects.Business;
import net.worldmc.countries.objects.Citizen;
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
                            CacheManager.getInstance().getBusinesses().forEach(business -> builder.suggest(business.getName()));
                            return builder.buildFuture();
                        })
                        .executes(context -> execute(context, context.getArgument("name", String.class))));
    }

    private int execute(@NotNull CommandContext<CommandSourceStack> context, String businessName) {
        Player player = (Player) context.getSource().getSender();

        Business business;
        if (businessName == null || businessName.trim().isEmpty()) {
            Citizen citizen = CacheManager.getInstance().getCitizen(player);
            if (citizen == null || citizen.getBusiness() == null) {
                MessageUtils.sendMessage(player, FormattingUtils.general("You are not part of any business and need to specify a business name."));
                return 1;
            }
            business = citizen.getBusiness();
        } else {
            business = CacheManager.getInstance().getBusiness(businessName.trim());
            if (business == null) {
                MessageUtils.sendMessage(player, FormattingUtils.fail("A business with the name \"" + businessName + "\" was not found!"));
                return 1;
            }
        }

        Component info = FormattingUtils.object(business.getName())
                .appendNewline()
                .append(FormattingUtils.key("Owner: "))
                .append(ObjectComponents.citizenComponent(business.getLeader()))
                .appendNewline()
                .append(FormattingUtils.key("Bank Account: "))
                .append(FormattingUtils.balance(business.getBankAccount().getBalance()))
                .appendNewline()
                .append(FormattingUtils.key("Employees: "))
                .append(FormattingUtils.general(business.getMemberCount()))
                .appendNewline()
                .append(FormattingUtils.key("Plots: "))
                .append(FormattingUtils.general(business.getPlotCount()));

        MessageUtils.sendMessage(player, info);
        return 1;
    }
}
