package com.circulation.ae2wut.mixin.ae2fc;

import appeng.api.storage.ITerminalHost;
import appeng.client.gui.implementations.GuiCraftConfirm;
import appeng.container.AEBaseContainer;
import appeng.core.sync.GuiBridge;
import appeng.helpers.WirelessTerminalGuiObject;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import com.circulation.ae2wut.mixin.ae2.gui.AccessorGuiCraftConfirm;
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
public abstract class MixinGuiFCCraftConfirm extends GuiCraftConfirm implements AccessorGuiCraftConfirm {
    @Shadow
    private GuiType originGui;

    public MixinGuiFCCraftConfirm(InventoryPlayer inventoryPlayer, ITerminalHost te) {
        super(inventoryPlayer, te);
    }

    @Inject(method = "initGui", at = @At(value = "TAIL"), remap = true)
    public void onInitGui(CallbackInfo ci) {
        if (getOriginalGui() != null && getOriginalGui() != GuiBridge.GUI_WIRELESS_TERM) return;
        Object te = ((AEBaseContainer) this.inventorySlots).getTarget();
        if (te instanceof WirelessTerminalGuiObject) {
            ItemStack tool = ((WirelessTerminalGuiObject) te).getItemStack();
            if (tool.getItem() instanceof ItemWirelessUniversalTerminal) {
                this.originGui = GuiType.WIRELESS_FLUID_PATTERN_TERMINAL;
            }
        }
    }
}