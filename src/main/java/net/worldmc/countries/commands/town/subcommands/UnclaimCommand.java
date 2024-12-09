package net.worldmc.countries.commands.town.subcommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import com.mojang.brigadier.context.CommandContext;
import net.kyori.adventure.text.Component;
import net.worldmc.countries.managers.ConfirmationManager;
import net.worldmc.countries.managers.CacheManager;
import net.worldmc.countries.objects.Citizen;
import net.worldmc.countries.objects.Plot;
import net.worldmc.countries.objects.Town;
import net.worldmc.countries.util.MessageUtils;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UnclaimCommand {
    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return Commands.literal("unclaim").executes(this::execute);
    }

    private int execute(@NotNull CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Citizen citizen = CacheManager.getInstance().getCitizen(player);
        if (citizen == null || citizen.getTown() == null) {
            MessageUtils.sendMessage(player, Component.text("You are not part of any town and cannot unclaim land."));
            return 1;
        }

        Town town = citizen.getTown();
        if (!town.isExtraTrusted(citizen)) {
            MessageUtils.sendMessage(player, Component.text("Only the town leader or deputies can unclaim land for your town."));
            return 1;
        }

        Chunk currentChunk = player.getLocation().getChunk();
        Plot block = CacheManager.getInstance().getPlot(currentChunk);
        if (block == null || !block.getTown().equals(town)) {
            MessageUtils.sendMessage(player, Component.text("This land is not owned by your town, so you cannot unclaim it."));
            return 1;
        }

        ConfirmationManager.addConfirmation(player, confirmedPlayer -> {
            CacheManager.getInstance().deletePlot(block);
            MessageUtils.sendMessage(confirmedPlayer, Component.text("Land successfully unclaimed."));
        });

        return 1;
    }
}
