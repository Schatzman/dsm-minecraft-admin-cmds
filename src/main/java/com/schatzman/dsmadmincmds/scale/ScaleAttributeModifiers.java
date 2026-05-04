package com.schatzman.dsmadmincmds.scale;

import java.util.UUID;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;

public final class ScaleAttributeModifiers {
	private static final UUID MOVEMENT_SPEED_MODIFIER_ID = UUID.fromString("76541f5a-c706-42f6-8ff5-36bb54ae50b1");
	private static final UUID FLYING_SPEED_MODIFIER_ID = UUID.fromString("e6f52e94-85bb-4285-a8f1-d3eeff89fd39");
	private static final String MOVEMENT_SPEED_MODIFIER_NAME = "DSM scale movement speed";
	private static final String FLYING_SPEED_MODIFIER_NAME = "DSM scale flying speed";

	private ScaleAttributeModifiers() {
	}

	public static void apply(EntityScaleAccess scaleAccess, Object entity) {
		if (!(entity instanceof LivingEntity livingEntity)) {
			return;
		}

		float scale = ScaleValues.clamp(scaleAccess.dsm$getScale());
		applyAttribute(livingEntity, Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED_MODIFIER_ID, MOVEMENT_SPEED_MODIFIER_NAME, scale);
		applyAttribute(livingEntity, Attributes.FLYING_SPEED, FLYING_SPEED_MODIFIER_ID, FLYING_SPEED_MODIFIER_NAME, scale);
	}

	private static void applyAttribute(LivingEntity livingEntity, Attribute attribute, UUID modifierId, String modifierName, float scale) {
		AttributeInstance attributeInstance = livingEntity.getAttribute(attribute);
		if (attributeInstance == null) {
			return;
		}

		attributeInstance.removeModifier(modifierId);
		if (!ScaleValues.isDefault(scale)) {
			attributeInstance.addTransientModifier(new AttributeModifier(modifierId, modifierName, scale - 1.0D, Operation.MULTIPLY_TOTAL));
		}
	}
}
