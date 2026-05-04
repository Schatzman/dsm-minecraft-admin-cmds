package com.schatzman.dsmadmincmds.mixin;

import com.schatzman.dsmadmincmds.scale.EntityScaleAccess;
import com.schatzman.dsmadmincmds.scale.ScaleAttributeModifiers;
import com.schatzman.dsmadmincmds.scale.ScaleValues;
import java.util.ArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityScaleMixin implements EntityScaleAccess {
	@Unique
	private static final String DSM_SCALE_NBT_KEY = "DsmAdminScale";
	@Unique
	private static final String DSM_REACH_MULTIPLIER_NBT_KEY = "DsmAdminReachMultiplier";
	@Unique
	private static final String DSM_SPEED_MULTIPLIER_NBT_KEY = "DsmAdminSpeedMultiplier";
	@Unique
	private static final String DSM_JUMP_MULTIPLIER_NBT_KEY = "DsmAdminJumpMultiplier";
	@Unique
	private static final String DSM_ENTITY_COLLISION_ENABLED_NBT_KEY = "DsmAdminEntityCollisionEnabled";
	@Unique
	private static final String DSM_INVULNERABLE_ADMIN_ENABLED_NBT_KEY = "DsmAdminInvulnerableEnabled";
	@Unique
	private static final EntityDataAccessor<Float> DSM_SCALE = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.FLOAT);
	@Unique
	private static final EntityDataAccessor<Float> DSM_REACH_MULTIPLIER = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.FLOAT);
	@Unique
	private static final EntityDataAccessor<Float> DSM_SPEED_MULTIPLIER = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.FLOAT);
	@Unique
	private static final EntityDataAccessor<Float> DSM_JUMP_MULTIPLIER = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.FLOAT);
	@Unique
	private static final EntityDataAccessor<Boolean> DSM_ENTITY_COLLISION_ENABLED = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);
	@Unique
	private static final EntityDataAccessor<Boolean> DSM_INVULNERABLE_ADMIN_ENABLED = SynchedEntityData.defineId(Entity.class, EntityDataSerializers.BOOLEAN);

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
		this.entityData.define(DSM_REACH_MULTIPLIER, ScaleValues.UNSET_EXPLICIT_MULTIPLIER);
		this.entityData.define(DSM_SPEED_MULTIPLIER, ScaleValues.UNSET_EXPLICIT_MULTIPLIER);
		this.entityData.define(DSM_JUMP_MULTIPLIER, ScaleValues.UNSET_EXPLICIT_MULTIPLIER);
		this.entityData.define(DSM_ENTITY_COLLISION_ENABLED, true);
		this.entityData.define(DSM_INVULNERABLE_ADMIN_ENABLED, false);
		this.dsm$scaleDataDefined = true;
	}

	@Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER))
	private void dsm$writeScaleData(CompoundTag nbt, CallbackInfoReturnable<CompoundTag> cir) {
		float scale = dsm$getScale();
		if (!ScaleValues.isDefault(scale)) {
			nbt.putFloat(DSM_SCALE_NBT_KEY, scale);
		}
		dsm$putExplicitMultiplier(nbt, DSM_REACH_MULTIPLIER_NBT_KEY, dsm$getReachMultiplier());
		dsm$putExplicitMultiplier(nbt, DSM_SPEED_MULTIPLIER_NBT_KEY, dsm$getSpeedMultiplier());
		dsm$putExplicitMultiplier(nbt, DSM_JUMP_MULTIPLIER_NBT_KEY, dsm$getJumpMultiplier());
		if (!dsm$isEntityCollisionEnabled()) {
			nbt.putBoolean(DSM_ENTITY_COLLISION_ENABLED_NBT_KEY, false);
		}
		if (dsm$isInvulnerableAdminEnabled()) {
			nbt.putBoolean(DSM_INVULNERABLE_ADMIN_ENABLED_NBT_KEY, true);
		}
	}

	@Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V", shift = At.Shift.AFTER))
	private void dsm$readScaleData(CompoundTag nbt, CallbackInfo ci) {
		if (nbt.contains(DSM_SCALE_NBT_KEY, Tag.TAG_ANY_NUMERIC)) {
			dsm$setScale(nbt.getFloat(DSM_SCALE_NBT_KEY));
		} else {
			dsm$resetScale();
		}
		dsm$setReachMultiplier(dsm$readExplicitMultiplier(nbt, DSM_REACH_MULTIPLIER_NBT_KEY));
		dsm$setSpeedMultiplier(dsm$readExplicitMultiplier(nbt, DSM_SPEED_MULTIPLIER_NBT_KEY));
		dsm$setJumpMultiplier(dsm$readExplicitMultiplier(nbt, DSM_JUMP_MULTIPLIER_NBT_KEY));
		dsm$setEntityCollisionEnabled(!nbt.contains(DSM_ENTITY_COLLISION_ENABLED_NBT_KEY, Tag.TAG_BYTE) || nbt.getBoolean(DSM_ENTITY_COLLISION_ENABLED_NBT_KEY));
		dsm$setInvulnerableAdminEnabled(nbt.contains(DSM_INVULNERABLE_ADMIN_ENABLED_NBT_KEY, Tag.TAG_BYTE) && nbt.getBoolean(DSM_INVULNERABLE_ADMIN_ENABLED_NBT_KEY));
	}

	@Inject(method = "onSyncedDataUpdated(Lnet/minecraft/network/syncher/EntityDataAccessor;)V", at = @At("TAIL"))
	private void dsm$onSyncedScaleUpdated(EntityDataAccessor<?> data, CallbackInfo ci) {
		if (DSM_SCALE.equals(data)) {
			dsm$applyScaleSideEffects();
			refreshDimensions();
		} else if (DSM_SPEED_MULTIPLIER.equals(data)) {
			dsm$applyScaleSideEffects();
		}
	}

	@Redirect(method = "refreshDimensions", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getDimensions(Lnet/minecraft/world/entity/Pose;)Lnet/minecraft/world/entity/EntityDimensions;"))
	private EntityDimensions dsm$scaleRefreshDimensions(Entity entity, Pose pose) {
		EntityDimensions dimensions = entity.getDimensions(pose);
		float scale = dsm$getScale();
		return ScaleValues.isDefault(scale) ? dimensions : dimensions.scale(scale);
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
		dsm$applyScaleSideEffects();
		if (Math.abs(previousScale - clampedScale) >= ScaleValues.EPSILON) {
			refreshDimensions();
		}
	}

	@Override
	public float dsm$getReachMultiplier() {
		return dsm$getFloatData(DSM_REACH_MULTIPLIER, ScaleValues.UNSET_EXPLICIT_MULTIPLIER);
	}

	@Override
	public void dsm$setReachMultiplier(float multiplier) {
		dsm$setFloatData(DSM_REACH_MULTIPLIER, dsm$sanitizeExplicitMultiplier(multiplier));
	}

	@Override
	public float dsm$getEffectiveReachMultiplier() {
		float reachMultiplier = dsm$getReachMultiplier();
		return ScaleValues.hasExplicitMultiplier(reachMultiplier) ? reachMultiplier : (float) ScaleValues.reachMultiplier(dsm$getScale());
	}

	@Override
	public float dsm$getSpeedMultiplier() {
		return dsm$getFloatData(DSM_SPEED_MULTIPLIER, ScaleValues.UNSET_EXPLICIT_MULTIPLIER);
	}

	@Override
	public void dsm$setSpeedMultiplier(float multiplier) {
		dsm$setFloatData(DSM_SPEED_MULTIPLIER, dsm$sanitizeExplicitMultiplier(multiplier));
		dsm$applyScaleSideEffects();
	}

	@Override
	public float dsm$getEffectiveSpeedMultiplier() {
		float speedMultiplier = dsm$getSpeedMultiplier();
		return ScaleValues.hasExplicitMultiplier(speedMultiplier) ? speedMultiplier : (float) ScaleValues.movementSpeedMultiplier(dsm$getScale());
	}

	@Override
	public float dsm$getJumpMultiplier() {
		return dsm$getFloatData(DSM_JUMP_MULTIPLIER, ScaleValues.UNSET_EXPLICIT_MULTIPLIER);
	}

	@Override
	public void dsm$setJumpMultiplier(float multiplier) {
		dsm$setFloatData(DSM_JUMP_MULTIPLIER, dsm$sanitizeExplicitMultiplier(multiplier));
	}

	@Override
	public float dsm$getEffectiveJumpMultiplier() {
		float jumpMultiplier = dsm$getJumpMultiplier();
		return ScaleValues.hasExplicitMultiplier(jumpMultiplier) ? jumpMultiplier : (float) ScaleValues.jumpVelocityMultiplier(dsm$getScale());
	}

	@Override
	public boolean dsm$isEntityCollisionEnabled() {
		return !this.dsm$scaleDataDefined || this.entityData.get(DSM_ENTITY_COLLISION_ENABLED);
	}

	@Override
	public void dsm$setEntityCollisionEnabled(boolean enabled) {
		if (this.dsm$scaleDataDefined) {
			this.entityData.set(DSM_ENTITY_COLLISION_ENABLED, enabled);
		}
	}

	@Override
	public boolean dsm$isInvulnerableAdminEnabled() {
		return this.dsm$scaleDataDefined && this.entityData.get(DSM_INVULNERABLE_ADMIN_ENABLED);
	}

	@Override
	public void dsm$setInvulnerableAdminEnabled(boolean enabled) {
		if (!this.dsm$scaleDataDefined) {
			return;
		}

		this.entityData.set(DSM_INVULNERABLE_ADMIN_ENABLED, enabled);
		if (enabled) {
			dsm$clearHarmfulEffects();
		}
	}

	@Unique
	private void dsm$applyScaleSideEffects() {
		ScaleAttributeModifiers.apply(this, this);
	}

	@Unique
	private void dsm$clearHarmfulEffects() {
		if (!((Object) this instanceof LivingEntity livingEntity)) {
			return;
		}

		for (MobEffectInstance effect : new ArrayList<>(livingEntity.getActiveEffects())) {
			if (effect.getEffect().getCategory() == MobEffectCategory.HARMFUL) {
				livingEntity.removeEffect(effect.getEffect());
			}
		}
	}

	@Unique
	private static void dsm$putExplicitMultiplier(CompoundTag nbt, String key, float multiplier) {
		if (ScaleValues.hasExplicitMultiplier(multiplier)) {
			nbt.putFloat(key, multiplier);
		}
	}

	@Unique
	private static float dsm$readExplicitMultiplier(CompoundTag nbt, String key) {
		return nbt.contains(key, Tag.TAG_ANY_NUMERIC) ? nbt.getFloat(key) : ScaleValues.UNSET_EXPLICIT_MULTIPLIER;
	}

	@Unique
	private static float dsm$sanitizeExplicitMultiplier(float multiplier) {
		return ScaleValues.hasExplicitMultiplier(multiplier) ? ScaleValues.clampMultiplier(multiplier) : ScaleValues.UNSET_EXPLICIT_MULTIPLIER;
	}

	@Unique
	private float dsm$getFloatData(EntityDataAccessor<Float> data, float fallback) {
		return this.dsm$scaleDataDefined ? this.entityData.get(data) : fallback;
	}

	@Unique
	private void dsm$setFloatData(EntityDataAccessor<Float> data, float value) {
		if (this.dsm$scaleDataDefined) {
			this.entityData.set(data, value);
		}
	}
}
