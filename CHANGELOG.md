# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

### Added
- `/fi rename "new name"` command allowing fief owners to rename their fief (`fiefs.rename`)
- `/fi whois <player>` command allowing players to check which fief a given player is a member of (`fiefs.whois`)

### Fixed
- Fiefs saved to `fiefs.json` failed to load on startup, throwing a `NullPointerException` during
  plugin enable — a fief's flags were read before they were initialized. Servers with existing fief
  data could not start the plugin, and the failed load left in-memory data empty so the next save
  wrote an empty `fiefs.json` over it
- `/fi kick` refused to kick any member with "That player is not in your fief." — the target's fief
  was looked up by fief name using the player's name instead of by their UUID
- `/fi invite` no longer invites a player who already belongs to another fief; the "already in a
  fief" check was looking the target up by fief name and never matched
- `/fi desc`'s no-argument usage message now shows double quotes, matching the double-quote parsing
  the command actually requires

## [0.11.0]

### Added
- Fief creation, disbanding, and management integrated with Medieval Factions
- Territory claiming for fiefs within faction land
- Member invite, join, leave, kick, and transfer commands
- Fief flags and config management
