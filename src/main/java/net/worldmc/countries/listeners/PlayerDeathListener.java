package net.worldmc.countries.listeners;

import net.kyori.adventure.text.Component;
import net.worldmc.countries.managers.CacheManager;
import net.worldmc.countries.objects.Citizen;
import net.worldmc.countries.objects.Plot;
import net.worldmc.countries.objects.Town;
import net.worldmc.countries.util.MessageUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        Citizen citizen = CacheManager.getInstance().getCitizen(player);

        if (citizen != null && citizen.getTown() != null) {
            Town town = citizen.getTown();
            Plot deathLocationPlot = CacheManager.getInstance().getPlot(player.getLocation().getChunk());

            if (deathLocationPlot != null) {
                Town deathTown = deathLocationPlot.getTown();

                if (town.equals(deathTown)) {
                    grantKeepInventory(event, player);
                } else if (deathTown.getCountry() != null && town.getCountry() != null) {
                    grantKeepInventory(event, player);
                }
            }
        }
    }

    private void grantKeepInventory(PlayerDeathEvent event, Player player) {
        event.setKeepInventory(true);
        event.setKeepLevel(true);
        MessageUtils.sendMessage(player, Component.text("You died in friendly territory and kept your inventory."));
    }
}
