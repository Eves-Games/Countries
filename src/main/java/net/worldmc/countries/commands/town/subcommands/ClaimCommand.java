package net.worldmc.countries.commands.town.subcommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import com.mojang.brigadier.context.CommandContext;
import net.worldmc.countries.managers.CacheManager;
import net.worldmc.countries.objects.Citizen;
import net.worldmc.countries.objects.Plot;
import net.worldmc.countries.objects.Town;
import net.worldmc.countries.util.ChunkUtils;
import net.worldmc.countries.util.FormattingUtils;
import net.worldmc.countries.util.MessageUtils;
import net.worldmc.countries.util.ObjectComponents;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClaimCommand {
    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return Commands.literal("claim").executes(this::execute);
    }

    private int execute(@NotNull CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Citizen citizen = CacheManager.getInstance().getCitizen(player);
        if (citizen == null || citizen.getTown() == null) {
            MessageUtils.sendMessage(player, FormattingUtils.fail("You are not part of any town and cannot claim plots."));
            return 1;
        }

        Town town = citizen.getTown();
        if (!town.isTrusted(citizen)) {
            MessageUtils.sendMessage(player,  FormattingUtils.fail("Only the town leader, deputies, or elders can claim plots for your town."));
            return 1;
        }

        Chunk currentChunk = player.getLocation().getChunk();
        if (CacheManager.getInstance().getPlot(currentChunk) != null) {
            MessageUtils.sendMessage(player,  FormattingUtils.fail("This plot is already claimed."));
            return 1;
        }

        if (town.getPlotCount() != 0 && !ChunkUtils.isAdjacentToAny(town.getPlots(), currentChunk)) {
            MessageUtils.sendMessage(player,  FormattingUtils.fail("You can only claim plots adjacent to existing plots owned by your town."));
            return 1;
        }

        if (town.getPlotCount() >= town.getMaxBlocks()) {
            MessageUtils.sendMessage(player,  FormattingUtils.fail("Your town has reached the maximum number of plots allowed."));
            return 1;
        }

        Plot plot = new Plot(currentChunk.getChunkKey());
        plot.setTown(town);

        CacheManager.getInstance().registerPlot(plot);

        MessageUtils.sendMessage(player,  FormattingUtils.general("Plot claimed successfully for")
                .appendSpace()
                .append(ObjectComponents.groupComponent(town))
        );
        return 1;
    }
}
