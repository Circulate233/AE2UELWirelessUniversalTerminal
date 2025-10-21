package com.circulation.ae2wut.mixin.ae2fc;

import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import com.glodblock.github.coremod.CoreModHooks;
import com.glodblock.github.loader.FCItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = CoreModHooks.class, remap = false)
public class MixinCoreModHooks {

    @Redirect(method = "startJob", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", remap = true))
    private static Item getItemR(ItemStack instance) {
        var item = instance.getItem();
        if (item instanceof ItemWirelessUniversalTerminal wut && wut.hasMode(instance, (byte) 4)) {
            return FCItems.WIRELESS_FLUID_PATTERN_TERMINAL;
        }
        return item;
    }
}