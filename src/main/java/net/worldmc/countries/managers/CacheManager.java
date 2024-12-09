package net.worldmc.countries.managers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.worldmc.countries.objects.*;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class CacheManager {
    private static CacheManager instance = new CacheManager();

    private Map<UUID, Country> countries;
    private Map<UUID, Town> towns;
    private Map<UUID, Business> businesses;
    private Map<Long, Plot> plots;
    private Map<UUID, Citizen> citizens;

    private CacheManager() {
        countries = new HashMap<>();
        towns = new HashMap<>();
        businesses = new HashMap<>();
        plots = new HashMap<>();
        citizens = new HashMap<>();
    }

    public static CacheManager getInstance() {
        return instance;
    }

    public void registerCountry(Country country) {
        countries.put(country.getUUID(), country);
    }

    public void registerTown(Town town) {
        towns.put(town.getUUID(), town);
    }

    public void registerBusiness(Business business) {
        businesses.put(business.getUUID(), business);
    }

    public void registerPlot(Plot plot) {
        plots.put(plot.getChunkKey(), plot);
    }

    public void registerCitizen(Citizen citizen) {
        citizens.put(citizen.getUUID(), citizen);
    }

    public Country getCountry(UUID uuid) {
        return countries.get(uuid);
    }

    public Country getCountry(String name) {
        return countries.values().stream()
                .filter(country -> country.getName().equalsIgnoreCase(name.trim()))
                .findFirst()
                .orElse(null);
    }

    public Town getTown(UUID uuid) {
        return towns.get(uuid);
    }

    public Town getTown(String name) {
        return towns.values().stream()
                .filter(town -> town.getName().equalsIgnoreCase(name.trim()))
                .findFirst()
                .orElse(null);
    }

    public Business getBusiness(UUID uuid) {
        return businesses.get(uuid);
    }

    public Business getBusiness(String name) {
        return businesses.values().stream()
                .filter(business -> business.getName().equalsIgnoreCase(name.trim()))
                .findFirst()
                .orElse(null);
    }

    public Plot getPlot(Long chunkKey) {
        return plots.get(chunkKey);
    }

    public Plot getPlot(Chunk chunk) {
        return plots.get(chunk.getChunkKey());
    }

    public Citizen getCitizen(UUID uuid) {
        return citizens.get(uuid);
    }

    public Citizen getCitizen(Player player) {
        if (player == null) {
            return null;
        }
        return getCitizen(player.getUniqueId());
    }

    public Collection<Country> getCountries() {
        return countries.values();
    }

    public Collection<Town> getTowns() {
        return towns.values();
    }

    public Collection<Business> getBusinesses() {
        return businesses.values();
    }

    public Collection<Plot> getPlots() {
        return plots.values();
    }

    public Collection<Citizen> getCitizens() {
        return citizens.values();
    }

    public void deleteCountry(Country country) {
        country.getTowns().forEach(town -> town.setCountry(null));
        countries.remove(country.getUUID());
    }

    public void deleteTown(Town town) {
        town.getPlots().stream()
                .map(Plot::getChunkKey)
                .forEach(plots::remove);
        town.getMembers().forEach(citizen -> citizen.setTown(null));
        towns.remove(town.getUUID());
        if (town.getCountry() != null) {
            town.getCountry().removeTown(town);
        }
    }

    public void deleteBusiness(Business business) {
        business.getPlots().forEach(plot -> plot.setOwner(null));
        business.getLeader().setBusiness(null);
        businesses.remove(business.getUUID());
    }

    public void deletePlot(Plot plot) {
        plots.remove(plot.getChunkKey());
        if (plot.getTown() != null) {
            plot.getTown().removePlot(plot);
        }
    }
}
