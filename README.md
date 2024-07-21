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
   - Velocity proxy is required and can be found [here](https://papermc.io/software/velocity).
   
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
```

- `host`: The database server address.
- `port`: The port for the database server.
- `user`: The database username.
- `password`: The database password.
- `database`: The name of the database to connect to.
- `name`: The name of the plugin, which can be customized.

2. **Customize Plugin Name**:
   - To customize the plugin name, modify the `name` field in the `config.toml` file. The default is `"PvP-Stats"`, but you can set it to any string you prefer.

## Usage

- **Reloading the Plugin**: Use the `/psc reload` command to reload the plugin configuration.
- **Viewing Player Stats**: The plugin will automatically retrieve and display player stats as defined in the database.

## Troubleshooting

- **Plugin Not Loading**: Ensure all dependencies are correctly installed and loaded before PVPStatsForClans.
- **Database Errors**: Check the database connection details in the `config.toml` file and ensure the database is accessible.

## Contributing

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/your-feature`).
3. Commit your changes (`git commit -am 'Add new feature'`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Create a new Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contact

For support or inquiries, please contact [itzMiney](mailto:itzminey@proton.me).

---

Enjoy using **PVPStatsForClans**! If you have any questions or need help, feel free to open an issue or contact us directly.

