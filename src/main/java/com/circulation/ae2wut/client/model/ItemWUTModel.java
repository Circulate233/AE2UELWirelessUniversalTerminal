package com.circulation.ae2wut.client.model;

import com.circulation.ae2wut.AE2UELWirelessUniversalTerminal;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;

import java.util.Collection;
import java.util.function.Function;

class ItemWUTModel implements IModel {
    private static final ResourceLocation BASE_MODEL = new ResourceLocation(AE2UELWirelessUniversalTerminal.MOD_ID, "item/wireless_universal_terminal");

    public Collection<ResourceLocation> getDependencies() {
        return ImmutableList.of(BASE_MODEL);
    }

    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        try {
            return new ItemWUTBakedModel(ModelLoaderRegistry.getModel(BASE_MODEL).bake(state, format, bakedTextureGetter));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}