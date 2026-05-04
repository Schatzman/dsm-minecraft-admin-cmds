package com.schatzman.dsmadmincmds.mixin;

import com.schatzman.dsmadmincmds.scale.EntityScaleAccess;
import com.schatzman.dsmadmincmds.scale.ScaleValues;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerGameModeReachMixin {
	@Shadow
	@Final
	protected ServerPlayer player;

	@Redirect(method = "handleBlockBreakAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D"))
	private double dsm$scaleBlockBreakReach(Vec3 instance, Vec3 other) {
		float scale = ((EntityScaleAccess) this.player).dsm$getScale();
		return ScaleValues.unscaleDistanceSqrForVanillaCheck(instance.distanceToSqr(other), scale);
	}
}
