package com.portalgun;

import com.portalgun.entity.V1Entity;
import com.portalgun.entity.VerityEntity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public final class ModEntities {
	public static final EntityType<V1Entity> V1 = register(
			"v1",
			EntityType.Builder.create(V1Entity::new, SpawnGroup.MONSTER).dimensions(0.6f, 1.95f).maxTrackingRange(10)
	);

	public static final EntityType<VerityEntity> VERITY = register(
			"verity",
			EntityType.Builder.create(VerityEntity::new, SpawnGroup.CREATURE).dimensions(0.6f, 1.95f).maxTrackingRange(10)
	);

	private static <T extends net.minecraft.entity.Entity> EntityType<T> register(String name, EntityType.Builder<T> builder) {
		RegistryKey<EntityType<?>> key = RegistryKey.of(RegistryKeys.ENTITY_TYPE, PortalGunMod.id(name));
		return Registry.register(Registries.ENTITY_TYPE, key, builder.build(key));
	}

	public static void register() {
		FabricDefaultAttributeRegistry.register(V1, V1Entity.createV1Attributes());
		FabricDefaultAttributeRegistry.register(VERITY, VerityEntity.createVerityAttributes());
	}

	private ModEntities() {
	}
}
