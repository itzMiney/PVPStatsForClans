package com.itzminey.pvpstatsforclans;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import de.simonsator.partyandfriends.velocity.api.pafplayers.OnlinePAFPlayer;
import de.simonsator.partyandfriends.velocity.api.pafplayers.PAFPlayer;
import de.simonsator.partyandfriends.velocity.clan.api.Clan;
import de.simonsator.partyandfriends.velocity.clan.api.ClanStat;
import de.simonsator.partyandfriends.velocity.clan.commands.ClanCommands;
import de.simonsator.partyandfriends.velocity.clan.commands.subcommands.Stats;
import de.simonsator.partyandfriends.velocity.api.PAFExtension;

import com.moandjiezana.toml.Toml;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.kyori.adventure.text.Component;

@Plugin(
        id = "pvpstatsforclans",
        name = "PVPStatsForClans",
        version = "1.0.0",
        authors = {"itzMiney"}
)
public class PVPStatsForClans extends PAFExtension implements ClanStat {

    private final Logger logger;
    private final ProxyServer proxyServer;
    private final Path dataDirectory;
    private DatabaseManager databaseManager;

    @Inject
    public PVPStatsForClans(@DataDirectory Path folder, Logger logger, ProxyServer proxyServer) {
        super(folder);
        this.logger = logger;
        this.proxyServer = proxyServer;
        this.dataDirectory = folder;
    }

    @Subscribe(order = PostOrder.LAST)
    public void onProxyInitialization(ProxyInitializeEvent event) {
        // ANSI escape codes for colors
        String RED = "\033[0;31m";
        String GREEN = "\033[0;32m";
        String BLUE = "\033[0;34m";
        String RESET = "\033[0m"; // Reset to default color

        // Log initialization
        logger.info("\033[0;31m  ____  ___   ___\033[0m");
        logger.info("\033[0;31m /___/ /___  /     \033[0;32mPVPStatsForClans\033[0m");
        logger.info("\033[0;31m/      ___/  \\___  \033[0;34mv1.0.0 Enabled\033[0m");
        logger.info("\033[0m");

        // Load configuration
        loadConfiguration();

        // Fetch player stats and log them
        Map<String, Map<String, Object>> stats = databaseManager.getPlayerStats();
        logger.info("Retrieving Player Stats from Database" + stats);

        for (Map.Entry<String, Map<String, Object>> entry : stats.entrySet()) {
            String uuid = entry.getKey();
            Map<String, Object> statData = entry.getValue();
            int kills = (int) statData.get("kills");
            int deaths = (int) statData.get("deaths");
        }

        // Register reload command
        registerCommands();
        onEnable();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        // Unregister commands
        ClanCommands clanCommandInstance = ClanCommands.getInstance();
        Stats statCommand = (Stats) clanCommandInstance.getSubCommand(Stats.class);
        statCommand.unregisterAll(this);
    }

    private void loadConfiguration() {
        // Load the TOML file
        File configFile = dataDirectory.resolve("config.toml").toFile();

        if (!configFile.exists()) {
            logger.error("Configuration file not found: " + configFile.getAbsolutePath());
            // You can create a default config here if needed
            createDefaultConfig(configFile);
            return;
        }

        Toml toml = new Toml().read(configFile);

        // Read values from the TOML file
        String host = toml.getString("database.host");
        int port = toml.getLong("database.port").intValue();
        String user = toml.getString("database.user");
        String password = toml.getString("database.password");
        String database = toml.getString("database.database");

        // Construct the JDBC URL
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database;

        // Initialize the DatabaseManager
        try {
            databaseManager = new DatabaseManager(url, user, password);
            logger.info("Database Configuration:");
            logger.info("Host=" + host + ":" + port);
            logger.info("User=" + user);
            logger.info("Port=" + port);
            logger.info("Database=" + database);

        } catch (Exception e) {
            logger.error("Failed to initialize DatabaseManager. Check your database credentials and URL.", e);
        }
    }

    private void createDefaultConfig(File configFile) {
        // Create default configuration logic
        // Example of writing a default TOML configuration to the file
        try {
            configFile.getParentFile().mkdirs();
            PrintWriter writer = new PrintWriter(configFile, "UTF-8");
            writer.println("[database]");
            writer.println("host = \"localhost\"");
            writer.println("port = 3306");
            writer.println("user = \"root\"");
            writer.println("password = \"password\"");
            writer.println("database = \"pvpstats\"");
            writer.close();
            logger.info("Default configuration file created at: " + configFile.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Failed to create default configuration file", e);
        }
    }

    @Override
    public void onEnable() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            // Register commands after delay
            ClanCommands clanCommandInstance = ClanCommands.getInstance();
            if (clanCommandInstance == null) {
                logger.error("Failed to retrieve ClanCommands instance. Ensure PartyAndFriends plugin is loaded and initialized.");
                return;
            }
            Stats statCommand = (Stats) clanCommandInstance.getSubCommand(Stats.class);
            if (statCommand == null) {
                logger.error("Failed to retrieve Stats subcommand. Ensure the subcommand is properly registered in PartyAndFriends.");
                return;
            }
            statCommand.registerClanStats(this, this);
        }, 20, TimeUnit.SECONDS); // 20 seconds delay
    }

    @Override
    public String getName() {
        return "KitPvP-Stats";
    }

    @Override
    public void stats(OnlinePAFPlayer pSender, Clan pClan) {
        List<PAFPlayer> PlayersOfTheClan = pClan.getAllPlayers();

        // Initialize counters for total kills and deaths
        int totalKills = 0;
        int totalDeaths = 0;

        // Aggregate kills and deaths for each online player
        for (PAFPlayer player : PlayersOfTheClan) {
            String playerUUID = player.getUniqueId().toString();
            Map<String, Object> stats = databaseManager.getPlayerStats().get(playerUUID);

            if (stats != null) {
                totalKills += (int) stats.get("kills");
                totalDeaths += (int) stats.get("deaths");
            }
        }

        // Send a message to the player with the combined stats
        pSender.sendMessage(Component.text("§7Total Kills: " + totalKills));
        pSender.sendMessage(Component.text("§7Total Deaths: " + totalDeaths));
    }

    public class DatabaseManager {
        private final String url;
        private final String user;
        private final String password;

        // Static block
        static {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("MySQL JDBC Driver not found", e);
            }
        }

        public DatabaseManager(String url, String user, String password) {
            this.url = url;
            this.user = user;
            this.password = password;

            // Attempt a connection to validate credentials and URL
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

    private void registerCommands() {
        // Register the command with the name "psc reload" and aliases
        CommandMeta meta = proxyServer.getCommandManager().metaBuilder("psc reload")
                .aliases("reload")
                .build();
        proxyServer.getCommandManager().register(meta, new ReloadCommand(this));
        logger.info("Registered psc reload command with alias reload.");
    }

    public static class ReloadCommand implements SimpleCommand {

        private final PVPStatsForClans plugin;

        public ReloadCommand(PVPStatsForClans plugin) {
            this.plugin = plugin;
        }

        @Override
        public void execute(Invocation invocation) {
            CommandSource source = invocation.source();

            // Log the command execution
            plugin.logger.info("Reload command executed by " + source.toString());

            // Reload configuration
            plugin.loadConfiguration();

            // Inform user
            source.sendMessage(Component.text("§7Configuration reloaded successfully. If the issue persists, check the database credentials and restart the server."));
        }

        @Override
        public List<String> suggest(Invocation invocation) {
            return List.of();
        }

        @Override
        public boolean hasPermission(Invocation invocation) {
            return invocation.source().hasPermission("pvpstatsforclans.reload");
        }
    }
}
