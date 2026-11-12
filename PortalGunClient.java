package com.portalgun.client;

import com.portalgun.ModEntities;
import com.portalgun.PortalGunMod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class PortalGunClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(ModEntities.V1,
				context -> new ArenaBipedRenderer<>(context, PortalGunMod.id("textures/entity/v1.png")));
		EntityRendererRegistry.register(ModEntities.VERITY,
				context -> new ArenaBipedRenderer<>(context, PortalGunMod.id("textures/entity/verity.png")));
	}
}
