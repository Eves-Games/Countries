package net.worldmc.countries.util;

import net.worldmc.countries.objects.Country;
import net.worldmc.countries.objects.Plot;
import net.worldmc.countries.objects.Town;
import org.bukkit.Chunk;

import java.util.List;

public class ChunkUtils {
    public static boolean isAdjacent(Chunk chunk1, Chunk chunk2) {
        int xDiff = Math.abs(chunk1.getX() - chunk2.getX());
        int zDiff = Math.abs(chunk1.getZ() - chunk2.getZ());

        return (xDiff == 1 && zDiff == 0) || (zDiff == 1 && xDiff == 0);
    }

    public static boolean isAdjacentToAny(List<Plot> existingPlots, Chunk chunk) {
        for (Plot plot : existingPlots) {
            if (isAdjacent(plot.getChunk(), chunk)) {
                return true;
            }
        }
        return false;
    }

    public static boolean areTownsAdjacent(Town town1, Town town2) {
        List<Plot> town1Plots = town1.getPlots();

        for (Plot plot1 : town1Plots) {
            if (isAdjacentToAny(town2.getPlots(), plot1.getChunk())) {
                return true;
            }
        }
        return false;
    }

    public static boolean areCountriesAdjacent(Country country1, Country country2) {
        List<Plot> country1Plots = country1.getPlots();

        for (Plot plot1 : country1Plots) {
            if (isAdjacentToAny(country2.getPlots(), plot1.getChunk())) {
                return true;
            }
        }
        return false;
    }
}
