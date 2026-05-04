package com.schatzman.dsmadmincmds.mixin;

import com.schatzman.dsmadmincmds.scale.EntityScaleAccess;
import com.schatzman.dsmadmincmds.scale.ScaleValues;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityScaleMixin implements EntityScaleAccess {
	@Unique
	private static final String DSM_SCALE_NBT_KEY = "DsmAdminScale";
	@Unique
	private static final EntityDataAccessor<Float> DSM_SCALE = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.FLOAT);

	@Shadow
	@Final
	protected SynchedEntityData entityData;

	@Unique
	private boolean dsm$scaleDataDefined;

	@Shadow
	public abstract void refreshDimensions();

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;defineSynchedData()V", shift = At.Shift.AFTER))
	private void dsm$defineScaleData(EntityType<?> entityType, Level level, CallbackInfo ci) {
		this.entityData.define(DSM_SCALE, ScaleValues.DEFAULT_SCALE);
		this.dsm$scaleDataDefined = true;
	}

	@Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER))
	private void dsm$writeScaleData(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> cir) {
		float scale = dsm$getScale();
		if (!ScaleValues.isDefault(scale)) {
			nbt.putFloat(DSM_SCALE_NBT_KEY, scale);
		}
	}

	@Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER))
	private void dsm$readScaleData(CompoundTag nbt, CallbackInfo ci) {
		if (nbt.contains(DSM_SCALE_NBT_KEY, Tag.TAG_ANY_NUMERIC)) {
			dsm$setScale(nbt.getFloat(DSM_SCALE_NBT_KEY));
		} else {
			dsm$resetScale();
		}
	}

	@Inject(method = "onSyncedDataUpdated(Lnet/minecraft/network/syncher/EntityDataAccessor;)V", at = @At("TAIL"))
	private void dsm$onSyncedScaleUpdated(EntityDataAccessor<?> data, CallbackInfo ci) {
		if (DSM_SCALE.equals(data)) {
			refreshDimensions();
		}
	}

	@Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true)
	private void dsm$scaleDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
		float scale = dsm$getScale();
		if (!ScaleValues.isDefault(scale)) {
			cir.setReturnValue(cir.getReturnValue().scale(scale));
		}
	}

	@Override
	public float dsm$getScale() {
		if (!this.dsm$scaleDataDefined) {
			return ScaleValues.DEFAULT_SCALE;
		}

		return this.entityData.get(DSM_SCALE);
	}

	@Override
	public void dsm$setScale(float scale) {
		if (!this.dsm$scaleDataDefined) {
			return;
		}

		float clampedScale = ScaleValues.clamp(scale);
		float previousScale = dsm$getScale();
		this.entityData.set(DSM_SCALE, clampedScale);
		if (Math.abs(previousScale - clampedScale) >= ScaleValues.EPSILON) {
			refreshDimensions();
		}
	}
}
