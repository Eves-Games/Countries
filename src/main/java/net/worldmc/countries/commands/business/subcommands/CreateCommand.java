package net.worldmc.countries.commands.business.subcommands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import com.mojang.brigadier.context.CommandContext;
import net.kyori.adventure.text.Component;
import net.worldmc.countries.managers.CacheManager;
import net.worldmc.countries.objects.Business;
import net.worldmc.countries.objects.Citizen;
import net.worldmc.countries.objects.enumerations.Rank;
import net.worldmc.countries.util.FormattingUtils;
import net.worldmc.countries.util.MessageUtils;
import net.worldmc.countries.util.ObjectComponents;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CreateCommand {
    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return Commands.literal("create")
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .executes(this::execute));
    }

    private int execute(@NotNull CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Citizen citizen = CacheManager.getInstance().getCitizen(player);

        if (citizen.getBusiness() != null) {
            MessageUtils.sendMessage(player, FormattingUtils.fail("You already a member of a business and cannot create a new one."));
            return 1;
        }

        String name = context.getArgument("name", String.class);
        if (name.isEmpty() || name.length() > 32) {
            MessageUtils.sendMessage(player, FormattingUtils.fail("The business name must be between 1 and 32 characters."));
            return 1;
        }

        if (CacheManager.getInstance().getBusiness(name) != null) {
            MessageUtils.sendMessage(player, FormattingUtils.fail("A business with this name already exists. Please choose a different name."));
            return 1;
        }

        Business business = new Business(UUID.randomUUID(), name);

        citizen.setBusiness(business);
        business.setRank(citizen, Rank.LEADER);

        CacheManager.getInstance().registerBusiness(business);

        MessageUtils.sendMessage(player, FormattingUtils.general("Successfully created the business ")
                .append(ObjectComponents.groupComponent(business)));

        return 1;
    }
}
