package com.schatzman.dsmadmincmds.spawner;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.BlockHitResult;

public final class SilkTouchSpawnerPlacement {
	private static final String BLOCK_ENTITY_TAG = "BlockEntityTag";
	private static final String MOB_SPAWNER_ID = "minecraft:mob_spawner";

	private SilkTouchSpawnerPlacement() {
	}

	public static void register() {
		UseBlockCallback.EVENT.register(SilkTouchSpawnerPlacement::placePreservedSpawner);
	}

	private static InteractionResult placePreservedSpawner(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
		ItemStack stack = player.getItemInHand(hand);
		if (!hasPreservedSpawnerData(stack)) {
			return InteractionResult.PASS;
		}

		if (player.getAbilities().instabuild) {
			return InteractionResult.PASS;
		}

		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		}

		if (!(level instanceof ServerLevel serverLevel)) {
			return InteractionResult.PASS;
		}

		BlockPlaceContext context = new BlockPlaceContext(player, hand, stack, hitResult);
		if (!context.canPlace()) {
			return InteractionResult.FAIL;
		}

		BlockPos placePos = context.getClickedPos();
		if (!player.mayUseItemAt(placePos, context.getClickedFace(), stack)) {
			return InteractionResult.FAIL;
		}

		BlockState state = Blocks.SPAWNER.getStateForPlacement(context);
		if (state == null || !state.canSurvive(serverLevel, placePos)) {
			return InteractionResult.FAIL;
		}

		if (!serverLevel.setBlock(placePos, state, 11)) {
			return InteractionResult.FAIL;
		}

		BlockEntity blockEntity = serverLevel.getBlockEntity(placePos);
		if (!(blockEntity instanceof SpawnerBlockEntity spawnerBlockEntity)) {
			serverLevel.removeBlock(placePos, false);
			return InteractionResult.FAIL;
		}

		CompoundTag spawnerTag = stack.getTagElement(BLOCK_ENTITY_TAG).copy();
		spawnerTag.putString("id", MOB_SPAWNER_ID);
		spawnerTag.putInt("x", placePos.getX());
		spawnerTag.putInt("y", placePos.getY());
		spawnerTag.putInt("z", placePos.getZ());
		spawnerBlockEntity.load(spawnerTag);
		spawnerBlockEntity.setChanged();
		serverLevel.sendBlockUpdated(placePos, state, state, 3);

		SoundType soundType = state.getSoundType();
		serverLevel.playSound(null, placePos, soundType.getPlaceSound(), SoundSource.BLOCKS,
				(soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
		stack.shrink(1);
		return InteractionResult.SUCCESS;
	}

	private static boolean hasPreservedSpawnerData(ItemStack stack) {
		if (!stack.is(Items.SPAWNER) || !stack.hasTag()) {
			return false;
		}

		CompoundTag tag = stack.getTag();
		if (tag == null || !tag.contains(BLOCK_ENTITY_TAG, Tag.TAG_COMPOUND)) {
			return false;
		}

		CompoundTag blockEntityTag = tag.getCompound(BLOCK_ENTITY_TAG);
		return blockEntityTag.contains("SpawnData", Tag.TAG_COMPOUND) || blockEntityTag.contains("SpawnPotentials", Tag.TAG_LIST);
	}
}
