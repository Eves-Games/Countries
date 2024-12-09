package net.worldmc.countries;

import net.worldmc.countries.commands.CommandRegistry;
import net.worldmc.countries.integrations.squaremap.SquareMapIntegration;
import net.worldmc.countries.listeners.*;
import net.worldmc.countries.database.PersistenceManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.jpenilla.squaremap.api.MapWorld;

import java.io.File;

public class Countries extends JavaPlugin {

    private static Countries instance;

    @Override
    public void onEnable() {
        instance = this;

        File pluginDir = new File(getDataFolder().getPath());
        if (!pluginDir.exists()) {
            if (pluginDir.mkdirs()) {
                getLogger().info("Created plugins/Countries directory.");
            } else {
                getLogger().warning("Failed to create plugins/Countries directory. Please check server permissions.");
            }
        }

        PersistenceManager.initialize();

        CommandRegistry commandRegistry = new CommandRegistry(this);
        commandRegistry.registerCommands();

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerRespawnListener(), this);
        getServer().getPluginManager().registerEvents(new ChunkEnterListener(), this);
        getServer().getPluginManager().registerEvents(new TownProtectionListener(), this);
        getServer().getPluginManager().registerEvents(new TownCreateListener(), this);

        SquareMapIntegration.initialize();

        World world = Bukkit.getWorld("world");
        if (world != null) {
            SquareMapIntegration.registerLayer(world, "towns", "Towns", 1, true, 100);
            SquareMapIntegration.registerLayer(world, "countries", "Countries", 2, false, 200);
            SquareMapIntegration.registerLayer(world, "businesses", "Businesses", 3, false, 300);
        }
    }

    @Override
    public void onDisable() {
        PersistenceManager.saveData();
    }

    public static Countries getInstance() {
        return instance;
    }
}
