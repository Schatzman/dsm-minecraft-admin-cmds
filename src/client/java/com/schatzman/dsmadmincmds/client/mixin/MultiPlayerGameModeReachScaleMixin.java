package com.schatzman.dsmadmincmds.client.mixin;

import com.schatzman.dsmadmincmds.scale.EntityScaleAccess;
import com.schatzman.dsmadmincmds.scale.ScaleValues;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeReachScaleMixin {
	@Inject(method = "getPickRange", at = @At("RETURN"), cancellable = true)
	private void dsm$scalePickRange(CallbackInfoReturnable<Float> cir) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) {
			return;
		}

		float reachMultiplier = ((EntityScaleAccess) player).dsm$getEffectiveReachMultiplier();
		if (!ScaleValues.isDefault(reachMultiplier)) {
			cir.setReturnValue((float) ScaleValues.scaleByMultiplier(cir.getReturnValueF(), reachMultiplier));
		}
	}
}
