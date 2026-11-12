package com.portalgun;

import com.portalgun.entity.V1Entity;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

/**
 * Сюда добавим награду за V1, когда решим, что он даёт игроку.
 */
public final class V1Rewards {
	/**
	 * Вызывается один раз, когда V1 умирает.
	 *
	 * @param killedByVerity true, если V1 добила Верити (а не игрок)
	 */
	public static void onV1Defeated(ServerWorld world, V1Entity v1, DamageSource source, boolean killedByVerity) {
		for (ServerPlayerEntity player : world.getPlayers()) {
			// TODO: выдать награду игроку. Пока ничего.
		}
	}

	private V1Rewards() {
	}
}
