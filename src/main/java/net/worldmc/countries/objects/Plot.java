package net.worldmc.countries.objects;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

public class Plot {
    private final Long chunkKey;
    private Town town;
    private Object owner;
    private boolean forSale = false;
    private int price = 0;
    private int priorSalePrice = 0;

    public Plot(Long chunkKey) { this.chunkKey = chunkKey; }

    public Long getChunkKey() { return chunkKey; }

    public Town getTown() { return town; }

    public void setTown(Town town) {
        this.town = town;
        town.addPlot(this);
    }

    public void setOwner(Object object) {
        if (object instanceof Citizen || object instanceof Business || object == null) {
            this.owner = object;
        } else {
            throw new IllegalArgumentException("Owner must be a Member or a Business.");
        }
    }

    public Object getOwner() { return owner; }

    public boolean isOwnedByMember() { return owner instanceof Citizen; }

    public boolean isOwnedByBusiness() { return owner instanceof Business; }

    public Chunk getChunk() {
        World world = Bukkit.getWorld("world");
        if (world != null) {
            return world.getChunkAt(chunkKey);
        }
        return null;
    }

    public boolean isForSale() { return forSale; }

    public void setForSale(boolean forSale) { this.forSale = forSale; }

    public int getPrice() { return price; }

    public void setPrice(int price) { this.price = price; }

    public int getPriorSalePrice() { return priorSalePrice; }

    public void setPriorSalePrice(int priorSalePrice) { this.priorSalePrice = priorSalePrice; }
}
