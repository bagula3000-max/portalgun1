package com.portalgun.entity;

import com.portalgun.ModEntities;
import com.portalgun.V1Rewards;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;

/**
 * V1 — босс арены. Нападает на игрока. Через {@link #FIGHT_TICKS_BEFORE_VERITY} тиков боя
 * или когда здоровье падает до половины, на арену приходит Верити.
 */
public class V1Entity extends HostileEntity {
	/** 60 секунд боя до появления Верити, если игрок не сбил V1 раньше. */
	private static final int FIGHT_TICKS_BEFORE_VERITY = 20 * 60;

	private final ServerBossBar bossBar = new ServerBossBar(
			Text.translatable("entity.portalgun.v1"),
			BossBar.Color.RED,
			BossBar.Style.PROGRESS
	);

	private int fightTicks = 0;
	private boolean verityCalled = false;
	private ServerWorld serverWorld;

	public V1Entity(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 50;
	}

	public static DefaultAttributeContainer.Builder createV1Attributes() {
		return HostileEntity.createHostileAttributes()
				.add(EntityAttributes.MAX_HEALTH, 100.0)
				.add(EntityAttributes.MOVEMENT_SPEED, 0.38)
				.add(EntityAttributes.ATTACK_DAMAGE, 6.0)
				.add(EntityAttributes.FOLLOW_RANGE, 48.0)
				.add(EntityAttributes.KNOCKBACK_RESISTANCE, 0.6);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new MeleeAttackGoal(this, 1.3, false));
		this.goalSelector.add(5, new WanderAroundGoal(this, 0.8));
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 16.0f));
		this.goalSelector.add(7, new LookAroundGoal(this));

		// RevengeGoal: если Верити ударит V1, он развернётся к ней.
		this.targetSelector.add(1, new RevengeGoal(this));
		this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
	}

	@Override
	protected void mobTick(ServerWorld world) {
		super.mobTick(world);
		this.serverWorld = world;
		this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());

		if (this.verityCalled) {
			return;
		}
		if (this.getTarget() instanceof PlayerEntity) {
			this.fightTicks++;
		}
		if (this.fightTicks >= FIGHT_TICKS_BEFORE_VERITY || this.getHealth() <= this.getMaxHealth() * 0.5f) {
			callVerity(world);
		}
	}

	private void callVerity(ServerWorld world) {
		this.verityCalled = true;

		VerityEntity verity = ModEntities.VERITY.create(world, SpawnReason.EVENT);
		if (verity == null) {
			return;
		}
		verity.setPosition(this.getX() + 8.0, this.getY(), this.getZ());
		verity.setPersistent();
		verity.setTarget(this);
		world.spawnEntity(verity);

		for (ServerPlayerEntity player : world.getPlayers()) {
			player.sendMessage(Text.translatable("message.portalgun.verity_arrives"), false);
		}
	}

	@Override
	public void onDeath(DamageSource source) {
		super.onDeath(source);
		if (this.serverWorld != null) {
			boolean killedByVerity = source.getAttacker() instanceof VerityEntity;
			V1Rewards.onV1Defeated(this.serverWorld, this, source, killedByVerity);
		}
	}

	@Override
	public void onStartedTrackingBy(ServerPlayerEntity player) {
		super.onStartedTrackingBy(player);
		this.bossBar.addPlayer(player);
	}

	@Override
	public void onStoppedTrackingBy(ServerPlayerEntity player) {
		super.onStoppedTrackingBy(player);
		this.bossBar.removePlayer(player);
	}

	@Override
	public boolean canImmediatelyDespawn(double distanceSquared) {
		return false;
	}
}
