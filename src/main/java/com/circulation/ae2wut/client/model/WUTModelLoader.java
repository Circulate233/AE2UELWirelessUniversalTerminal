package com.circulation.ae2wut.client.model;

import com.circulation.ae2wut.AE2UELWirelessUniversalTerminal;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

public class WUTModelLoader implements ICustomModelLoader {

    private final ItemWUTModel model = new ItemWUTModel();

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        return modelLocation.getNamespace().equals(AE2UELWirelessUniversalTerminal.MOD_ID)
                && !modelLocation.getPath().startsWith("models");
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) {
        return model;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }
}
