package net.worldmc.countries.commands.utility;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.worldmc.countries.managers.InviteManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AcceptCommand {
    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return Commands.literal("accept")
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .suggests((context, builder) -> {
                            CommandSender sender = context.getSource().getSender();
                            if (sender instanceof Player player) {
                                InviteManager.getPendingInviteNames(player).forEach(builder::suggest);
                            }
                            return builder.buildFuture();
                        })
                        .executes(this::execute));
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();

        String townName = context.getArgument("name", String.class).trim();
        InviteManager.acceptInvite(player, townName);
        return 1;
    }
}
