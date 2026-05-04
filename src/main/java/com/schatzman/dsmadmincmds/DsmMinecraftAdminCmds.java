package com.schatzman.dsmadmincmds;

import com.schatzman.dsmadmincmds.command.ScaleCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public final class DsmMinecraftAdminCmds implements ModInitializer {
	public static final String MOD_ID = "dsm_minecraft_admin_cmds";

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ScaleCommand.register(dispatcher));
	}
}
