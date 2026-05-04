package com.schatzman.dsmadmincmds.command;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public final class AdminPermissions {
	private static final int CHEAT_PERMISSION_LEVEL = 2;

	private AdminPermissions() {
	}

	public static boolean canUseCheatCommand(CommandSourceStack source) {
		if (!source.hasPermission(CHEAT_PERMISSION_LEVEL)) {
			return false;
		}

		Entity entity = source.getEntity();
		return !(entity instanceof ServerPlayer player) || player.isCreative();
	}
}
