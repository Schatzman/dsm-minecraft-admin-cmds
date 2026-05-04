package com.schatzman.dsmadmincmds.mixin;

import com.schatzman.dsmadmincmds.scale.EntityScaleAccess;
import com.schatzman.dsmadmincmds.scale.ScaleValues;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityPoseEyeHeightScaleMixin {
	@Inject(method = "getEyeHeight(Lnet/minecraft/world/entity/Pose;)F", at = @At("RETURN"), cancellable = true)
	private void dsm$scalePoseEyeHeight(Pose pose, CallbackInfoReturnable<Float> cir) {
		Entity entity = (Entity) (Object) this;
		if (entity instanceof Player) {
			return;
		}

		float scale = ((EntityScaleAccess) entity).dsm$getScale();
		if (!ScaleValues.isDefault(scale)) {
			cir.setReturnValue(cir.getReturnValueF() * scale);
		}
	}
}
