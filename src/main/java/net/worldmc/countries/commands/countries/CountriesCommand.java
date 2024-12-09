package net.worldmc.countries.commands.countries;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.worldmc.countries.commands.countries.subcommands.CreateCommand;
import net.worldmc.countries.commands.countries.subcommands.DeleteCommand;
import net.worldmc.countries.util.FormattingUtils;
import net.worldmc.countries.util.MessageUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CountriesCommand {
    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return Commands.literal("countries")
                .requires(source -> {
                    CommandSender sender = source.getSender();
                    return !(sender instanceof Player) || sender.isOp();
                })
                .executes(context -> {
                    MessageUtils.sendMessage(context.getSource().getSender(), FormattingUtils.general("Usage: /countries <subcommand>"));
                    return 1;
                })
                .then(new CreateCommand().getCommandNode())
                .then(new DeleteCommand().getCommandNode());
    }
}
