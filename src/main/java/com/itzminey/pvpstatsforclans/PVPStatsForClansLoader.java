package com.itzminey.pvpstatsforclans;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import de.simonsator.partyandfriends.velocity.VelocityExtensionLoadingInfo;
import de.simonsator.partyandfriends.velocity.main.PAFPlugin;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = "pvpstatsforclans",
        name = "PVPStatsForClans",
        version = "1.1.0",
        url = "https://github.com/itzMiney/PVPStatsForClans",
        description = "Integrates PVP stats with Clans for Party and Friends. Ensure you configure your MySQL data in the config.",
        authors = {"itzMiney"},
        dependencies = {
                @Dependency(id = "partyandfriends"),
                @Dependency(id = "clans-loader")
        }
)
public class PVPStatsForClansLoader {

    private final Path folder;
    private final Logger logger;
    private final ProxyServer proxyServer;

    @Inject
    public PVPStatsForClansLoader(@DataDirectory Path folder, Logger logger, ProxyServer proxyServer) {
        this.folder = folder;
        this.logger = logger;
        this.proxyServer = proxyServer;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        PAFPlugin.loadExtension(new VelocityExtensionLoadingInfo(
                new PVPStatsForClans(folder, logger, proxyServer),
                "pvpstatsforclans",
                "PVPStatsForClans",
                "1.0.1",
                "itzMiney"
        ));
    }
}
