# Fiefs Configuration

Configuration can be viewed and changed in-game with `/fi config`. A `config.yml` is generated in `plugins/Fiefs/` on first run.

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `version` | String | *(plugin version)* | Plugin version. Do not edit manually. |
| `debugMode` | Boolean | `false` | Enables verbose debug logging to the console. |
| `limitLand` | Boolean | `true` | Whether fiefs are restricted to land already claimed by their faction. |
| `enableTerritoryAlerts` | Boolean | `true` | Whether players receive a message when entering or leaving fief territory. |
| `usage-reporting.enabled` | Boolean | `true` | Whether the plugin reports usage events (see below). Set to `false` to turn it off. |
| `usage-reporting.endpoint` | String | `https://trace.danielstephenson.dev` | The trace server events are sent to. |
| `usage-reporting.key` | String | the plugin's key | Identifies this plugin to the trace server so reports are attributed to it. Not a secret: it ships in the default config and can only report as Fiefs. Empty means reporting is off regardless of `enabled`. |

## Usage reporting

When the plugin is enabled, and each time one of its commands is used, a small event is sent to the
author's [trace](https://github.com/Stephenson-Software/trace-client-java) server so it is known which
plugins are actually in use. An event carries the plugin's name, the event name (`startup` or
`command`), and either the plugin version or the command name — nothing about players, the world, or
the server. Sending happens off the main thread, never delays a tick, and is dropped silently if the
server cannot be reached. The plugin says on every start whether reporting is on.

To turn it off for this plugin, set `usage-reporting.enabled` to `false`, in `config.yml` or with
`/fi config set usage-reporting.enabled false`. To turn it off for every plugin on the server that
reports to trace, set `enabled: false` in `plugins/trace/config.yml`, which the first such plugin to
start writes; plugins never turn it back on. The environment variables `TRACE_USAGE_REPORTING=off`
and `DO_NOT_TRACK=1` turn it off as well. Details:
https://github.com/Stephenson-Software/trace#usage-reporting

A `config.yml` written by a version before usage reporting existed has no `usage-reporting` block;
the plugin writes the block, with the bundled values, the first time it starts without it, so the
switch is always visible on disk. Should the file still lack a key, the bundled default is read.
