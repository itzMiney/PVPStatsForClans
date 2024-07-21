# PVPStatsForClans

**PVPStatsForClans** is a plugin designed for tracking player stats in PvP-based Minecraft servers. It integrates with the Party and Friends plugin and provides detailed statistics for clans. This plugin supports Velocity proxy servers and is intended to be used with for a PvP Server but can be customized for other PvP configurations like KitPvP.

## Features

- Track and manage player statistics for clans.
- Integrates with the Party and Friends plugin for enhanced functionality.
- Configurable to use with KitPvP or other PvP setups.
- Supports Velocity proxies.

## Installation

1. **Download the Plugin**:
   - Get the latest release from the [releases page](https://github.com/itzMiney/PVPStatsForClans/releases).

2. **Place the Plugin**:
   - Put the `PVPStatsForClans.jar` file into the `plugins` directory of your Velocity server.

3. **Install Dependencies**:
   - Ensure you have the following dependencies installed on your server:
     - [Clans for Party and Friends Extended](https://www.spigotmc.org/resources/clans-for-party-and-friends-extended.13890/)
     - [Party and Friends Extended Edition](https://www.spigotmc.org/resources/party-and-friends-extended-edition-for-bungeecord-velocity-supports-1-7-1-21-x.10123/)
     - [PVP Stats](https://www.spigotmc.org/resources/pvp-stats.59124/)
     - [Velocity](https://papermc.io/software/velocity).

## Configuration

1. **Create/Edit Configuration File**:
   - Locate the `config.toml` file in the plugin's data directory. If it does not exist, it will be created automatically with default values.
   - Modify the `config.toml` file to configure your database and plugin settings.

```toml
[database]
host = "localhost"
port = 3306
user = "root"
password = "password"
database = "pvpstats"

[plugin]
name = "PvP-Stats"  # Default name, can be customized
