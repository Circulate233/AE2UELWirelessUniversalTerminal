package com.circulation.ae2wut.mixin.ae2exttable;

import appeng.api.features.IWirelessTermHandler;
import appeng.helpers.WirelessTerminalGuiObject;
import com._0xc4de.ae2exttable.client.gui.AE2ExtendedGUIs;
import com._0xc4de.ae2exttable.client.gui.WirelessTerminalGuiObjectTwo;
import com._0xc4de.ae2exttable.interfaces.ICraftingClass;
import com._0xc4de.ae2exttable.items.ItemRegistry;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = WirelessTerminalGuiObjectTwo.class, remap = false)
public abstract class MixinWirelessTerminalGuiObjectTwo extends WirelessTerminalGuiObject implements ICraftingClass {

    @Shadow
    public AE2ExtendedGUIs gui;

    @Shadow
    protected ItemStack effectiveItem;

    private MixinWirelessTerminalGuiObjectTwo(IWirelessTermHandler wh, ItemStack is, EntityPlayer ep, World w, int x, int y, int z) {
        super(wh, is, ep, w, x, y, z);
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/_0xc4de/ae2exttable/items/ItemRegistry;guiByItem(Lnet/minecraft/item/Item;)Lcom/_0xc4de/ae2exttable/client/gui/AE2ExtendedGUIs;"))
    private AE2ExtendedGUIs RedirectGuiByItem(Item item) {
        if (effectiveItem.getItem() instanceof ItemWirelessUniversalTerminal) {
            return ItemWirelessUniversalTerminal.getGuiType(effectiveItem);
        }
        return ItemRegistry.guiByItem(effectiveItem.getItem());
    }

}