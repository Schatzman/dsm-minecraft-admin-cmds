package com.schatzman.dsmadmincmds.mixin;

import com.schatzman.dsmadmincmds.scale.EntityScaleAccess;
import com.schatzman.dsmadmincmds.scale.ScaleValues;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerEyeHeightScaleMixin {
	@Inject(method = "getStandingEyeHeight", at = @At("RETURN"), cancellable = true)
	private void dsm$scaleStandingEyeHeight(Pose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> cir) {
		float scale = ((EntityScaleAccess) this).dsm$getScale();
		if (!ScaleValues.isDefault(scale)) {
			cir.setReturnValue(cir.getReturnValueF() * scale);
		}
	}
}
