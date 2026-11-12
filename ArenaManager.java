package com.portalgun;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.portalgun.entity.V1Entity;

import net.minecraft.entity.SpawnReason;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.TeleportTarget;

/**
 * Телепорт на арену (измерение portalgun:v1_arena, описано в data/portalgun/dimension/v1_arena.json)
 * и обратно. Точки возврата хранятся в памяти: после перезапуска сервера игрока вернёт в спавн Верхнего мира.
 */
public final class ArenaManager {
	public static final RegistryKey<World> ARENA_KEY = RegistryKey.of(RegistryKeys.WORLD, PortalGunMod.id("v1_arena"));

	/** Верхняя поверхность платформы арены (см. слои в dimension json). */
	private static final double ARENA_SURFACE_Y = 4.0;

	private record ReturnPoint(RegistryKey<World> world, double x, double y, double z, float yaw, float pitch) {
	}

	private static final Map<UUID, ReturnPoint> RETURN_POINTS = new HashMap<>();

	public static void toggle(ServerPlayerEntity player, ServerWorld currentWorld) {
		if (currentWorld.getRegistryKey().equals(ARENA_KEY)) {
			leaveArena(player, currentWorld.getServer());
		} else {
			enterArena(player, currentWorld);
		}
	}

	private static void enterArena(ServerPlayerEntity player, ServerWorld currentWorld) {
		MinecraftServer server = currentWorld.getServer();
		ServerWorld arena = server.getWorld(ARENA_KEY);
		if (arena == null) {
			player.sendMessage(Text.translatable("message.portalgun.no_arena"), false);
			return;
		}

		RETURN_POINTS.put(player.getUuid(), new ReturnPoint(
				currentWorld.getRegistryKey(),
				player.getX(), player.getY(), player.getZ(),
				player.getYaw(), player.getPitch()
		));

		// Игрок смотрит на +Z, V1 появляется перед ним.
		TeleportTarget target = new TeleportTarget(
				arena,
				new Vec3d(0.5, ARENA_SURFACE_Y, 0.5),
				Vec3d.ZERO,
				0.0f,
				0.0f,
				entity -> spawnV1(arena)
		);
		player.teleportTo(target);
		player.sendMessage(Text.translatable("message.portalgun.enter"), true);
	}

	private static void leaveArena(ServerPlayerEntity player, MinecraftServer server) {
		ReturnPoint point = RETURN_POINTS.remove(player.getUuid());
		ServerWorld destination = point != null ? server.getWorld(point.world()) : null;
		if (destination == null) {
			destination = server.getOverworld();
			point = null;
		}

		Vec3d pos;
		float yaw = 0.0f;
		float pitch = 0.0f;
		if (point != null) {
			pos = new Vec3d(point.x(), point.y(), point.z());
			yaw = point.yaw();
			pitch = point.pitch();
		} else {
			int topY = destination.getTopY(Heightmap.Type.MOTION_BLOCKING, 0, 0);
			pos = new Vec3d(0.5, topY, 0.5);
		}

		player.teleportTo(new TeleportTarget(destination, pos, Vec3d.ZERO, yaw, pitch, TeleportTarget.NO_OP));
		player.sendMessage(Text.translatable("message.portalgun.leave"), true);
	}

	private static void spawnV1(ServerWorld arena) {
		// Один V1 на арену.
		if (!arena.getEntitiesByType(ModEntities.V1, entity -> entity.isAlive()).isEmpty()) {
			return;
		}
		V1Entity v1 = ModEntities.V1.create(arena, SpawnReason.EVENT);
		if (v1 == null) {
			return;
		}
		v1.setPosition(0.5, ARENA_SURFACE_Y, 10.5);
		v1.setYaw(180.0f);
		v1.setPersistent();
		arena.spawnEntity(v1);
	}

	private ArenaManager() {
	}
}
