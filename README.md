# DSM Minecraft Admin Commands

Fabric admin commands for a private Minecraft server. The first command set adds `/dsm-scale` for safely scaling living entities.

## Target

- Minecraft 1.20.1
- Fabric Loader 0.18.6
- Mod id: `dsm_minecraft_admin_cmds`
- License: MIT

## Commands

All commands require permission level 2 or higher. Player command sources must also be in creative mode; console/admin sources may execute when they have permission.

- `/dsm-scale larger <targets> [factor]` - multiply selected living entities by `factor`, default `1.25`.
- `/dsm-scale smaller <targets> [factor]` - multiply selected living entities by `factor`, default `0.8`.
- `/dsm-scale reset <targets>` - reset selected living entities to `1.0`.
- `/dsm-scale set <targets> <scale>` - set selected living entities to a scale from `0.1` to `10.0`.

The scale is synchronized with tracked entity data, persisted in NBT, applied to entity dimensions/hitboxes, and applied visually on clients for living entity renders.
