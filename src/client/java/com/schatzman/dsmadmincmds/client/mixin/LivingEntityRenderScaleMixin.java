package com.schatzman.dsmadmincmds.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.schatzman.dsmadmincmds.scale.EntityScaleAccess;
import com.schatzman.dsmadmincmds.scale.ScaleValues;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRenderScaleMixin<T extends LivingEntity> {
	@Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("HEAD"))
	private void dsm$pushScale(T livingEntity, float entityYaw, float tickDelta, PoseStack poseStack,
			MultiBufferSource multiBufferSource, int light, CallbackInfo ci) {
		poseStack.pushPose();
		float scale = ((EntityScaleAccess) livingEntity).dsm$getScale();
		if (!ScaleValues.isDefault(scale)) {
			poseStack.scale(scale, scale, scale);
		}
	}

	@Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("RETURN"))
	private void dsm$popScale(T livingEntity, float entityYaw, float tickDelta, PoseStack poseStack,
			MultiBufferSource multiBufferSource, int light, CallbackInfo ci) {
		poseStack.popPose();
	}
}
