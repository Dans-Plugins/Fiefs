# User Guide

## Prerequisites

- A Minecraft server running Spigot or Paper (1.13+)
- [Medieval Factions](https://github.com/Dans-Plugins/Medieval-Factions) installed and running

## First Steps

1. Install the Fiefs plugin by placing the JAR in your server's `plugins/` folder.
2. Restart your server.
3. Ensure Medieval Factions is installed and working — Fiefs depends on it.
4. Join a faction in Medieval Factions before using any Fiefs commands.

## Common Scenarios

### Creating a Fief

1. Join a faction using Medieval Factions.
2. Run `/fi create <name>` to create a new fief within your faction.

### Claiming Land

1. Stand in the chunk you want to claim.
2. Run `/fi claim` to claim the chunk for your fief.
3. Use `/fi checkclaim` to verify chunk ownership.

### Inviting and Managing Members

1. Run `/fi invite <player>` to invite a player to your fief.
2. The invited player joins with `/fi join <fief-name>`.
3. View members with `/fi members`.
4. Remove a member with `/fi kick <player>`.

### Viewing Fief Information

- `/fi info` — View information about your fief or another fief.
- `/fi list` — List all fiefs in your faction.

### Configuring Fief Flags

- `/fi flags` — View and modify your fief's flags (e.g. land protection).

### Leaving or Disbanding

- `/fi leave` — Leave your current fief.
- `/fi disband` — Disband a fief you own.

## Permissions

| Permission | Default | Description |
|---|---|---|
| `fiefs.help` | `true` | View the help command |
| `fiefs.list` | `true` | List fiefs in your faction |
| `fiefs.create` | `true` | Create a fief |
| `fiefs.disband` | `true` | Disband your fief |
| `fiefs.claim` | `true` | Claim a chunk for your fief |
| `fiefs.unclaim` | `true` | Unclaim a chunk |
| `fiefs.checkclaim` | `true` | Check which fief owns a chunk |
| `fiefs.info` | `true` | View fief information |
| `fiefs.invite` | `true` | Invite a player to your fief |
| `fiefs.join` | `true` | Join a fief |
| `fiefs.leave` | `true` | Leave your fief |
| `fiefs.members` | `true` | View fief members |
| `fiefs.desc` | `true` | Change your fief's description |
| `fiefs.kick` | `true` | Kick a member from your fief |
| `fiefs.transfer` | `true` | Transfer fief ownership |
| `fiefs.config` | `op` | View and modify plugin configuration |
