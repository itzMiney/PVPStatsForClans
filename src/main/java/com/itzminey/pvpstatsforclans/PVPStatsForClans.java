package com.itzminey.pvpstatsforclans;

import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.ProxyServer;
import de.simonsator.partyandfriends.velocity.api.PAFExtension;
import de.simonsator.partyandfriends.velocity.api.pafplayers.OnlinePAFPlayer;
import de.simonsator.partyandfriends.velocity.api.pafplayers.PAFPlayer;
import de.simonsator.partyandfriends.velocity.clan.api.Clan;
import de.simonsator.partyandfriends.velocity.clan.api.ClanStat;
import de.simonsator.partyandfriends.velocity.clan.commands.ClanCommands;
import de.simonsator.partyandfriends.velocity.clan.commands.subcommands.Stats;
import com.moandjiezana.toml.Toml;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class PVPStatsForClans extends PAFExtension implements ClanStat {

    private final Logger logger;
    private final ProxyServer proxyServer;
    private final Path dataDirectory;
    private DatabaseManager databaseManager;
    private String pluginName;

    public PVPStatsForClans(Path dataDirectory, Logger logger, ProxyServer proxyServer) {
        super(dataDirectory);
        this.dataDirectory = dataDirectory;
        this.logger = logger;
        this.proxyServer = proxyServer;
    }

    @Subscribe
    public void onEnable() {
        loadConfiguration();

        Map<String, Map<String, Object>> stats = databaseManager.getPlayerStats();
        logger.info("Retrieving Player Stats from Database");

        registerCommands();

        ClanCommands clanCommandInstance = ClanCommands.getInstance();
        Stats statCommand = (Stats) clanCommandInstance.getSubCommand(Stats.class);
        if (statCommand != null) {
            statCommand.registerClanStats(this, this);
        } else {
            logger.error("Failed to retrieve Stats subcommand. Ensure the subcommand is properly registered in PartyAndFriends.");
        }
    }

    @Override
    public String getName() {
        return pluginName;
    }

    public void stats(OnlinePAFPlayer pSender, Clan pClan) {
        List<PAFPlayer> PlayersOfTheClan = pClan.getAllPlayers();

        int totalKills = 0;
        int totalDeaths = 0;

        for (PAFPlayer player : PlayersOfTheClan) {
            String playerUUID = player.getUniqueId().toString();
            Map<String, Object> stats = databaseManager.getPlayerStats().get(playerUUID);

            if (stats != null) {
                totalKills += (int) stats.get("kills");
                totalDeaths += (int) stats.get("deaths");
            }
        }

        pSender.sendMessage(Component.text("§7Total Kills: " + totalKills));
        pSender.sendMessage(Component.text("§7Total Deaths: " + totalDeaths));
    }

    private void loadConfiguration() {
        File configFile = dataDirectory.resolve("config.toml").toFile();

        if (!configFile.exists()) {
            logger.error("Configuration file not found: " + configFile.getAbsolutePath());
            createDefaultConfig(configFile);
            return;
        }

        Toml toml = new Toml().read(configFile);

        String host = toml.getString("database.host");
        int port = toml.getLong("database.port").intValue();
        String user = toml.getString("database.user");
        String password = toml.getString("database.password");
        String database = toml.getString("database.database");

        pluginName = toml.getString("plugin.name", "PvP-Stats");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database;

        try {
            databaseManager = new DatabaseManager(url, user, password, logger);
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
        try {
            configFile.getParentFile().mkdirs();
            PrintWriter writer = new PrintWriter(configFile, "UTF-8");
            writer.println("[database]");
            writer.println("host = \"localhost\"");
            writer.println("port = 3306");
            writer.println("user = \"root\"");
            writer.println("password = \"password\"");
            writer.println("database = \"pvpstats\"");
            writer.println("[plugin]");
            writer.println("name = \"PvP-Stats\"");
            writer.close();
            logger.info("Default configuration file created at: " + configFile.getAbsolutePath());
        } catch (Exception e) {
            logger.error("Failed to create default configuration file", e);
        }
    }

    private void registerCommands() {
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
        public void execute(SimpleCommand.Invocation invocation) {
            CommandSource source = invocation.source();

            plugin.logger.info("Reload command executed by " + source.toString());

            plugin.loadConfiguration();

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