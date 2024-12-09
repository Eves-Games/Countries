package net.worldmc.countries.commands.plot;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.worldmc.countries.commands.plot.subcommands.BuyCommand;
import net.worldmc.countries.commands.plot.subcommands.EvictCommand;
import net.worldmc.countries.commands.plot.subcommands.SellCommand;
import net.worldmc.countries.util.FormattingUtils;
import net.worldmc.countries.util.MessageUtils;

public class PlotCommand {
    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return Commands.literal("plot")
                .executes(context -> {
                    MessageUtils.sendMessage(context.getSource().getSender(), FormattingUtils.general("Usage: /plot <subcommand>"));
                    return 1;
                })
                .then(new SellCommand().getCommandNode())
                .then(new BuyCommand().getCommandNode())
                .then(new EvictCommand().getCommandNode());
    }
}
