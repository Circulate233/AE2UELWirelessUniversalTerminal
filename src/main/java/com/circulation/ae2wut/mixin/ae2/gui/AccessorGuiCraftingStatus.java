package com.circulation.ae2wut.mixin.ae2.gui;

import appeng.client.gui.implementations.GuiCraftingStatus;
import appeng.core.sync.GuiBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GuiCraftingStatus.class, remap = false)
public interface AccessorGuiCraftingStatus {

    @Accessor
    GuiBridge getOriginalGui();
}
