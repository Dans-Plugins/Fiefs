# Fiefs Commands

All commands use `/fi` or `/fiefs` as the base. Fiefs requires Medieval Factions to be installed.

In the syntax below, `<value>` is required, `[value]` is optional, and `{a|b}` means one of the listed forms.

| Command | Description | Permission |
|---------|-------------|------------|
| `/fi` | View plugin version and info. | `fiefs.default` |
| `/fi help {1\|2}` | View command list (2 pages). | `fiefs.help` |
| `/fi list` | List the fiefs in your faction. | `fiefs.list` |
| `/fi create "<name>"` | Create a new fief in your faction. | `fiefs.create` |
| `/fi disband` | Disband your fief. | `fiefs.disband` |
| `/fi info [fief]` | View information about your fief, or about the named fief. | `fiefs.info` |
| `/fi members [fief]` | View members of your fief, or of the named fief. | `fiefs.members` |
| `/fi join <fief>` | Join a fief you have been invited to. | `fiefs.join` |
| `/fi leave` | Leave your current fief. | `fiefs.leave` |
| `/fi invite <player>` | Invite a player to your fief. | `fiefs.invite` |
| `/fi kick <player>` | Kick a player from your fief. | `fiefs.kick` |
| `/fi transfer <player>` | Transfer ownership of your fief to another player. | `fiefs.transfer` |
| `/fi desc "<description>"` | Set a description for your fief. | `fiefs.desc` |
| `/fi rename "<name>"` | Rename your fief. | `fiefs.rename` |
| `/fi claim` | Claim a chunk of faction land for your fief. | `fiefs.claim` |
| `/fi unclaim` | Unclaim a chunk of land from your fief. | `fiefs.unclaim` |
| `/fi checkclaim` | Check which fief owns the chunk you are standing in. | `fiefs.checkclaim` |
| `/fi flags {show\|set <flag> <value>}` | View and alter your fief's configuration flags. | `fiefs.flags` |
| `/fi config {show\|set <option> <value>}` | View and alter plugin config options. | `fiefs.config` |
| `/fi whois <player>` | Check which fief a player is a member of. | `fiefs.whois` |

`/fi create`, `/fi rename` and `/fi desc` take their text in double quotes, for example `/fi create "South Reach"`. `/fi join`, `/fi info` and `/fi members` take a fief name with or without double quotes: `/fi join "South Reach"` and `/fi join South Reach` both work.
