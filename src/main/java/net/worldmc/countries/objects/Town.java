package net.worldmc.countries.objects;

import net.worldmc.countries.objects.interfaces.MemberOwner;
import net.worldmc.countries.objects.interfaces.PlotOwner;

import java.util.*;

public class Town extends Group implements PlotOwner, MemberOwner {
    private final List<Citizen> members = new ArrayList<>();
    private final List<Plot> plots = new ArrayList<>();
    private Country country;

    public Town(UUID uuid, String name) { super(uuid, name); }

    // MemberList
    @Override
    public List<Citizen> getMembers() { return new ArrayList<>(members); }

    @Override
    public int getMemberCount() { return members.size(); }

    @Override
    public boolean hasMember(Citizen member) { return members.contains(member); }

    // MemberOwner
    @Override
    public void addMember(Citizen member) { members.add(member); }

    @Override
    public void removeMember(Citizen member) { members.remove(member); }

    // PlotList
    @Override
    public List<Plot> getPlots() {
        return plots;
    }

    @Override
    public int getPlotCount() { return plots.size(); }

    @Override
    public boolean hasPlot(Plot land) { return plots.contains(land); }

    // PlotOwner
    @Override
    public void addPlot(Plot plot) { plots.add(plot); }

    @Override
    public void removePlot(Plot plot) {
        plots.remove(plot);
    }

    // Default
    public int getMaxBlocks() { return getMemberCount() * 20; }

    public void setCountry(Country country) {
        if (this.country != null && this.country != country) {
            this.country.removeTown(this);
        }
        this.country = country;
    }

    public Country getCountry() { return country; }
}
