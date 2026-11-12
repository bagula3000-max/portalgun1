package com.portalgun;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Портальная пушка. ПКМ: телепорт на арену V1. ПКМ на арене: возврат обратно.
 */
public class PortalGunItem extends Item {
	private static final int COOLDOWN_TICKS = 60;

	public PortalGunItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		if (world instanceof ServerWorld serverWorld && user instanceof ServerPlayerEntity player) {
			ArenaManager.toggle(player, serverWorld);
			player.getItemCooldownManager().set(user.getStackInHand(hand), COOLDOWN_TICKS);
		}
		return ActionResult.SUCCESS;
	}
}
