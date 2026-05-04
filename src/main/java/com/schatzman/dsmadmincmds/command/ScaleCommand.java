package com.schatzman.dsmadmincmds.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.schatzman.dsmadmincmds.scale.EntityScaleAccess;
import com.schatzman.dsmadmincmds.scale.ScaleValues;
import java.util.Collection;
import java.util.function.UnaryOperator;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class ScaleCommand {
	private static final float DEFAULT_LARGER_FACTOR = 1.25F;
	private static final float DEFAULT_SMALLER_FACTOR = 0.8F;
	private static final SimpleCommandExceptionType NO_LIVING_TARGETS = new SimpleCommandExceptionType(
			Component.literal("No selected targets are living entities."));

	private ScaleCommand() {
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("dsm-scale")
				.requires(AdminPermissions::canUseCheatCommand)
				.then(Commands.literal("larger")
						.then(Commands.argument("targets", EntityArgument.entities())
								.executes(context -> multiply(context, DEFAULT_LARGER_FACTOR))
								.then(Commands.argument("factor", FloatArgumentType.floatArg(0.01F, 100.0F))
										.executes(context -> multiply(context, FloatArgumentType.getFloat(context, "factor"))))))
				.then(Commands.literal("smaller")
						.then(Commands.argument("targets", EntityArgument.entities())
								.executes(context -> multiply(context, DEFAULT_SMALLER_FACTOR))
								.then(Commands.argument("factor", FloatArgumentType.floatArg(0.01F, 100.0F))
										.executes(context -> multiply(context, FloatArgumentType.getFloat(context, "factor"))))))
				.then(Commands.literal("reset")
						.then(Commands.argument("targets", EntityArgument.entities())
								.executes(context -> set(context, ScaleValues.DEFAULT_SCALE))))
				.then(Commands.literal("set")
						.then(Commands.argument("targets", EntityArgument.entities())
								.then(Commands.argument("scale", FloatArgumentType.floatArg(ScaleValues.MIN_SCALE, ScaleValues.MAX_SCALE))
										.executes(context -> set(context, FloatArgumentType.getFloat(context, "scale")))))));
	}

	private static int multiply(CommandContext<CommandSourceStack> context, float factor) throws CommandSyntaxException {
		return apply(context, currentScale -> currentScale * factor);
	}

	private static int set(CommandContext<CommandSourceStack> context, float scale) throws CommandSyntaxException {
		return apply(context, ignored -> scale);
	}

	private static int apply(CommandContext<CommandSourceStack> context, UnaryOperator<Float> scaleOperation)
			throws CommandSyntaxException {
		Collection<? extends Entity> selectedEntities = EntityArgument.getEntities(context, "targets");
		int changedCount = 0;
		float minScale = Float.MAX_VALUE;
		float maxScale = Float.MIN_VALUE;

		for (Entity entity : selectedEntities) {
			if (!(entity instanceof LivingEntity livingEntity)) {
				continue;
			}

			EntityScaleAccess scaleAccess = (EntityScaleAccess) livingEntity;
			float nextScale = ScaleValues.clamp(scaleOperation.apply(scaleAccess.dsm$getScale()));
			scaleAccess.dsm$setScale(nextScale);
			minScale = Math.min(minScale, nextScale);
			maxScale = Math.max(maxScale, nextScale);
			changedCount++;
		}

		if (changedCount == 0) {
			throw NO_LIVING_TARGETS.create();
		}

		int finalChangedCount = changedCount;
		float finalMinScale = minScale;
		float finalMaxScale = maxScale;
		context.getSource().sendSuccess(() -> Component.literal(formatFeedback(finalChangedCount, finalMinScale, finalMaxScale)), true);
		return changedCount;
	}

	private static String formatFeedback(int changedCount, float minScale, float maxScale) {
		if (Math.abs(minScale - maxScale) < ScaleValues.EPSILON) {
			return String.format("Scaled %d living target(s) to %.2fx.", changedCount, minScale);
		}

		return String.format("Scaled %d living target(s) to %.2fx-%.2fx.", changedCount, minScale, maxScale);
	}
}
