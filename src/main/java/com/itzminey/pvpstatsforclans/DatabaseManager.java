package com.itzminey.pvpstatsforclans;

import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {
    private final String url;
    private final String user;
    private final String password;
    private final Logger logger;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    public DatabaseManager(String url, String user, String password, Logger logger) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.logger = logger;

        try (Connection connection = getConnection()) {
            if (connection != null && !connection.isClosed()) {
                logger.info("Database connection successful.");
            } else {
                logger.error("Database connection failed. Check your credentials and URL.");
            }
        } catch (SQLException e) {
            logger.error("Failed to connect to the database. Check your credentials and URL.", e);
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public Map<String, Map<String, Object>> getPlayerStats() {
        Map<String, Map<String, Object>> playerStats = new HashMap<>();
        String query = "SELECT uid, kills, deaths FROM pvpstats";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String uid = resultSet.getString("uid");
                int kills = resultSet.getInt("kills");
                int deaths = resultSet.getInt("deaths");

                Map<String, Object> stats = new HashMap<>();
                stats.put("kills", kills);
                stats.put("deaths", deaths);

                playerStats.put(uid, stats);
            }
        } catch (SQLException e) {
            logger.error("Failed to fetch player stats. Check your database connection.", e);
        }

        return playerStats;
    }
}