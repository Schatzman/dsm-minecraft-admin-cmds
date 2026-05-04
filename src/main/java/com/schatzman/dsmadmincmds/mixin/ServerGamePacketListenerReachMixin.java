package com.schatzman.dsmadmincmds.mixin;

import com.schatzman.dsmadmincmds.scale.EntityScaleAccess;
import com.schatzman.dsmadmincmds.scale.ScaleValues;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerReachMixin {
	@Shadow
	public ServerPlayer player;

	@Redirect(method = "handleUseItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D"))
	private double dsm$scaleUseItemOnEyeReach(Vec3 instance, Vec3 other) {
		return dsm$unscaleDistanceSqr(instance.distanceToSqr(other));
	}

	@Redirect(method = "handleUseItemOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;distanceToSqr(DDD)D"))
	private double dsm$scaleUseItemOnPlacementReach(ServerPlayer player, double x, double y, double z) {
		return dsm$unscaleDistanceSqr(player.distanceToSqr(x, y, z));
	}

	@Redirect(method = "handleInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D"))
	private double dsm$scaleEntityInteractionReach(AABB box, Vec3 position) {
		return dsm$unscaleDistanceSqr(box.distanceToSqr(position));
	}

	private double dsm$unscaleDistanceSqr(double distanceSqr) {
		float scale = ((EntityScaleAccess) this.player).dsm$getScale();
		return ScaleValues.unscaleReachDistanceSqrForVanillaCheck(distanceSqr, scale);
	}
}
