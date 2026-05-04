package com.schatzman.dsmadmincmds.client.mixin;

import com.schatzman.dsmadmincmds.scale.EntityScaleAccess;
import com.schatzman.dsmadmincmds.scale.ScaleValues;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GameRenderer.class)
public abstract class GameRendererReachScaleMixin {
	@ModifyConstant(method = "pick", constant = @Constant(doubleValue = 6.0D))
	private double dsm$scaleCreativeEntityReach(double distance) {
		return dsm$scaleLocalPlayerDistance(distance);
	}

	@ModifyConstant(method = "pick", constant = @Constant(doubleValue = 3.0D))
	private double dsm$scaleSurvivalEntityReachFlag(double distance) {
		return dsm$scaleLocalPlayerDistance(distance);
	}

	@ModifyConstant(method = "pick", constant = @Constant(doubleValue = 9.0D))
	private double dsm$scaleSurvivalEntityReachLimit(double distanceSqr) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) {
			return distanceSqr;
		}

		float reachMultiplier = ((EntityScaleAccess) player).dsm$getEffectiveReachMultiplier();
		if (ScaleValues.isDefault(reachMultiplier)) {
			return distanceSqr;
		}

		double scaledDistance = ScaleValues.scaleByMultiplier(Math.sqrt(distanceSqr), reachMultiplier);
		return scaledDistance * scaledDistance;
	}

	private double dsm$scaleLocalPlayerDistance(double distance) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) {
			return distance;
		}

		return ScaleValues.scaleReachDistance(distance, (EntityScaleAccess) player);
	}
}
