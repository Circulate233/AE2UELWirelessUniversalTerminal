package com.circulation.ae2wut.mixin.ae2exttable;

import appeng.api.storage.ITerminalHost;
import appeng.client.gui.implementations.GuiCraftingCPU;
import appeng.client.gui.implementations.GuiCraftingStatus;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.helpers.WirelessTerminalGuiObject;
import com._0xc4de.ae2exttable.client.gui.AE2ExtendedGUIs;
import com.circulation.ae2wut.AE2UELWirelessUniversalTerminal;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiCraftingStatus.class, remap = false)
public class MixinCraftingCPUStatusTWO extends GuiCraftingCPU {

    @Shadow
    private ItemStack myIcon;

    @Shadow
    private GuiTabButton originalGuiBtn;

    @Unique
    private AE2ExtendedGUIs wut$extendedOriginalGui;

    @Unique
    private WirelessTerminalGuiObject wut$obj;

    public MixinCraftingCPUStatusTWO(InventoryPlayer inventoryPlayer, Object te) {
        super(inventoryPlayer, te);
    }

    @SuppressWarnings("InjectIntoConstructor")
    @Inject(method = "<init>(Lnet/minecraft/entity/player/InventoryPlayer;Lappeng/api/storage/ITerminalHost;)V", at = @At(value = "INVOKE", target = "Lappeng/api/definitions/IDefinitions;parts()Lappeng/api/definitions/IParts;", shift = At.Shift.AFTER))
    private void onInit(final InventoryPlayer inventoryPlayer, final ITerminalHost te, CallbackInfo ci) {
        if (te instanceof WirelessTerminalGuiObject wt) {
            ItemStack item = wt.getItemStack();
            if (item.getItem() instanceof ItemWirelessUniversalTerminal) {
                if (item.getTagCompound() != null) {
                    byte mode = item.getTagCompound().getByte("mode");
                    var gui = ItemWirelessUniversalTerminal.getGui(mode);
                    if (gui != null) {
                        this.wut$extendedOriginalGui = gui;
                        this.myIcon = item;
                        this.wut$obj = wt;
                    }
                }
            }
        }
    }

    @Inject(method = "actionPerformed", at = @At(value = "INVOKE", target = "Lappeng/client/gui/implementations/GuiCraftingCPU;actionPerformed(Lnet/minecraft/client/gui/GuiButton;)V", shift = At.Shift.AFTER), cancellable = true, remap = true)
    protected void actionPerformed(GuiButton btn, CallbackInfo ci) {
        if (btn == this.originalGuiBtn && this.wut$extendedOriginalGui != null) {
            AE2UELWirelessUniversalTerminal.openWirelessTerminalGui(this.wut$obj, ItemWirelessUniversalTerminal.getAE2EMode(this.wut$extendedOriginalGui));
            ci.cancel();
        }
    }

}