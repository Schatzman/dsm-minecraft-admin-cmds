package com.schatzman.dsmadmincmds.mixin;

import com.schatzman.dsmadmincmds.scale.EntityScaleAccess;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityCollisionToggleMixin {
	@Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
	private void dsm$skipEntityPushWhenDisabled(Entity other, CallbackInfo ci) {
		EntityScaleAccess selfAccess = (EntityScaleAccess) this;
		EntityScaleAccess otherAccess = (EntityScaleAccess) other;
		if (!selfAccess.dsm$isEntityCollisionEnabled() || !otherAccess.dsm$isEntityCollisionEnabled()) {
			ci.cancel();
		}
	}
}
