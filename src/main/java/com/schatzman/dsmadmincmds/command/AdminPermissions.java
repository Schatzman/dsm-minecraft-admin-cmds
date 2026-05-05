package com.schatzman.dsmadmincmds.command;

import net.minecraft.commands.CommandSourceStack;

public final class AdminPermissions {
	private static final int CHEAT_PERMISSION_LEVEL = 2;

	private AdminPermissions() {
	}

	public static boolean canUseCheatCommand(CommandSourceStack source) {
		return source.hasPermission(CHEAT_PERMISSION_LEVEL);
	}
}
