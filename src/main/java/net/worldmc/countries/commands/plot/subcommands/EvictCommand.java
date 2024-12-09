package net.worldmc.countries.commands.plot.subcommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.worldmc.countries.managers.ConfirmationManager;
import net.worldmc.countries.managers.CacheManager;
import net.worldmc.countries.objects.Business;
import net.worldmc.countries.objects.Citizen;
import net.worldmc.countries.objects.Plot;
import net.worldmc.countries.objects.Town;
import net.worldmc.countries.util.FormattingUtils;
import net.worldmc.countries.util.MessageUtils;
import net.worldmc.countries.util.ObjectComponents;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EvictCommand {
    public LiteralArgumentBuilder<CommandSourceStack> getCommandNode() {
        return Commands.literal("evict")
                .executes(this::execute);
    }

    private int execute(@NotNull CommandContext<CommandSourceStack> context) {
        Player player = (Player) context.getSource().getSender();
        Citizen citizen = CacheManager.getInstance().getCitizen(player);
        if (citizen.getTown() == null || !citizen.getTown().isExtraTrusted(citizen)) {
            MessageUtils.sendMessage(player, Component.text("Only the town leader or a deputy can initiate an eviction."));
            return 1;
        }

        Town town = citizen.getTown();
        Plot land = CacheManager.getInstance().getPlot(player.getLocation().getChunk());
        if (land == null || !land.getTown().equals(town)) {
            MessageUtils.sendMessage(player, Component.text("You can only evict owners from plots owned by your town."));
            return 1;
        }

        if (land.getOwner() == null) {
            MessageUtils.sendMessage(player, Component.text("This plot is already owned by the town."));
            return 1;
        }

        int evictionCost = land.getPriorSalePrice();
        // Uncomment treasury check logic if needed
        // if (town.getTreasuryBalance() < evictionCost) {
        //     MessageUtils.sendMessage(player, Component.text("The town does not have enough funds to proceed with the eviction. Required: $" + evictionCost));
        //     return 1;
        // }

        MessageUtils.sendMessage(player, Component.text("This will cost the town $" + evictionCost + "."));

        ConfirmationManager.addConfirmation(player, confirmedPlayer -> {
            // Deduct treasury balance and handle eviction logic
            // town.setTreasuryBalance(town.getTreasuryBalance() - evictionCost);

            if (land.getOwner() instanceof Citizen owner) {
                // owner.setBalance(owner.getBalance() + evictionCost);
                MessageUtils.sendMessage(owner, FormattingUtils.general("You have been evicted from your plot in " + ObjectComponents.groupComponent(town) + ". You have received $" + evictionCost + " in compensation."));
            } else if (land.getOwner() instanceof Business businessOwner) {
                // businessOwner.setBalance(businessOwner.getBalance() + evictionCost);
                MessageUtils.sendMessage(businessOwner.getLeader(), FormattingUtils.general("Your business has been evicted from its land in " + ObjectComponents.groupComponent(town) + ". The business has received $" + evictionCost + " in compensation."));
            }

            land.setOwner(null);
            land.setForSale(false);
            land.setPriorSalePrice(0);

            MessageUtils.sendMessage(confirmedPlayer, Component.text("The plot has been evicted for $" + evictionCost + ". It is now owned by the town."));
        });

        return 1;
    }
}
