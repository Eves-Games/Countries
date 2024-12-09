package net.worldmc.countries.commands.business;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.worldmc.countries.commands.business.subcommands.CreateCommand;
import net.worldmc.countries.commands.business.subcommands.InfoCommand;
import net.worldmc.countries.commands.business.subcommands.ListCommand;
import net.worldmc.countries.util.FormattingUtils;
import net.worldmc.countries.util.MessageUtils;

public class BusinessCommand {
    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return Commands.literal("business")
                .executes(context -> {
                    MessageUtils.sendMessage(context.getSource().getSender(), FormattingUtils.general("Usage: /business <subcommand>"));
                    return 1;
                })
                .then(new CreateCommand().getCommandNode())
                .then(new ListCommand().getCommandNode())
                .then(new InfoCommand().getCommandNode());
    }
}
