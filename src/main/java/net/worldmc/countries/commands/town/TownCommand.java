package net.worldmc.countries.commands.town;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.worldmc.countries.commands.town.subcommands.*;
import net.worldmc.countries.commands.town.subcommands.set.SetCommand;
import net.worldmc.countries.util.FormattingUtils;
import net.worldmc.countries.util.MessageUtils;

public class TownCommand {
    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return Commands.literal("town")
                .executes(context -> {
                    MessageUtils.sendMessage(context.getSource().getSender(), FormattingUtils.general("Usage: /town <subcommand>"));
                    return 1;
                })
                .then(new CreateCommand().getCommandNode())
                .then(new ClaimCommand().getCommandNode())
                .then(new UnclaimCommand().getCommandNode())
                .then(new SpawnCommand().getCommandNode())
                .then(new InfoCommand().getCommandNode())
                .then(new InviteCommand().getCommandNode())
                .then(new ListCommand().getCommandNode())
                .then(new DepositCommand().getCommandNode())
                .then(new WithdrawCommand().getCommandNode())
                .then(new SetCommand().getCommandNode());
    }
}
