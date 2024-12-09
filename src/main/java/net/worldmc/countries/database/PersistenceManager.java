package net.worldmc.countries.database;

import net.worldmc.countries.Countries;
import net.worldmc.countries.managers.CacheManager;
import net.worldmc.countries.objects.*;
import net.worldmc.countries.objects.enumerations.Rank;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.*;
import java.util.UUID;

public class PersistenceManager {
    public static void initialize() {
        DatabaseManager.setupDatabase();

        loadData();

        Bukkit.getScheduler().runTaskTimerAsynchronously(
                Countries.getInstance(),
                () -> {
                    try {
                        Countries.getInstance().getLogger().info("Starting periodic data save...");
                        saveData();
                        Countries.getInstance().getLogger().info("Periodic data save completed.");
                    } catch (Exception e) {
                        Countries.getInstance().getLogger().severe("An error occurred during data save: " + e.getMessage());
                        e.printStackTrace();
                    }
                },
                0L,
                6000L
        );
    }

    public static void loadData() {
        try (Connection conn = DatabaseManager.getConnection()) {
            Countrys.load(conn);
            Towns.load(conn);
            Businesses.load(conn);
            Citizens.load(conn);
            Plots.load(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveData() {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);

            Countrys.save(conn);
            Towns.save(conn);
            Businesses.save(conn);
            Citizens.save(conn);
            Plots.save(conn);

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class Citizens {
        public static void load(Connection conn) throws SQLException {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM citizens");
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                UUID townUUID = rs.getString("town_uuid") != null ? UUID.fromString(rs.getString("town_uuid")) : null;
                UUID businessUUID = rs.getString("business_uuid") != null ? UUID.fromString(rs.getString("business_uuid")) : null;

                Citizen citizen = new Citizen(uuid);

                if (townUUID != null) {
                    Town town = CacheManager.getInstance().getTown(townUUID);
                    if (town != null) {
                        citizen.setTown(town);
                    }
                }

                if (businessUUID != null) {
                    Business business = CacheManager.getInstance().getBusiness(businessUUID);
                    if (business != null) {
                        citizen.setBusiness(business);
                    }
                }

                Ranks.load(conn, citizen);

                CacheManager.getInstance().registerCitizen(citizen);
            }
        }

        public static void save(Connection conn) throws SQLException {
            for (Citizen citizen : CacheManager.getInstance().getCitizens()) {
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT OR REPLACE INTO citizens (uuid, town_uuid, business_uuid) VALUES (?, ?, ?)"
                );
                stmt.setString(1, citizen.getUUID().toString());
                stmt.setString(2, citizen.getTown() != null ? citizen.getTown().getUUID().toString() : null);
                stmt.setString(3, citizen.getBusiness() != null ? citizen.getBusiness().getUUID().toString() : null);
                stmt.executeUpdate();

                Ranks.save(conn, citizen);
            }
        }
    }

    public static class Ranks {
        public static void load(Connection conn, Citizen citizen) throws SQLException {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ranks WHERE citizen_uuid = ?");
            stmt.setString(1, citizen.getUUID().toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String townRank = rs.getString("town_rank");
                String businessRank = rs.getString("business_rank");
                String countryRank = rs.getString("country_rank");

                Town town = citizen.getTown();
                Business business = citizen.getBusiness();
                Country country = town != null ? town.getCountry() : null;

                if (townRank != null && town != null) {
                    town.setRank(citizen, Rank.valueOf(townRank));
                }
                if (businessRank != null && business != null) {
                    business.setRank(citizen, Rank.valueOf(businessRank));
                }
                if (countryRank != null && country != null) {
                    country.setRank(citizen, Rank.valueOf(countryRank));
                }
            }
        }

        public static void save(Connection conn, Citizen citizen) throws SQLException {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT OR REPLACE INTO ranks (citizen_uuid, town_rank, business_rank, country_rank) VALUES (?, ?, ?, ?)"
            );

            Town town = citizen.getTown();
            Business business = citizen.getBusiness();
            Country country = town != null ? town.getCountry() : null;

            stmt.setString(1, citizen.getUUID().toString());
            stmt.setString(2, town != null ? town.getRank(citizen).name() : null);
            stmt.setString(3, business != null ? business.getRank(citizen).name() : null);
            stmt.setString(4, country != null ? country.getRank(citizen).name() : null);
            stmt.executeUpdate();
        }
    }

    public static class Plots {
        public static void load(Connection conn) throws SQLException {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM plots");
            while (rs.next()) {
                long chunkKey = rs.getLong("chunk_key");
                UUID townUUID = UUID.fromString(rs.getString("town_uuid"));
                UUID ownerUUID = rs.getString("owner_uuid") != null ? UUID.fromString(rs.getString("owner_uuid")) : null;
                String ownerType = rs.getString("owner_type");

                Town town = CacheManager.getInstance().getTown(townUUID);
                if (town != null) {
                    Plot plot = new Plot(chunkKey);
                    plot.setTown(town);

                    if (ownerUUID != null && ownerType != null) {
                        if (ownerType.equals("CITIZEN")) {
                            Citizen owner = CacheManager.getInstance().getCitizen(ownerUUID);
                            plot.setOwner(owner);
                        } else if (ownerType.equals("BUSINESS")) {
                            Business owner = CacheManager.getInstance().getBusiness(ownerUUID);
                            plot.setOwner(owner);
                        }
                    }

                    CacheManager.getInstance().registerPlot(plot);
                }
            }
        }

