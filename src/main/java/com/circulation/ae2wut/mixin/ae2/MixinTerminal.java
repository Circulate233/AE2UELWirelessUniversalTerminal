package com.circulation.ae2wut.mixin.ae2;

import appeng.core.sync.GuiBridge;
import appeng.items.tools.powered.Terminal;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Terminal.class,remap = false)
public class MixinTerminal {

    @Final
    @Shadow
    GuiBridge bridge;

    @Inject(method = "getBridge",at = @At("RETURN"), cancellable = true)
    public void getBridgeMixin(CallbackInfoReturnable<GuiBridge> cir) {
        if (this.bridge == GuiBridge.GUI_INTERFACE_TERMINAL){
            cir.setReturnValue(GuiBridge.GUI_WIRELESS_INTERFACE_TERMINAL);
        }
    }
}
