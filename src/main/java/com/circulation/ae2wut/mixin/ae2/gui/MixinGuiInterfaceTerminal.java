package com.circulation.ae2wut.mixin.ae2.gui;

import appeng.client.gui.AEBaseGui;
import appeng.client.gui.implementations.GuiInterfaceTerminal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiInterfaceTerminal.class,remap = false)
public abstract class MixinGuiInterfaceTerminal extends AEBaseGui {

    public MixinGuiInterfaceTerminal() {
        super(null);
    }

    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z",ordinal = 3))
    public void add(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        wut$addB(mouseX, mouseY, partialTicks);
    }

    @Unique
    public void wut$addB(int mouseX, int mouseY, float partialTicks) {

    }
}
