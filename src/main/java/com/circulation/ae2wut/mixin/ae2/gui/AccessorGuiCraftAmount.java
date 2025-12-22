package com.circulation.ae2wut.mixin.ae2.gui;


import appeng.client.gui.implementations.GuiCraftAmount;
import appeng.core.sync.GuiBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GuiCraftAmount.class, remap = false)
public interface AccessorGuiCraftAmount {

    @Accessor
    GuiBridge getOriginalGui();
}
