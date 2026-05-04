package com.schatzman.dsmadmincmds.client.mixin;

import com.schatzman.dsmadmincmds.scale.EntityScaleAccess;
import com.schatzman.dsmadmincmds.scale.ScaleValues;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Camera.class)
public abstract class CameraScaleMixin {
	@Shadow
	private Entity entity;

	@ModifyArg(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getMaxZoom(D)D"), index = 0)
	private double dsm$scaleThirdPersonCameraDistance(double distance) {
		if (this.entity == null) {
			return distance;
		}

		float scale = ((EntityScaleAccess) this.entity).dsm$getScale();
		return ScaleValues.isDefault(scale) ? distance : distance * scale;
	}
}
