# Fiefs User Guide

## What is Fiefs?

Fiefs is a Spigot plugin that adds a sub-faction territory system to Medieval Factions servers. Faction members can create fiefs — named sub-groups within their faction — and claim chunks of faction land for those fiefs.

## Requirements

- [Medieval Factions](https://github.com/Dans-Plugins/Medieval-Factions) must be installed.

## Installation

1. Download the latest `Fiefs-<version>.jar` from the [Releases](https://github.com/Dans-Plugins/Fiefs/releases) page.
2. Place the JAR (and the Medieval Factions JAR) in your server's `plugins/` folder.
3. Restart the server.

## Getting Started

1. Create a fief within your faction: `/fi create "Fief Name"`
2. Invite members: `/fi invite <player>`
3. Claim faction land for your fief: stand in a faction-owned chunk and run `/fi claim`
4. Check fief ownership of a chunk: `/fi checkclaim`
5. View all fiefs in your faction: `/fi list`

## Permissions

| Permission | Default | Description |
|------------|---------|-------------|
| `fiefs.help` | `true` | View the help menu. |
| `fiefs.list` | `true` | List fiefs. |
| `fiefs.create` | `true` | Create a fief. |
| `fiefs.disband` | `true` | Disband a fief. |
| `fiefs.info` | `true` | View fief information. |
| `fiefs.members` | `true` | View fief members. |
| `fiefs.join` | `true` | Join a fief. |
| `fiefs.leave` | `true` | Leave a fief. |
| `fiefs.invite` | `true` | Invite a player to a fief. |
| `fiefs.kick` | `true` | Kick a player from a fief. |
| `fiefs.transfer` | `true` | Transfer fief ownership. |
| `fiefs.desc` | `true` | Set a fief description. |
| `fiefs.rename` | `true` | Rename a fief. |
| `fiefs.claim` | `true` | Claim a chunk for a fief. |
| `fiefs.unclaim` | `true` | Unclaim a chunk from a fief. |
| `fiefs.checkclaim` | `true` | Check which fief owns a chunk. |
| `fiefs.flags` | `true` | View and alter fief flags. |
| `fiefs.config` | `op` | View and alter plugin config options. |

## Support

Ask questions in the [Discord server](https://discord.gg/xXtuAQ2) or open a [GitHub issue](https://github.com/Dans-Plugins/Fiefs/issues).
