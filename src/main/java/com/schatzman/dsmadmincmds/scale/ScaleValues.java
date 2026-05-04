package com.schatzman.dsmadmincmds.scale;

public final class ScaleValues {
	public static final float DEFAULT_SCALE = 1.0F;
	public static final float MIN_SCALE = 0.1F;
	public static final float MAX_SCALE = 10.0F;
	public static final float EPSILON = 0.0001F;

	private ScaleValues() {
	}

	public static float clamp(float scale) {
		return Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale));
	}

	public static boolean isDefault(float scale) {
		return Math.abs(scale - DEFAULT_SCALE) < EPSILON;
	}
}
