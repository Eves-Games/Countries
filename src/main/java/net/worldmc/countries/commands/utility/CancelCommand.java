package net.worldmc.countries.commands.utility;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.worldmc.countries.managers.ConfirmationManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CancelCommand {
    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return Commands.literal("cancel").executes(this::execute);
    }

    private int execute(@NotNull CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        ConfirmationManager.cancel(player);
        return 1;
    }
}
