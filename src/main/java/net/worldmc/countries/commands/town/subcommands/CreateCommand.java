package net.worldmc.countries.commands.town.subcommands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import com.mojang.brigadier.context.CommandContext;
import net.kyori.adventure.text.Component;
import net.worldmc.countries.events.TownCreateEvent;
import net.worldmc.countries.managers.CacheManager;
import net.worldmc.countries.objects.Citizen;
import net.worldmc.countries.objects.Plot;
import net.worldmc.countries.objects.Town;
import net.worldmc.countries.objects.enumerations.Rank;
import net.worldmc.countries.util.FormattingUtils;
import net.worldmc.countries.util.LocationUtils;
import net.worldmc.countries.util.MessageUtils;
import net.worldmc.countries.util.ObjectComponents;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CreateCommand {
    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return Commands.literal("create")
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .executes(this::execute));
    }

    private int execute(@NotNull CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Citizen citizen = CacheManager.getInstance().getCitizen(player);
        if (citizen.getTown() != null) {
            MessageUtils.sendMessage(player, FormattingUtils.fail("You are already a member of a town and cannot create a new one."));
            return 1;
        }

        String name = context.getArgument("name", String.class);
        if (name.isEmpty() || name.length() > 32) {
            MessageUtils.sendMessage(player, FormattingUtils.fail("The town name must be between 1 and 32 characters."));
            return 1;
        }

        if (CacheManager.getInstance().getTown(name) != null) {
            MessageUtils.sendMessage(player, FormattingUtils.fail("A town with this name already exists. Please choose a different name."));
            return 1;
        }

        Location proposedLocation = player.getLocation();
        Location safeLocation = LocationUtils.getSafeLocation(proposedLocation);
        if (safeLocation != null) {
            Town town = new Town(UUID.randomUUID(), name);
            town.setSpawn(safeLocation);

            citizen.setTown(town);
            town.setRank(citizen, Rank.LEADER);

            Plot plot = new Plot(player.getChunk().getChunkKey());
            plot.setTown(town);

            CacheManager.getInstance().registerTown(town);
            CacheManager.getInstance().registerPlot(plot);

            Bukkit.getPluginManager().callEvent(new TownCreateEvent(town));

            Component info = FormattingUtils.general("Successfully created the town ")
                    .append(ObjectComponents.groupComponent(town))
                    .append(FormattingUtils.general(" with a spawn location at "))
                    .append(FormattingUtils.location(safeLocation));

            MessageUtils.sendMessage(player, info);
        } else {
            MessageUtils.sendMessage(player, FormattingUtils.fail("Could not find a safe location for your town's spawn point."));
        }

        return 1;
    }
}
