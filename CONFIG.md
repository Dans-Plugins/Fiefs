# Configuration Guide

Fiefs stores its configuration in `plugins/Fiefs/config.yml`. Options can also be viewed with `/fi config show` and changed in-game using `/fi config set <option> <value>` (requires op).

## version

**Type:** string
**Default:** Current plugin version
**Description:** The plugin version that last wrote the config file. This is set automatically and should not be changed manually.

## debugMode

**Type:** boolean
**Default:** `false`
**Description:** Enables debug logging for the plugin. Useful for troubleshooting issues.

```yaml
debugMode: false
```

## limitLand

**Type:** boolean
**Default:** `true`
**Description:** When enabled, limits the amount of land a fief can claim.

```yaml
limitLand: true
```

## enableTerritoryAlerts

**Type:** boolean
**Default:** `true`
**Description:** When enabled, players receive alerts when entering or leaving fief territory.

```yaml
enableTerritoryAlerts: true
```
