package net.worldmc.countries.commands.countries.subcommands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.worldmc.countries.managers.CacheManager;
import net.worldmc.countries.objects.Country;
import net.worldmc.countries.objects.Plot;
import net.worldmc.countries.objects.Town;
import net.worldmc.countries.util.FormattingUtils;
import net.worldmc.countries.util.LocationUtils;
import net.worldmc.countries.util.MessageUtils;
import net.worldmc.countries.util.ObjectComponents;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CreateCommand {
    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return Commands.literal("create")
                .then(Commands.argument("type", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            builder.suggest("town");
                            builder.suggest("country");
                            return builder.buildFuture();
                        })
                        .then(Commands.argument("name", StringArgumentType.greedyString())
                                .executes(this::execute)));
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        CommandSender sender = context.getSource().getSender();
        String type = context.getArgument("type", String.class).toLowerCase();
        String name = context.getArgument("name", String.class).trim();

        if (name.isEmpty() || name.length() > 32) {
            MessageUtils.sendMessage(sender, FormattingUtils.fail("The name must be between 1 and 32 characters."));
            return 1;
        }

        CacheManager manager = CacheManager.getInstance();

        switch (type) {
            case "town" -> {
                if (manager.getTown(name) != null) {
                    MessageUtils.sendMessage(sender, FormattingUtils.fail("A town with this name already exists. Please choose a different name."));
                    return 1;
                }
                Town town = new Town(UUID.randomUUID(), name);
                CacheManager.getInstance().registerTown(town);

                if (sender instanceof Player player) {
                    Location playerLocation = player.getLocation();
                    Location safeLocation = LocationUtils.getSafeLocation(playerLocation);

                    if (safeLocation != null) {
                        town.setSpawn(safeLocation);
                        Plot block = new Plot(player.getChunk().getChunkKey());
                        block.setTown(town);

                        Component info = FormattingUtils.general("Successfully created the town ")
                                .append(ObjectComponents.groupComponent(town))
                                .append(FormattingUtils.general(" with a spawn location at "))
                                .append(FormattingUtils.location(safeLocation));

                        MessageUtils.sendMessage(player, info);
                    } else {
                        Component info = FormattingUtils.general("Successfully created the town ")
                                .append(ObjectComponents.groupComponent(town))
                                .append(FormattingUtils.general(", but could not set a spawn location"));

                        MessageUtils.sendMessage(player, info);
                    }
                } else {
                    Component info = FormattingUtils.general("Successfully created the town ")
                            .append(ObjectComponents.groupComponent(town))
                            .append(FormattingUtils.general(", but could not set a spawn location or land"));

                    MessageUtils.sendMessage(sender, info);
                }
            }
            case "country" -> {
                if (manager.getCountry(name) != null) {
                    MessageUtils.sendMessage(sender, FormattingUtils.fail("A country with this name already exists. Please choose a different name."));
                    return 1;
                }
                Country country = new Country(UUID.randomUUID(), name);
                CacheManager.getInstance().registerCountry(country);
                MessageUtils.sendMessage(sender, FormattingUtils.general("Country '" + name + "' created successfully."));
            }
            default -> {
                MessageUtils.sendMessage(sender, FormattingUtils.fail("Invalid type. Use 'town', 'state', or 'country'."));
                return 1;
            }
        }

        return 1;
    }
}
