package net.worldmc.countries.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:plugins/Countries/countries.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void setupDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS spawns (
                uuid TEXT PRIMARY KEY,
                spawn_x DOUBLE NOT NULL,
                spawn_y DOUBLE NOT NULL,
                spawn_z DOUBLE NOT NULL
            );
        """);

            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS countries (
                uuid TEXT PRIMARY KEY REFERENCES spawns(uuid) ON DELETE CASCADE,
                name TEXT NOT NULL
            )
        """);

            // Towns table with spawn ID and optional country linkage
            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS towns (
                uuid TEXT PRIMARY KEY REFERENCES spawns(uuid) ON DELETE CASCADE,
                name TEXT NOT NULL,
                country_uuid TEXT REFERENCES countries(uuid) ON DELETE SET NULL
            );
        """);

            // Citizens table linked to towns
            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS citizens (
                uuid TEXT PRIMARY KEY,
                town_uuid TEXT,
                business_uuid TEXT,
                FOREIGN KEY (town_uuid) REFERENCES towns(uuid) ON DELETE SET NULL,
                FOREIGN KEY (business_uuid) REFERENCES businesses(uuid) ON DELETE SET NULL
            )
        """);

            // Blocks table with polymorphic ownership and town linkage
            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS plots (
                chunk_key BIGINT PRIMARY KEY,
                town_uuid TEXT NOT NULL,
                owner_uuid TEXT,
                owner_type TEXT CHECK (owner_type IN ('CITIZEN', 'BUSINESS')),
                FOREIGN KEY (town_uuid) REFERENCES towns(uuid) ON DELETE CASCADE
            )
        """);

            // Businesses table with spawn ID
            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS businesses (
                uuid TEXT PRIMARY KEY REFERENCES spawns(uuid) ON DELETE CASCADE,
                name TEXT NOT NULL
            )
        """);

            // Citizen ranks table for managing multiple group memberships
            stmt.executeUpdate("""
            CREATE TABLE IF NOT EXISTS ranks (
                citizen_uuid TEXT PRIMARY KEY,
                town_rank TEXT,
                business_rank TEXT,
                country_rank TEXT,
                FOREIGN KEY (citizen_uuid) REFERENCES citizens(uuid) ON DELETE CASCADE
            )
        """);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
