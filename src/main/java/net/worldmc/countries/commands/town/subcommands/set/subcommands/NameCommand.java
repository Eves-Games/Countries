package net.worldmc.countries.commands.town.subcommands.set.subcommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.worldmc.countries.managers.CacheManager;
import net.worldmc.countries.objects.Citizen;
import net.worldmc.countries.objects.Town;
import net.worldmc.countries.util.FormattingUtils;
import net.worldmc.countries.util.MessageUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NameCommand {
    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return Commands.literal("name")
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .executes(this::execute));
    }

    private int execute(@NotNull CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Citizen citizen = CacheManager.getInstance().getCitizen(player);
        Town town = citizen.getTown();

        String newName = StringArgumentType.getString(context, "name").trim();
        if (newName.isEmpty() || newName.length() > 32) {
            MessageUtils.sendMessage(player, FormattingUtils.fail("The new town name must be between 1 and 32 characters!"));
            return 1;
        }

        town.setName(newName);
        MessageUtils.sendMessage(player, FormattingUtils.general("Town name set successfully to \"" + newName + "\"."));
        return 1;
    }
}
