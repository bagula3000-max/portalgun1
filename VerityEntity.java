package com.portalgun.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.world.World;

/**
 * Верити. Приходит на арену, бежит к V1 и добивает его клинком. Неуязвима. Игроку не враждебна.
 * После смерти V1 уходит.
 */
public class VerityEntity extends PathAwareEntity {
	/** Сколько тиков Верити остаётся после смерти V1. */
	private static final int LEAVE_DELAY_TICKS = 60;

	private int idleTicks = 0;

	public VerityEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
		super(entityType, world);
		this.setInvulnerable(true);
		// Клинок в руке (рендерится стандартным слоем предмета в руке).
		this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.NETHERITE_SWORD));
	}

	public static DefaultAttributeContainer.Builder createVerityAttributes() {
		return MobEntity.createMobAttributes()
				.add(EntityAttributes.MAX_HEALTH, 200.0)
				.add(EntityAttributes.MOVEMENT_SPEED, 0.55)
				.add(EntityAttributes.ATTACK_DAMAGE, 30.0)
				.add(EntityAttributes.FOLLOW_RANGE, 64.0);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new MeleeAttackGoal(this, 1.6, true));
		this.goalSelector.add(5, new WanderAroundGoal(this, 0.8));
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
		this.goalSelector.add(7, new LookAroundGoal(this));
	}

	@Override
	protected void mobTick(ServerWorld world) {
		super.mobTick(world);

		LivingEntity target = this.getTarget();
		if (target == null || !target.isAlive()) {
			this.idleTicks++;
			if (this.idleTicks == 1) {
				for (ServerPlayerEntity player : world.getPlayers()) {
					player.sendMessage(Text.translatable("message.portalgun.verity_leaves"), false);
				}
			}
			if (this.idleTicks > LEAVE_DELAY_TICKS) {
				this.discard();
			}
		}
	}

	@Override
	public boolean canImmediatelyDespawn(double distanceSquared) {
		return false;
	}
}
