package net.worldmc.countries.commands.town.subcommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import com.mojang.brigadier.context.CommandContext;
import net.kyori.adventure.text.Component;
import net.worldmc.countries.managers.CacheManager;
import net.worldmc.countries.objects.Citizen;
import net.worldmc.countries.objects.Town;
import net.worldmc.countries.util.FormattingUtils;
import net.worldmc.countries.util.MessageUtils;
import net.worldmc.countries.util.ObjectComponents;
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
        if (citizen == null || citizen.getTown() == null) {
            MessageUtils.sendMessage(player, FormattingUtils.general("You are not part of any town and cannot teleport to a town spawn."));
            return 1;
        }

        Town town = citizen.getTown();
        Location spawnLocation = town.getSpawn();
        if (spawnLocation == null) {
            MessageUtils.sendMessage(player, FormattingUtils.general("The spawn location for your town is not set."));
            return 1;
        }

        player.teleportAsync(spawnLocation);
        MessageUtils.sendMessage(player, FormattingUtils.general("Teleported to the spawn of ")
                .append(ObjectComponents.groupComponent(town)));
        return 1;
    }
}
