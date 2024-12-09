package net.worldmc.countries.events;

import net.worldmc.countries.objects.Plot;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TownPreUnclaimEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();

    private final Plot plot;

    public TownPreUnclaimEvent(@NotNull Plot plot) {
        super(true);
        this.plot = plot;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    public @NotNull Plot getPlot() {
        return plot;
    }
}
