package com.schatzman.dsmadmincmds.mixin;

import com.schatzman.dsmadmincmds.scale.EntityScaleAccess;
import com.schatzman.dsmadmincmds.scale.ScaleValues;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseJumpScaleMixin {
	@Inject(method = "getCustomJump", at = @At("RETURN"), cancellable = true)
	private void dsm$scaleHorseCustomJump(CallbackInfoReturnable<Double> cir) {
		float jumpMultiplier = ((EntityScaleAccess) this).dsm$getEffectiveJumpMultiplier();
		if (!ScaleValues.isDefault(jumpMultiplier)) {
			cir.setReturnValue(cir.getReturnValueD() * jumpMultiplier);
		}
	}
}
