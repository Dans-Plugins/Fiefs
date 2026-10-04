# Fiefs

## Description

Fiefs is a Minecraft plugin that allows faction members to create fiefs (sub-factions) within [Medieval Factions](https://github.com/Dans-Plugins/Medieval-Factions). Fiefs function as sub-factions, allowing for more granular organization within a faction.

## Supported Minecraft Versions
This plugin is supported on the Minecraft versions listed in [`minecraft-versions.json`](minecraft-versions.json): currently **1.19.4**, **1.21.11**, **26.2** and **26.3** (Spigot and its forks). Every stable release is booted on a real server of each of these versions before it is published, and every build checks that the plugin only uses Bukkit API that exists on all of them. Other versions from 1.19.4 onwards are expected to work but are not tested. To support another version, add it to the file: both checks pick it up.

## Installation

### First Time Installation

1. Download the plugin from [SpigotMC](https://www.spigotmc.org/resources/fiefs-early-access.98559/) or the [releases page](https://github.com/Dans-Plugins/Fiefs/releases).
2. Place the jar in the `plugins` folder of your server.
3. Restart your server.

### Dependencies

This plugin depends on [Medieval Factions](https://github.com/Dans-Plugins/Medieval-Factions) in order to work.

**Compatibility:** Fiefs v0.12.1 was enabled against [Medieval Factions](https://github.com/Dans-Plugins/Medieval-Factions) 7.0.0 by the [dependents gate](https://github.com/Dans-Plugins/release-gates/actions/runs/36957984824) before Medieval Factions 7.0.0 was published.

**Other Medieval Factions expansions:** [Currencies](https://github.com/Dans-Plugins/Currencies) (faction currencies), [Democracy](https://github.com/Dans-Plugins/Democracy) (elections), [Bluemap_MedievalFactions](https://github.com/Dans-Plugins/Bluemap_MedievalFactions) (claims on a BlueMap web map). All of them are listed in the [Medieval Factions README](https://github.com/Dans-Plugins/Medieval-Factions#expansions).

## Usage

### Documentation

- [User Guide](USER_GUIDE.md) – Getting started and common scenarios
- [Commands Reference](COMMANDS.md) – Complete list of all commands
- [Configuration Guide](CONFIG.md) – Detailed configuration options

### Wiki & Additional Resources

- [Wiki Guide](https://github.com/Dans-Plugins/Fiefs/wiki/Guide)
- [FAQ](https://github.com/Dans-Plugins/Fiefs/wiki/FAQ)

## Support

You can find the support Discord server [here](https://discord.gg/xXtuAQ2).

### Experiencing a bug?

Please fill out a bug report [here](https://github.com/Dans-Plugins/Fiefs/issues/new?template=bug_report.md).

- [Known Bugs](https://github.com/Dans-Plugins/Fiefs/issues?q=is%3Aopen+is%3Aissue+label%3Abug)

## Contributing

- [CONTRIBUTING.md](CONTRIBUTING.md)
- [Notes for Developers](https://github.com/Dans-Plugins/Fiefs/wiki/Developer-Notes)

## Testing

### Build Verification

Linux / macOS:

    mvn clean package

Windows:

    mvn clean package

If you see `BUILD SUCCESS`, the project has built successfully. `mvn clean package` also runs the JUnit test suite under `src/test/`.

## Development

### Building the Plugin

1. Clone the repository: `git clone https://github.com/Dans-Plugins/Fiefs.git`
2. Build the plugin: `mvn clean package`
3. The compiled JAR will be in the `target/` directory.

### Manual Testing

1. Build the plugin with `mvn clean package`.
2. Copy the JAR from `target/` into your test server's `plugins/` folder.
3. Start or restart the server.

## Authors and Acknowledgement

### Developers

| Name | Main Contributions |
|------|--------------------|
| Daniel Stephenson | Creator |

This plugin was requested by Laughingspade.

## License

This project is licensed under the [GNU General Public License v3.0](LICENSE) (GPL-3.0).

You are free to use, modify, and distribute this software, provided that:

- Source code is made available under the same license when distributed.
- Changes are documented and attributed.
- No additional restrictions are applied.

See the [LICENSE](LICENSE) file for the full text of the GPL-3.0 license.

## Project Status

This project is in active development.

### bStats

You can view the bStats page for the plugin [here](https://bstats.org/plugin/bukkit/Fiefs/12743).

## Usage reporting

Usage reporting is on by default: each time the plugin is enabled, and each time one of its commands is run, it sends its name, its version and the command's name to the author's trace server at https://trace.danielstephenson.dev, so it is known which plugins are actually in use. Nothing about players, worlds or IP addresses is sent, and nothing typed after a command is.

Each event also carries a random server ID (the `server-id` line in `plugins/trace/config.yml`) so
servers can be counted rather than events. It identifies no person, account or IP address; delete
the line to get a new one.

To turn it off:

- for this plugin: set `usage-reporting.enabled: false` in `plugins/Fiefs/config.yml`, or run `/fi config set usage-reporting.enabled false`
- for every plugin on the server that reports to trace: set `enabled: false` in `plugins/trace/config.yml` (written on first start)
- for the whole server process: set the environment variable `TRACE_USAGE_REPORTING=off` (or `DO_NOT_TRACK=1`)

The plugin says on every start whether reporting is on. Details: https://github.com/Stephenson-Software/trace#usage-reporting
