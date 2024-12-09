package net.worldmc.countries.commands.citizen;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.worldmc.countries.commands.citizen.subcommands.InfoCommand;
import net.worldmc.countries.util.FormattingUtils;
import net.worldmc.countries.util.MessageUtils;

public class CitizenCommand {
    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return Commands.literal("citizen")
                .executes(context -> {
                    MessageUtils.sendMessage(context.getSource().getSender(), FormattingUtils.general("Usage: /citizen <subcommand>"));
                    return 1;
                })
                .then(new InfoCommand().getCommandNode());
    }
}
