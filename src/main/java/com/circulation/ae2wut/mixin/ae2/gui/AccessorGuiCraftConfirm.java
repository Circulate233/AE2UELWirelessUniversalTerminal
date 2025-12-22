package com.circulation.ae2wut.mixin.ae2.gui;

import appeng.client.gui.implementations.GuiCraftConfirm;
import appeng.core.sync.GuiBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GuiCraftConfirm.class, remap = false)
public interface AccessorGuiCraftConfirm {

    @Accessor
    GuiBridge getOriginalGui();
}
