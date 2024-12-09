package net.worldmc.countries.commands.town.subcommands.set.subcommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import com.mojang.brigadier.context.CommandContext;
import net.kyori.adventure.text.Component;
import net.worldmc.countries.managers.CacheManager;
import net.worldmc.countries.objects.Citizen;
import net.worldmc.countries.objects.Town;
import net.worldmc.countries.util.FormattingUtils;
import net.worldmc.countries.util.LocationUtils;
import net.worldmc.countries.util.MessageUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpawnCommand {
    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return Commands.literal("spawn").executes(this::execute);
    }

    private int execute(@NotNull CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Citizen citizen = CacheManager.getInstance().getCitizen(player);
        Town town = citizen.getTown();

        Location currentLocation = player.getLocation();
        Location safeLocation = LocationUtils.getSafeLocation(currentLocation);

        if (safeLocation == null) {
            MessageUtils.sendMessage(player, FormattingUtils.fail("The current location is not safe for setting the spawn!"));
            return 1;
        }

        town.setSpawn(safeLocation);
        MessageUtils.sendMessage(player, FormattingUtils.general("Town spawn set successfully at your current location."));
        return 1;
    }
}
