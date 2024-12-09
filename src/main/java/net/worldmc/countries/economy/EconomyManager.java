package net.worldmc.countries.economy;

import net.milkbowl.vault2.economy.Economy;
import net.worldmc.countries.Countries;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.math.BigDecimal;
import java.util.UUID;

public class EconomyManager {
    private static Economy economy;

    static {
        if (!setupEconomy()) {
            throw new IllegalStateException("Vault dependency not found or not loaded!");
        }
    }

    public static Economy getEconomy() {
        return economy;
    }

    private static boolean setupEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }
}
