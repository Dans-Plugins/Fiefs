# Copilot Instructions

This repository follows the DPC (Dans Plugins Community) conventions defined at
https://github.com/Dans-Plugins/dpc-conventions. Read those conventions before
making any changes.

## Technology Stack

- Language: Java
- Build tool: Maven
- Target platform: Spigot / Paper (Minecraft plugin)
- Dependency: Medieval Factions, Ponder

## Project Structure

- `src/main/java/` – Plugin source code (`dansplugins.fiefs` package)
- `src/main/resources/` – `plugin.yml` and resource files
- `pom.xml` – Maven build configuration

### Key Packages

- `dansplugins.fiefs` – Main plugin class
- `dansplugins.fiefs.commands` – Command executors
- `dansplugins.fiefs.listeners` – Event listeners
- `dansplugins.fiefs.objects` – Domain objects (Fief, FiefFlags, ClaimedChunk)
- `dansplugins.fiefs.services` – Services (Config, Storage, Chunk)
- `dansplugins.fiefs.data` – Persistent data management
- `dansplugins.fiefs.externalapi` – Public API for other plugins
- `dansplugins.fiefs.integrators` – Medieval Factions integration
- `dansplugins.fiefs.utils` – Utility classes (Logger, Scheduler)

## Coding Conventions

- Follow the existing package structure when adding new classes.
- Annotate every command executor and event listener with `@Override` where applicable.
- Config options are managed through `ConfigService`; add new options there.
- Fief flags are managed through `FiefFlags`; follow the documented pattern to add new flags.

## Contribution Workflow

- Branch from `develop` for all changes.
- Open a pull request against `develop`, not `main`.
- Reference the related GitHub issue in every pull request description.
