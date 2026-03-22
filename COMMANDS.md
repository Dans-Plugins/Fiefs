# Commands Reference

All Fiefs commands use the base command `/fiefs` (or the alias `/fi`).

## General Commands

### /fi help \<page\>

**Description:** View a paginated list of available commands.
**Permission:** `fiefs.help`
**Usage:** `/fi help 1` or `/fi help 2`

### /fi list

**Description:** List all fiefs in your faction.
**Permission:** `fiefs.list`
**Usage:** `/fi list`

### /fi info [fief-name]

**Description:** View information about your fief or a specified fief.
**Permission:** `fiefs.info`
**Usage:** `/fi info` or `/fi info <fief-name>`

### /fi members [fief-name]

**Description:** View the members of your fief or a specified fief.
**Permission:** `fiefs.members`
**Usage:** `/fi members` or `/fi members <fief-name>`

## Fief Management Commands

### /fi create \<name\>

**Description:** Create a new fief within your faction.
**Permission:** `fiefs.create`
**Usage:** `/fi create <name>`

### /fi disband

**Description:** Disband your fief.
**Permission:** `fiefs.disband`
**Usage:** `/fi disband`

### /fi desc \<description\>

**Description:** Set or change the description of your fief.
**Permission:** `fiefs.desc`
**Usage:** `/fi desc <description>`

### /fi transfer \<player\>

**Description:** Transfer ownership of your fief to another player.
**Permission:** `fiefs.transfer`
**Usage:** `/fi transfer <player>`

## Land Commands

### /fi claim

**Description:** Claim the chunk you are standing in for your fief.
**Permission:** `fiefs.claim`
**Usage:** `/fi claim`

### /fi unclaim

**Description:** Unclaim the chunk you are standing in.
**Permission:** `fiefs.unclaim`
**Usage:** `/fi unclaim`

### /fi checkclaim

**Description:** Check which fief owns the chunk you are standing in.
**Permission:** `fiefs.checkclaim`
**Usage:** `/fi checkclaim`

## Member Commands

### /fi invite \<player\>

**Description:** Invite a player to your fief.
**Permission:** `fiefs.invite`
**Usage:** `/fi invite <player>`

### /fi join \<fief-name\>

**Description:** Join a fief you have been invited to.
**Permission:** `fiefs.join`
**Usage:** `/fi join <fief-name>`

### /fi leave

**Description:** Leave your current fief.
**Permission:** `fiefs.leave`
**Usage:** `/fi leave`

### /fi kick \<player\>

**Description:** Kick a player from your fief.
**Permission:** `fiefs.kick`
**Usage:** `/fi kick <player>`

## Configuration Commands

### /fi flags [flag] [value]

**Description:** View your fief's flags or set a specific flag value.
**Permission:** `fiefs.flags`
**Usage:** `/fi flags` or `/fi flags <flag> <value>`

### /fi config [option] [value]

**Description:** View or modify the plugin's configuration options.
**Permission:** `fiefs.config` (op only)
**Usage:** `/fi config show` or `/fi config set <option> <value>`
