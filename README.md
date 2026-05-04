# DSM Minecraft Admin Commands

Fabric admin commands for a private Minecraft server. Command sets add `/dsm-scale` plus explicit reach, speed, jump, collision, and invulnerability controls.

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
- `/dsm-scale set <targets> <scale>` - set selected living entities to a scale from `0.01` to `100.0`.
- `/dsm-reach set <targets> <multiplier>` - set explicit reach multiplier for selected living entities.
- `/dsm-reach reset <targets>` - clear explicit reach multiplier and return to scale-derived reach.
- `/dsm-reach larger <targets> [factor]` - multiply current effective reach, default factor `1.25`.
- `/dsm-reach smaller <targets> [factor]` - multiply current effective reach, default factor `0.8`.
- `/dsm-speed set|reset|larger|smaller <targets> ...` - same multiplier shape for movement/flying speed.
- `/dsm-jump set|reset|larger|smaller <targets> ...` - same multiplier shape for jump velocity.
- `/dsm-collision on|off <targets>` - toggle entity-to-entity physical pushing. Block/world collision, targeting, melee, and projectile hits are not intentionally disabled.
- `/dsm-invulnerable on|off <targets>` - toggle admin invulnerability without changing game mode. Damage is blocked and new status effects are rejected; existing harmful effects are cleared when toggled on.

Scale is synchronized with tracked entity data, persisted in NBT, applied to entity dimensions/hitboxes, and applied visually on clients for living entity renders. Reach, speed, and jump use scale-derived defaults until an explicit multiplier is set; `reset` clears the explicit axis override. Speed uses stable transient attribute modifiers, removing the previous DSM modifier before applying the current multiplier.
