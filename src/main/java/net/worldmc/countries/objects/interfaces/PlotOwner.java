package net.worldmc.countries.objects.interfaces;

import net.worldmc.countries.objects.Plot;

import java.util.List;

public interface PlotOwner extends PlotList {
    void addPlot(Plot plot);
    void removePlot(Plot plot);
}
