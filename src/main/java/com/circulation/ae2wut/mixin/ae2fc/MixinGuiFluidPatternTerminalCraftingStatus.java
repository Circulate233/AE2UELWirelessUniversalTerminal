package com.circulation.ae2wut.mixin.ae2fc;

import appeng.api.storage.ITerminalHost;
import appeng.client.gui.implementations.GuiCraftingStatus;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.helpers.WirelessTerminalGuiObject;
import com.circulation.ae2wut.AE2UELWirelessUniversalTerminal;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import com.glodblock.github.client.GuiFluidPatternTerminalCraftingStatus;
import com.glodblock.github.util.Ae2ReflectClient;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiFluidPatternTerminalCraftingStatus.class,remap = false)
public class MixinGuiFluidPatternTerminalCraftingStatus extends GuiCraftingStatus {

    @Mutable
    @Final
    @Shadow
    private ITerminalHost part;
    @Shadow
    private GuiTabButton originalGuiBtn;

    public MixinGuiFluidPatternTerminalCraftingStatus(InventoryPlayer inventoryPlayer, ITerminalHost te) {
        super(inventoryPlayer, te);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(InventoryPlayer inventoryPlayer, ITerminalHost te, CallbackInfo ci) {
        if (te instanceof WirelessTerminalGuiObject) {
            ItemStack tool = ((WirelessTerminalGuiObject) te).getItemStack();
            if (tool.getItem() instanceof ItemWirelessUniversalTerminal) {
                Ae2ReflectClient.setIconItem(this, tool);
            }
        }
    }

    @Inject(method = "actionPerformed", at = @At(value = "HEAD"), remap = true)
    public void onActionPerformed(GuiButton btn, CallbackInfo ci) {
        if (btn == this.originalGuiBtn) {
            if (this.part instanceof WirelessTerminalGuiObject t) {
                ItemStack tool = t.getItemStack();
                if (tool.getItem() instanceof ItemWirelessUniversalTerminal) {
                    AE2UELWirelessUniversalTerminal.openWirelessTerminalGui(t, (byte) 4);
                }
            }
        }
    }
}