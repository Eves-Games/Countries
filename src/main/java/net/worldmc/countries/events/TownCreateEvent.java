package net.worldmc.countries.events;

import net.worldmc.countries.objects.Town;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class TownCreateEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();

    private final Town town;

    public TownCreateEvent(@NotNull Town town) {
        super(true);
        this.town = town;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }

    public @NotNull Town getTown() {
        return town;
    }
}
