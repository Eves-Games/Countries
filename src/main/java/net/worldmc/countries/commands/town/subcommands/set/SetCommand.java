package net.worldmc.countries.commands.town.subcommands.set;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.worldmc.countries.commands.town.subcommands.set.subcommands.NameCommand;
import net.worldmc.countries.commands.town.subcommands.set.subcommands.SpawnCommand;
import net.worldmc.countries.util.FormattingUtils;
import net.worldmc.countries.util.MessageUtils;

public class SetCommand {
    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return Commands.literal("set")
                .executes(context -> {
                    MessageUtils.sendMessage(context.getSource().getSender(), FormattingUtils.general("Usage: /town set <subcommand>"));
                    return 1;
                })
                .then(new SpawnCommand().getCommandNode())
                .then(new NameCommand().getCommandNode());
    }
}
