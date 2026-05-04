package com.schatzman.dsmadmincmds.mixin;

import com.schatzman.dsmadmincmds.scale.EntityScaleAccess;
import com.schatzman.dsmadmincmds.scale.ScaleValues;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityJumpScaleMixin {
	@Inject(method = "getJumpPower", at = @At("RETURN"), cancellable = true)
	private void dsm$scaleJumpPower(CallbackInfoReturnable<Float> cir) {
		float scale = ((EntityScaleAccess) this).dsm$getScale();
		if (!ScaleValues.isDefault(scale)) {
			cir.setReturnValue((float) (cir.getReturnValueF() * ScaleValues.jumpVelocityMultiplier(scale)));
		}
	}
}