        public static void save(Connection conn) throws SQLException {
            for (Plot plot : CacheManager.getInstance().getPlots()) {
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT OR REPLACE INTO plots (chunk_key, town_uuid, owner_uuid, owner_type) VALUES (?, ?, ?, ?)"
                );
                stmt.setLong(1, plot.getChunkKey());
                stmt.setString(2, plot.getTown().getUUID().toString());

                if (plot.getOwner() instanceof Citizen) {
                    stmt.setString(3, ((Citizen) plot.getOwner()).getUUID().toString());
                    stmt.setString(4, "CITIZEN");
                } else if (plot.getOwner() instanceof Business) {
                    stmt.setString(3, ((Business) plot.getOwner()).getUUID().toString());
                    stmt.setString(4, "BUSINESS");
                } else {
                    stmt.setNull(3, java.sql.Types.VARCHAR);
                    stmt.setNull(4, java.sql.Types.VARCHAR);
                }

                stmt.executeUpdate();
            }
        }
    }

    public static class Towns {
        public static void load(Connection conn) throws SQLException {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM towns");
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String name = rs.getString("name");
                UUID countryUUID = rs.getString("country_uuid") != null ? UUID.fromString(rs.getString("country_uuid")) : null;

                Town town = new Town(uuid, name);
                Spawns.loadSpawnLocation(conn, town);

                if (countryUUID != null) {
                    Country country = CacheManager.getInstance().getCountry(countryUUID);
                    if (country != null) {
                        town.setCountry(country);
                    }
                }

                CacheManager.getInstance().registerTown(town);
            }
        }

        public static void save(Connection conn) throws SQLException {
            for (Town town : CacheManager.getInstance().getTowns()) {
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT OR REPLACE INTO towns (uuid, name) VALUES (?, ?)"
                );
                stmt.setString(1, town.getUUID().toString());
                stmt.setString(2, town.getName());
                if (town.getSpawn() != null) {
                    Spawns.saveSpawnLocation(conn, town.getUUID(), town.getSpawn());
                }
                stmt.executeUpdate();
            }
        }
    }

    public static class Countrys {
        public static void load(Connection conn) throws SQLException {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM countries");
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String name = rs.getString("name");

                Country country = new Country(uuid, name);

                Spawns.loadSpawnLocation(conn, country);

                CacheManager.getInstance().registerCountry(country);
            }
        }

        public static void save(Connection conn) throws SQLException {
            for (Country country : CacheManager.getInstance().getCountries()) {
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT OR REPLACE INTO countries (uuid, name) VALUES (?, ?)"
                );
                stmt.setString(1, country.getUUID().toString());
                stmt.setString(2, country.getName());
                if (country.getSpawn() != null) {
                    Spawns.saveSpawnLocation(conn, country.getUUID(), country.getSpawn());
                }
                stmt.executeUpdate();
            }
        }
    }

    public static class Businesses {
        public static void load(Connection conn) throws SQLException {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM businesses");
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                String name = rs.getString("name");

                Business business = new Business(uuid, name);

                Spawns.loadSpawnLocation(conn, business);

                CacheManager.getInstance().registerBusiness(business);
            }
        }

        public static void save(Connection conn) throws SQLException {
            for (Business business : CacheManager.getInstance().getBusinesses()) {
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT OR REPLACE INTO businesses (uuid, name) VALUES (?, ?)"
                );
                stmt.setString(1, business.getUUID().toString());
                stmt.setString(2, business.getName());
                if (business.getSpawn() != null) {
                    Spawns.saveSpawnLocation(conn, business.getUUID(), business.getSpawn());
                }
                stmt.executeUpdate();
            }
        }
    }

    public static class Spawns {
        public static void loadSpawnLocation(Connection conn, Group group) throws SQLException {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT spawn_x, spawn_y, spawn_z FROM spawns WHERE uuid = ?"
            );
            stmt.setString(1, group.getUUID().toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double spawnX = rs.getDouble("spawn_x");
                double spawnY = rs.getDouble("spawn_y");
                double spawnZ = rs.getDouble("spawn_z");

                if (spawnX != 0 || spawnY != 0 || spawnZ != 0) {
                    Location spawn = new Location(Bukkit.getWorld("world"), spawnX, spawnY, spawnZ);
                    group.setSpawn(spawn);
                }
            }
        }

        public static void saveSpawnLocation(Connection conn, UUID uuid, Location spawn) throws SQLException {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT OR REPLACE INTO spawns (uuid, spawn_x, spawn_y, spawn_z) VALUES (?, ?, ?, ?)"
            );
            stmt.setString(1, uuid.toString());
            if (spawn != null) {
                stmt.setDouble(2, spawn.getX());
                stmt.setDouble(3, spawn.getY());
                stmt.setDouble(4, spawn.getZ());
            } else {
                stmt.setNull(2, java.sql.Types.DOUBLE);
                stmt.setNull(3, java.sql.Types.DOUBLE);
                stmt.setNull(4, java.sql.Types.DOUBLE);
            }
            stmt.executeUpdate();
        }
    }
}
