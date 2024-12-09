package net.worldmc.countries.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Country extends Group {
    private final List<Town> towns = new ArrayList<>();
    private Town capital;

    public Country(UUID uuid, String name) { super(uuid, name); }

    // MemberList
    @Override
    public List<Citizen> getMembers() {
        return towns.stream()
                .flatMap(town -> town.getMembers().stream())
                .toList();
    }

    @Override
    public boolean hasMember(Citizen citizen) {
        return towns.stream()
                .anyMatch(town -> town.hasMember(citizen));
    }

    @Override
    public int getMemberCount() {
        return towns.stream()
                .mapToInt(Town::getMemberCount)
                .sum();
    }

    // PlotList
    @Override
    public List<Plot> getPlots() {
        return towns.stream()
                .flatMap(town -> town.getPlots().stream())
                .toList();
    }

    @Override
    public int getPlotCount() {
        return towns.stream()
                .mapToInt(Town::getPlotCount)
                .sum();
    }

    @Override
    public boolean hasPlot(Plot land) {
        return towns.stream()
                .anyMatch(town -> town.hasPlot(land));
    }

    // Default
    public List<Town> getTowns() { return towns; }

    public int getTownCount() { return towns.size(); }

    public boolean hasTown(Town town) { return towns.contains(town); }

    public void addTown(Town town) { towns.add(town); }

    public void removeTown(Town town) { towns.remove(town); }

    public void setCapital(Town town) { this.capital = town; }

    public Town getCapital() { return capital; }
}
