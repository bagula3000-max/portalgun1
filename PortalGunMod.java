package com.portalgun;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

public class PortalGunMod implements ModInitializer {
	public static final String MOD_ID = "portalgun";

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		ModEntities.register();
		ModItems.register();
	}
}
