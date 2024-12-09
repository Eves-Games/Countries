package net.worldmc.countries.objects.interfaces;

import net.worldmc.countries.objects.Plot;

import java.util.List;

public interface PlotList {
    List<Plot> getPlots();
    boolean hasPlot(Plot plot);
    int getPlotCount();
}
