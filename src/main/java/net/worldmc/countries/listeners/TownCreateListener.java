package net.worldmc.countries.listeners;

import net.worldmc.countries.events.TownCreateEvent;
import net.worldmc.countries.integrations.squaremap.SquareMapIntegration;
import net.worldmc.countries.objects.Plot;
import net.worldmc.countries.objects.Town;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.jpenilla.squaremap.api.Point;

import java.util.ArrayList;
import java.util.List;

public class TownCreateListener implements Listener {
    @EventHandler
    public void onCreateTown(TownCreateEvent event) {
        Town town = event.getTown();
        List<Plot> plots = town.getPlots();

        if (plots.isEmpty())
            return;

        World world = Bukkit.getWorld("world");
        if (world == null)
            return;

        List<Point> points = new ArrayList<>();
        for (Plot plot : plots) {
            int chunkX = plot.getChunk().getX() * 16;
            int chunkZ = plot.getChunk().getZ() * 16;

            points.add(Point.of(chunkX, chunkZ));
            points.add(Point.of(chunkX + 16, chunkZ));
            points.add(Point.of(chunkX + 16, chunkZ + 16));
            points.add(Point.of(chunkX, chunkZ + 16));
        }

        SquareMapIntegration.addRectangleMarker(
                "towns",
                "town-" + town.getUUID(),
                world,
                points.get(0),
                points.get(2),
                "Town: " + town.getName()
        );
    }
}
