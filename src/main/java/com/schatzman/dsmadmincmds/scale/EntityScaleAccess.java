package com.schatzman.dsmadmincmds.scale;

public interface EntityScaleAccess {
	float dsm$getScale();

	void dsm$setScale(float scale);

	default void dsm$resetScale() {
		dsm$setScale(ScaleValues.DEFAULT_SCALE);
	}
}
