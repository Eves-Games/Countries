package net.worldmc.countries.commands.countries.subcommands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.worldmc.countries.managers.ConfirmationManager;
import net.worldmc.countries.managers.CacheManager;
import net.worldmc.countries.objects.Town;
import net.worldmc.countries.util.MessageUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DeleteCommand {
    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return Commands.literal("delete")
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .suggests((context, builder) -> {
                            List<String> townNames = CacheManager.getInstance().getTowns().stream()
                                    .map(Town::getName)
                                    .toList();
                            for (String name : townNames) {
                                builder.suggest(name);
                            }
                            return CompletableFuture.completedFuture(builder.build());
                        })
                        .executes(this::execute));
    }

    private int execute(@NotNull CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        String townName = context.getArgument("name", String.class).trim();

        CacheManager manager = CacheManager.getInstance();
        Town town = manager.getTown(townName);

        if (town == null) {
            MessageUtils.sendMessage(sender, Component.text("The town \"" + townName + "\" does not exist."));
            return 1;
        }

        if (sender instanceof Player player) {
            ConfirmationManager.addConfirmation(player, confirmedPlayer -> {
                manager.deleteTown(town);
                MessageUtils.sendMessage(player, Component.text("The town \"" + town.getName() + "\" has been deleted successfully."));
            });
        } else {
            manager.deleteTown(town);
            MessageUtils.sendMessage(sender, Component.text("The town \"" + town.getName() + "\" has been deleted successfully."));
        }
        return 1;
    }
}
