package com.schatzman.dsmadmincmds.scale;

public final class ScaleValues {
	public static final float DEFAULT_SCALE = 1.0F;
	public static final float MIN_SCALE = 0.01F;
	public static final float MAX_SCALE = 100.0F;
	public static final float EPSILON = 0.0001F;
	private static final double MOVEMENT_SPEED_EXPONENT = 0.75D;
	private static final double REACH_EXPONENT = 0.75D;
	private static final double JUMP_VELOCITY_EXPONENT = 0.5D;

	private ScaleValues() {
	}

	public static float clamp(float scale) {
		return Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale));
	}

	public static boolean isDefault(float scale) {
		return Math.abs(scale - DEFAULT_SCALE) < EPSILON;
	}

	public static double movementSpeedMultiplier(float scale) {
		return abilityMultiplier(scale, MOVEMENT_SPEED_EXPONENT);
	}

	public static double reachMultiplier(float scale) {
		return abilityMultiplier(scale, REACH_EXPONENT);
	}

	public static double jumpVelocityMultiplier(float scale) {
		// Jump height follows initial velocity squared, so sqrt(scale) keeps height proportional to body size.
		return abilityMultiplier(scale, JUMP_VELOCITY_EXPONENT);
	}

	public static double scaleDistance(double distance, float scale) {
		return isDefault(scale) ? distance : distance * scale;
	}

	public static double scaleReachDistance(double distance, float scale) {
		return distance * reachMultiplier(scale);
	}

	public static double unscaleReachDistanceSqrForVanillaCheck(double distanceSqr, float scale) {
		double reachMultiplier = reachMultiplier(scale);
		return distanceSqr / (reachMultiplier * reachMultiplier);
	}

	public static double unscaleDistanceSqrForVanillaCheck(double distanceSqr, float scale) {
		if (isDefault(scale)) {
			return distanceSqr;
		}

		double safeScale = Math.max(MIN_SCALE, scale);
		return distanceSqr / (safeScale * safeScale);
	}

	private static double abilityMultiplier(float scale, double exponent) {
		float clampedScale = clamp(scale);
		return isDefault(clampedScale) ? DEFAULT_SCALE : Math.pow(clampedScale, exponent);
	}
}
