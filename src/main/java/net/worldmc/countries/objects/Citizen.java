package net.worldmc.countries.objects;

import net.worldmc.countries.objects.enumerations.Rank;
import net.worldmc.countries.objects.interfaces.Identity;
import net.worldmc.countries.objects.interfaces.PlotOwner;
import org.bukkit.Bukkit;

import java.util.*;

public class Citizen implements PlotOwner, Identity {
    private final UUID uuid;
    private Town town;
    private Business business;
    private final List<Plot> plots = new ArrayList<>();

    public Citizen(UUID uuid) { this.uuid = uuid; }

    // Identity
    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public String getName() { return Bukkit.getOfflinePlayer(uuid).getName(); }

    @Override
    public void setName(String name) { throw new UnsupportedOperationException("Name cannot be changed."); }

    // PlotList
    @Override
    public List<Plot> getPlots() {
        return plots;
    }

    @Override
    public int getPlotCount() { return plots.size(); }

    @Override
    public boolean hasPlot(Plot block) { return plots.contains(block); }

    // PlotOwner
    @Override
    public void addPlot(Plot plot) { plots.add(plot); }

    @Override
    public void removePlot(Plot plot) {
        plots.remove(plot);
    }

    // Default
    public Town getTown() {
        return town;
    }

    public Business getBusiness() {
        return business;
    }

    public void setTown(Town town) {
        if (this.town != null) {
            this.town.removeMember(this);
        }
        if (town != null) {
            town.addMember(this);
        }
        this.town = town;
    }

    public void setBusiness(Business business) {
        if (this.business != null) {
            this.business.removeMember(this);
        }
        if (business != null) {
            business.addMember(this);
        }
        this.business = business;
    }

    public Rank getGroupRank(Group group) {
        return group.getRank(this);
    }
}
