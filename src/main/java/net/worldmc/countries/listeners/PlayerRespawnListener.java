package net.worldmc.countries.listeners;

import net.worldmc.countries.managers.CacheManager;
import net.worldmc.countries.objects.Citizen;
import net.worldmc.countries.objects.Town;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        Location bedSpawn = player.getPotentialBedLocation();
        if (bedSpawn != null) {
            event.setRespawnLocation(bedSpawn);
            return;
        }

        Citizen citizen = CacheManager.getInstance().getCitizen(player);
        if (citizen != null) {
            Town town = citizen.getTown();
            if (town != null && town.getSpawn() != null) {
                event.setRespawnLocation(town.getSpawn());
            }
        }
    }
}
