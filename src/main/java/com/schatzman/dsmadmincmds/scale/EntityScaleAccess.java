package com.schatzman.dsmadmincmds.scale;

public interface EntityScaleAccess {
	float dsm$getScale();

	void dsm$setScale(float scale);

	default void dsm$resetScale() {
		dsm$setScale(ScaleValues.DEFAULT_SCALE);
	}

	float dsm$getReachMultiplier();

	void dsm$setReachMultiplier(float multiplier);

	default void dsm$resetReachMultiplier() {
		dsm$setReachMultiplier(ScaleValues.UNSET_EXPLICIT_MULTIPLIER);
	}

	float dsm$getEffectiveReachMultiplier();

	float dsm$getSpeedMultiplier();

	void dsm$setSpeedMultiplier(float multiplier);

	default void dsm$resetSpeedMultiplier() {
		dsm$setSpeedMultiplier(ScaleValues.UNSET_EXPLICIT_MULTIPLIER);
	}

	float dsm$getEffectiveSpeedMultiplier();

	float dsm$getJumpMultiplier();

	void dsm$setJumpMultiplier(float multiplier);

	default void dsm$resetJumpMultiplier() {
		dsm$setJumpMultiplier(ScaleValues.UNSET_EXPLICIT_MULTIPLIER);
	}

	float dsm$getEffectiveJumpMultiplier();

	boolean dsm$isEntityCollisionEnabled();

	void dsm$setEntityCollisionEnabled(boolean enabled);

	boolean dsm$isInvulnerableAdminEnabled();

	void dsm$setInvulnerableAdminEnabled(boolean enabled);
}
