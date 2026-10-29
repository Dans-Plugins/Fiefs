# Fiefs Configuration

Configuration can be viewed and changed in-game with `/fi config`. A `config.yml` is generated in `plugins/Fiefs/` on first run.

`/fi config set (option) (value)` finds the option whatever its case, so `debugmode` sets `debugMode`. A Boolean option takes only `true` or `false`, in any case; any other value is refused with "Value must be true or false." and the option is left as it was.

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `version` | String | *(plugin version)* | Plugin version. Do not edit manually. |
| `debugMode` | Boolean | `false` | Enables verbose debug logging to the console. |
| `limitLand` | Boolean | `true` | Currently has no effect: nothing reads it. `/fi claim` always requires land that the player's own faction has claimed, whatever this is set to. |
| `enableTerritoryAlerts` | Boolean | `true` | Whether players receive a message when entering or leaving fief territory. |
| `usage-reporting.enabled` | Boolean | `true` | Whether the plugin reports usage events (see below). Set to `false` to turn it off. |
| `usage-reporting.endpoint` | String | `https://trace.danielstephenson.dev` | The trace server events are sent to. |
| `usage-reporting.key` | String | the plugin's key | Identifies this plugin to the trace server so reports are attributed to it. Not a secret: it ships in the default config and can only report as Fiefs. Empty means reporting is off regardless of `enabled`. |

## Usage reporting

When the plugin is enabled, and each time one of its commands is used, a small event is sent to the
author's [trace](https://github.com/Stephenson-Software/trace-client-java) server so it is known which
plugins are actually in use. An event carries the plugin's name, the event name (`startup` or
`command`), the plugin version, and for a command the command name, plus a random server ID (the
`server-id` line in `plugins/trace/config.yml`, which identifies no person, account or IP address;
deleting the line gets a new one) — nothing about players or the world. Repeats are coalesced: the
first report of an event is sent at once, and identical reports in the next 60 seconds are counted
and sent as one event carrying the count, so a command run in a loop costs one report a minute
(`startup` is never held back). Sending happens off the main
thread, never delays a tick, and is dropped silently if the
server cannot be reached. The plugin says on every start whether reporting is on.

To turn it off for this plugin, set `usage-reporting.enabled` to `false`, in `config.yml` or with
`/fi config set usage-reporting.enabled false`. To turn it off for every plugin on the server that
reports to trace, set `enabled: false` in `plugins/trace/config.yml`, which the first such plugin to
start writes; plugins never turn it back on. The environment variables `TRACE_USAGE_REPORTING=off`
and `DO_NOT_TRACK=1` turn it off as well. Details:
https://danielstephenson.dev/usage-reporting

A `config.yml` written by a version before usage reporting existed has no `usage-reporting` block;
the plugin writes the block, with the bundled values, the first time it starts without it, so the
switch is always visible on disk. Should the file still lack a key, the bundled default is read.
