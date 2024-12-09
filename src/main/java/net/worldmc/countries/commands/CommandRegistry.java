package net.worldmc.countries.commands;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.worldmc.countries.Countries;
import net.worldmc.countries.commands.business.BusinessCommand;
import net.worldmc.countries.commands.citizen.CitizenCommand;
import net.worldmc.countries.commands.countries.CountriesCommand;
import net.worldmc.countries.commands.plot.PlotCommand;
import net.worldmc.countries.commands.town.TownCommand;
import net.worldmc.countries.commands.utility.AcceptCommand;
import net.worldmc.countries.commands.utility.CancelCommand;
import net.worldmc.countries.commands.utility.ConfirmCommand;
import net.worldmc.countries.commands.utility.DenyCommand;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class CommandRegistry {
    private final Countries plugin;

    public CommandRegistry(Countries plugin) {
        this.plugin = plugin;
    }

    public void registerCommands() {
        LifecycleEventManager<Plugin> manager = plugin.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            commands.register(
                    new ConfirmCommand().getCommandNode().build(),
                    "Confirm a pending action"
            );

            commands.register(
                    new CancelCommand().getCommandNode().build(),
                    "Cancel a pending action"
            );

            commands.register(
                    new AcceptCommand().getCommandNode().build(),
                    "Accept a town invite"
            );

            commands.register(
                    new DenyCommand().getCommandNode().build(),
                    "Deny a town invite"
            );

            commands.register(
                    new TownCommand().getCommandNode().build(),
                    "Interact with towns using various subcommands",
                    List.of("t")
            );

            commands.register(
                    new BusinessCommand().getCommandNode().build(),
                    "Interact with businesses using various subcommands",
                    List.of("b")
            );

            commands.register(
                    new PlotCommand().getCommandNode().build(),
                    "Interact with lands using various subcommands",
                    List.of("l")
            );

            commands.register(
                    new CitizenCommand().getCommandNode().build(),
                    "Interact with citizens using various subcommands"
            );

            commands.register(
                    new CountriesCommand().getCommandNode().build(),
                    "Manage the Countries plugin using various subcommands"
            );
        });
    }
}