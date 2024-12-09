package net.worldmc.countries.objects.interfaces;

import java.util.UUID;

public interface Identity {
    String getName();
    void setName(String name);
    UUID getUUID();
}
