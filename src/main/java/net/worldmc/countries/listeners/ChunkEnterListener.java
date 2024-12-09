package net.worldmc.countries.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.worldmc.countries.managers.SidebarManager;
import net.worldmc.countries.objects.*;
import net.worldmc.countries.managers.CacheManager;
import net.worldmc.countries.util.ObjectComponents;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;

public class ChunkEnterListener implements Listener {

    private final Map<Player, SidebarManager> sidebars = new HashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Chunk newChunk = event.getTo().getChunk();
        Chunk oldChunk = event.getFrom().getChunk();

        if (newChunk.equals(oldChunk)) return;

        updateSidebar(player, newChunk);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Chunk currentChunk = player.getLocation().getChunk();
        updateSidebar(player, currentChunk);
    }

    private void updateSidebar(Player player, Chunk chunk) {
        Plot plot = CacheManager.getInstance().getPlot(chunk);

        Country country = (plot != null && plot.getTown() != null) ? plot.getTown().getCountry() : null;
        Town town = (plot != null) ? plot.getTown() : null;
        Object owner = (plot != null) ? plot.getOwner() : null;

        SidebarManager sidebar = getSidebar(player);
        sidebar.clearFields();

        int line = 15;

        if (country != null) {
            sidebar.updateField("country", ObjectComponents.groupComponent(country), line--);
        }

        if (town != null) {
            sidebar.updateField("town", ObjectComponents.groupComponent(town), line--);
        } else {
            sidebar.updateField("town", Component.text("Wilderness", NamedTextColor.DARK_GREEN), line--);
        }

        if (owner instanceof Citizen) {
            sidebar.updateField("owner", ObjectComponents.citizenComponent((Citizen) owner), line--);
        } else if (owner instanceof Business) {
            sidebar.updateField("owner", ObjectComponents.groupComponent((Business) owner), line--);
        }

        sidebar.displayTo(player);
    }

    private SidebarManager getSidebar(Player player) {
        return sidebars.computeIfAbsent(player, p -> new SidebarManager());
    }
}