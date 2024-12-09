package net.worldmc.countries.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

public class LocationUtils {

    /**
     * Attempts to find a safe location near the provided location.
     *
     * @param location The starting location.
     * @return A safe {@link Location} or {@code null} if none found.
     */
    public static Location getSafeLocation(Location location) {
        World world = location.getWorld();
        if (world == null) {
            return null;
        }

        int range = 22;
        int startY = location.getBlockY();

        for (int yOffset = -range; yOffset <= range; yOffset++) {
            int y = startY + yOffset;

            if (y < world.getMinHeight() || y > world.getMaxHeight()) {
                continue;
            }

            Location checkLocation = new Location(world, location.getX(), y, location.getZ());

            if (isSafeLocation(checkLocation)) {
                checkLocation.setX(checkLocation.getBlockX() + 0.5);
                checkLocation.setZ(checkLocation.getBlockZ() + 0.5);
                checkLocation.setYaw(location.getYaw());
                checkLocation.setPitch(location.getPitch());
                return checkLocation;
            }
        }

        return null;
    }

    /**
     * Checks if the given location is safe for a player to spawn.
     *
     * @param location The location to check.
     * @return {@code true} if the location is safe, {@code false} otherwise.
     */
    public static boolean isSafeLocation(Location location) {
        World world = location.getWorld();
        if (world == null) {
            return false;
        }

        Block block = world.getBlockAt(location);
        Block blockBelow = block.getRelative(0, -1, 0);
        Block blockAbove = block.getRelative(0, 1, 0);

        if (!block.isPassable() || !blockAbove.isPassable()) {
            return false;
        }

        if (!blockBelow.getType().isSolid()) {
            return false;
        }

        return !block.getType().isBurnable() && block.getType() != Material.LAVA && blockBelow.getType() != Material.LAVA;
    }
}
