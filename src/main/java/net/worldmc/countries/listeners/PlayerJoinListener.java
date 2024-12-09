package net.worldmc.countries.listeners;

import net.worldmc.countries.managers.CacheManager;
import net.worldmc.countries.objects.Citizen;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (CacheManager.getInstance().getCitizen(player) == null) {
            Citizen citizen = new Citizen(player.getUniqueId());
            CacheManager.getInstance().registerCitizen(citizen);
        }
    }
}
