package com.circulation.ae2wut.mixin.ae2fc;

import appeng.api.storage.ITerminalHost;
import appeng.client.gui.implementations.GuiCraftConfirm;
import appeng.container.AEBaseContainer;
import appeng.helpers.WirelessTerminalGuiObject;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import com.glodblock.github.client.GuiFCCraftConfirm;
import com.glodblock.github.inventory.GuiType;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiFCCraftConfirm.class, remap = false)
public class MixinGuiFCCraftConfirm extends GuiCraftConfirm {
    @Shadow
    private GuiType originGui;

    public MixinGuiFCCraftConfirm(InventoryPlayer inventoryPlayer, ITerminalHost te) {
        super(inventoryPlayer, te);
    }

    @Inject(method = "initGui", at = @At(value = "TAIL"), remap = true)
    public void onInitGui(CallbackInfo ci) {
        Object te = ((AEBaseContainer) this.inventorySlots).getTarget();
        if (te instanceof WirelessTerminalGuiObject) {
            ItemStack tool = ((WirelessTerminalGuiObject) te).getItemStack();
            if (tool.getItem() instanceof ItemWirelessUniversalTerminal) {
                this.originGui = GuiType.WIRELESS_FLUID_PATTERN_TERMINAL;
            }
        }
    }
}