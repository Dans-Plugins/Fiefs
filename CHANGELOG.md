# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Unreleased]

### Added
- `/fi rename "new name"` command allowing fief owners to rename their fief (`fiefs.rename`)
- `/fi whois <player>` command allowing players to check which fief a given player is a member of (`fiefs.whois`)

### Fixed
- `fiefs.default`, the permission node `DefaultCommand` (bare `/fi`) declares, was missing from
  `plugin.yml` and undocumented; it is now registered with `default: true` and listed alongside
  every other command's permission node
- Fiefs saved to `fiefs.json` failed to load on startup, throwing a `NullPointerException` during
  plugin enable — a fief's flags were read before they were initialized. Servers with existing fief
  data could not start the plugin, and because the failed load left in-memory data empty, a
  subsequent save could write an empty `fiefs.json` over it
- `/fi kick` refused to kick any member with "That player is not in your fief." — the target's fief
  was looked up by fief name using the player's name instead of by their UUID
- `/fi invite` no longer invites a player who already belongs to another fief; the "already in a
  fief" check was looking the target up by fief name and never matched
- `/fi desc`'s no-argument usage message now shows double quotes, matching the double-quote parsing
  the command actually requires
- A load failure partway through `fiefs.json` or `claimedChunks.json` (a malformed entry or invalid
  JSON) no longer leaves in-memory fief/claim data empty; the file is now parsed fully before
  replacing the existing in-memory data, and saving is skipped until the file is fixed and the
  server restarted, so a bad load can no longer overwrite good data on disk
- A zero-byte `fiefs.json` or `claimedChunks.json` — which a crash or kill during the shutdown save
  can leave behind — was treated as a corrupt file, which disabled saving for the whole session and
  silently discarded every fief created or changed during it. An empty save file now loads as "no
  data", the same as a missing one, and leaves saving enabled
- The `./plugins/Fiefs/` save directory is now created with `mkdirs()` rather than `mkdir()`, so the
  save no longer fails silently in environments where `./plugins/` does not already exist
- An existing `fiefs.json` or `claimedChunks.json` that cannot be opened for reading (for example
  after a permission or ownership change on the server's data directory) was previously treated the
  same as a missing file, loading as "no fiefs/claims" and leaving saving enabled — so the next save
  could overwrite real data with an empty file. It is now treated as a failed load, matching the
  existing handling for a malformed file, and saving is skipped until the file is fixed

## [0.11.0]

### Added
- Fief creation, disbanding, and management integrated with Medieval Factions
- Territory claiming for fiefs within faction land
- Member invite, join, leave, kick, and transfer commands
- Fief flags and config management
