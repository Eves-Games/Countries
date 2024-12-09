package net.worldmc.countries.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;

public class FormattingUtils {
    public static Component key(String text) {
        return Component.text(text, NamedTextColor.GRAY);
    }

    public static Component object(String text) {
        return Component.text(text, NamedTextColor.AQUA);
    }

    public static Component balance(int amount) {
        return Component.text("$" + amount, NamedTextColor.YELLOW);
    }

    public static Component data(Object input) {
        String text = input != null ? input.toString() : "null";
        return Component.text("(" + text + ")", NamedTextColor.GREEN);
    }

    public static Component fail(String text) {
        return Component.text(text, NamedTextColor.RED);
    }

    public static Component general(Object input) {
        String text = input != null ? input.toString() : "null";
        return Component.text(text, NamedTextColor.WHITE);
    }

    public static Component location(Location location) {
        return Component.text(location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ(), NamedTextColor.BLUE);
    }
}
