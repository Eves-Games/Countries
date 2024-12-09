package net.worldmc.countries.objects;

import net.worldmc.countries.objects.interfaces.MemberOwner;
import net.worldmc.countries.objects.interfaces.PlotOwner;

import java.util.*;

public class Business extends Group implements PlotOwner, MemberOwner {
    private final List<Citizen> members = new ArrayList<>();
    private final List<Plot> plots = new ArrayList<>();

    public Business(UUID uuid, String name) { super(uuid, name); }

    // MemberList
    @Override
    public List<Citizen> getMembers() { return members; }

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
    public void addPlot(Plot land) { plots.add(land); }

    @Override
    public void removePlot(Plot block) {
        plots.remove(block);
    }
}
