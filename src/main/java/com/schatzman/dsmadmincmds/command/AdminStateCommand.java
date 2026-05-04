package com.schatzman.dsmadmincmds.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.schatzman.dsmadmincmds.scale.EntityScaleAccess;
import com.schatzman.dsmadmincmds.scale.ScaleValues;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class AdminStateCommand {
	private static final float DEFAULT_LARGER_FACTOR = 1.25F;
	private static final float DEFAULT_SMALLER_FACTOR = 0.8F;
	private static final SimpleCommandExceptionType NO_LIVING_TARGETS = new SimpleCommandExceptionType(
			Component.literal("No selected targets are living entities."));

	private AdminStateCommand() {
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		registerMultiplier(dispatcher, "dsm-reach", "reach", EntityScaleAccess::dsm$getEffectiveReachMultiplier, EntityScaleAccess::dsm$setReachMultiplier);
		registerMultiplier(dispatcher, "dsm-speed", "speed", EntityScaleAccess::dsm$getEffectiveSpeedMultiplier, EntityScaleAccess::dsm$setSpeedMultiplier);
		registerMultiplier(dispatcher, "dsm-jump", "jump", EntityScaleAccess::dsm$getEffectiveJumpMultiplier, EntityScaleAccess::dsm$setJumpMultiplier);
		registerToggle(dispatcher, "dsm-collision", "entity collision", EntityScaleAccess::dsm$setEntityCollisionEnabled);
		registerToggle(dispatcher, "dsm-invulnerable", "admin invulnerability", EntityScaleAccess::dsm$setInvulnerableAdminEnabled);
	}

	private static void registerMultiplier(CommandDispatcher<CommandSourceStack> dispatcher, String root, String label,
			MultiplierGetter getter, MultiplierSetter setter) {
		dispatcher.register(Commands.literal(root)
				.requires(AdminPermissions::canUseCheatCommand)
				.then(Commands.literal("larger")
						.then(Commands.argument("targets", EntityArgument.entities())
								.executes(context -> multiply(context, label, getter, setter, DEFAULT_LARGER_FACTOR))
								.then(Commands.argument("factor", FloatArgumentType.floatArg(ScaleValues.MIN_MULTIPLIER, ScaleValues.MAX_MULTIPLIER))
										.executes(context -> multiply(context, label, getter, setter, FloatArgumentType.getFloat(context, "factor"))))))
				.then(Commands.literal("smaller")
						.then(Commands.argument("targets", EntityArgument.entities())
								.executes(context -> multiply(context, label, getter, setter, DEFAULT_SMALLER_FACTOR))
								.then(Commands.argument("factor", FloatArgumentType.floatArg(ScaleValues.MIN_MULTIPLIER, ScaleValues.MAX_MULTIPLIER))
										.executes(context -> multiply(context, label, getter, setter, FloatArgumentType.getFloat(context, "factor"))))))
				.then(Commands.literal("reset")
						.then(Commands.argument("targets", EntityArgument.entities())
								.executes(context -> reset(context, label, getter, setter))))
				.then(Commands.literal("set")
						.then(Commands.argument("targets", EntityArgument.entities())
								.then(Commands.argument("multiplier", FloatArgumentType.floatArg(ScaleValues.MIN_MULTIPLIER, ScaleValues.MAX_MULTIPLIER))
										.executes(context -> set(context, label, getter, setter, FloatArgumentType.getFloat(context, "multiplier")))))));
	}

	private static void registerToggle(CommandDispatcher<CommandSourceStack> dispatcher, String root, String label, BooleanSetter setter) {
		dispatcher.register(Commands.literal(root)
				.requires(AdminPermissions::canUseCheatCommand)
				.then(Commands.literal("on")
						.then(Commands.argument("targets", EntityArgument.entities())
								.executes(context -> setToggle(context, label, setter, true))))
				.then(Commands.literal("off")
						.then(Commands.argument("targets", EntityArgument.entities())
								.executes(context -> setToggle(context, label, setter, false)))));
	}

	private static int multiply(CommandContext<CommandSourceStack> context, String label, MultiplierGetter getter,
			MultiplierSetter setter, float factor) throws CommandSyntaxException {
		return applyMultiplier(context, label, getter, access -> getter.get(access) * factor, setter);
	}

	private static int set(CommandContext<CommandSourceStack> context, String label, MultiplierGetter getter,
			MultiplierSetter setter, float multiplier) throws CommandSyntaxException {
		return applyMultiplier(context, label, getter, access -> multiplier, setter);
	}

	private static int reset(CommandContext<CommandSourceStack> context, String label, MultiplierGetter getter,
			MultiplierSetter setter) throws CommandSyntaxException {
		return applyMultiplier(context, label, getter, access -> ScaleValues.UNSET_EXPLICIT_MULTIPLIER, setter);
	}

	private static int applyMultiplier(CommandContext<CommandSourceStack> context, String label, MultiplierGetter getter,
			MultiplierOperation operation, MultiplierSetter setter) throws CommandSyntaxException {
		Collection<? extends Entity> selectedEntities = EntityArgument.getEntities(context, "targets");
		int changedCount = 0;
		float minMultiplier = Float.MAX_VALUE;
		float maxMultiplier = Float.MIN_VALUE;

		for (Entity entity : selectedEntities) {
			if (!(entity instanceof LivingEntity livingEntity)) {
				continue;
			}

			EntityScaleAccess access = (EntityScaleAccess) livingEntity;
			float nextMultiplier = operation.apply(access);
			setter.set(access, nextMultiplier);
			float reportedMultiplier = getter.get(access);
			minMultiplier = Math.min(minMultiplier, reportedMultiplier);
			maxMultiplier = Math.max(maxMultiplier, reportedMultiplier);
			changedCount++;
		}

		if (changedCount == 0) {
			throw NO_LIVING_TARGETS.create();
		}

		int finalChangedCount = changedCount;
		float finalMinMultiplier = minMultiplier;
		float finalMaxMultiplier = maxMultiplier;
		context.getSource().sendSuccess(() -> Component.literal(formatMultiplierFeedback(label, finalChangedCount, finalMinMultiplier, finalMaxMultiplier)), true);
		return changedCount;
	}

	private static int setToggle(CommandContext<CommandSourceStack> context, String label, BooleanSetter setter, boolean enabled)
			throws CommandSyntaxException {
		Collection<? extends Entity> selectedEntities = EntityArgument.getEntities(context, "targets");
		for (Entity entity : selectedEntities) {
			setter.set((EntityScaleAccess) entity, enabled);
		}

		int changedCount = selectedEntities.size();
		context.getSource().sendSuccess(() -> Component.literal(String.format("%s %s for %d target(s).",
				enabled ? "Enabled" : "Disabled", label, changedCount)), true);
		return changedCount;
	}

	private static String formatMultiplierFeedback(String label, int changedCount, float minMultiplier, float maxMultiplier) {
		if (Math.abs(minMultiplier - maxMultiplier) < ScaleValues.EPSILON) {
			return String.format("Set %s multiplier for %d living target(s) to %.2fx.", label, changedCount, minMultiplier);
		}

		return String.format("Set %s multiplier for %d living target(s) to %.2fx-%.2fx.", label, changedCount, minMultiplier, maxMultiplier);
	}

	@FunctionalInterface
	private interface MultiplierGetter {
		float get(EntityScaleAccess access);
	}

	@FunctionalInterface
	private interface MultiplierSetter {
		void set(EntityScaleAccess access, float multiplier);
	}

	@FunctionalInterface
	private interface MultiplierOperation {
		float apply(EntityScaleAccess access);
	}

	@FunctionalInterface
	private interface BooleanSetter {
		void set(EntityScaleAccess access, boolean enabled);
	}
}
