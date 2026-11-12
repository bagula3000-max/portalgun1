package com.portalgun.client;

import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

/** Простой гуманоидный рендерер (модель игрока 64x64) с отдельной текстурой на каждого моба. */
public class ArenaBipedRenderer<T extends MobEntity>
		extends BipedEntityRenderer<T, BipedEntityRenderState, BipedEntityModel<BipedEntityRenderState>> {
	private final Identifier texture;

	public ArenaBipedRenderer(EntityRendererFactory.Context context, Identifier texture) {
		super(context, new BipedEntityModel<>(context.getPart(EntityModelLayers.PLAYER)), 0.5f);
		this.texture = texture;
	}

	@Override
	public BipedEntityRenderState createRenderState() {
		return new BipedEntityRenderState();
	}

	@Override
	public Identifier getTexture(BipedEntityRenderState state) {
		return this.texture;
	}
}
