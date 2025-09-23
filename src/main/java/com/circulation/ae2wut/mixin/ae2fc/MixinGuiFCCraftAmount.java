package com.circulation.ae2wut.mixin.ae2fc;

import appeng.api.storage.ITerminalHost;
import appeng.client.gui.implementations.GuiCraftAmount;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.container.AEBaseContainer;
import appeng.helpers.WirelessTerminalGuiObject;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import com.glodblock.github.client.GuiFCCraftAmount;
import com.glodblock.github.inventory.GuiType;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(value = GuiFCCraftAmount.class,remap = false)
public class MixinGuiFCCraftAmount extends GuiCraftAmount {

    @Shadow
    private GuiType originGui;
    @Shadow
    private GuiTabButton originalGuiBtn;

    public MixinGuiFCCraftAmount(InventoryPlayer inventoryPlayer, ITerminalHost te) {
        super(inventoryPlayer, te);
    }

    @Inject(method = "initGui", at = @At(value = "TAIL"), cancellable = true, remap = true)
    public void onInitGui(CallbackInfo ci) {
        Object te = ((AEBaseContainer) this.inventorySlots).getTarget();
        ItemStack icon = ItemStack.EMPTY;
        if (te instanceof WirelessTerminalGuiObject) {
            ItemStack tool = ((WirelessTerminalGuiObject) te).getItemStack();
            if (tool.getItem() instanceof ItemWirelessUniversalTerminal) {
                icon = tool;
                this.originGui = GuiType.WIRELESS_FLUID_PATTERN_TERMINAL;
            }
            if (!icon.isEmpty() && this.originGui != null) {
                if (this.originalGuiBtn != null) {
                    this.originalGuiBtn.visible = false;
                    this.buttonList.remove(this.originalGuiBtn);
                }
                this.buttonList.add(this.originalGuiBtn = new GuiTabButton(this.guiLeft + 154, this.guiTop, icon, icon.getDisplayName(), this.itemRender));
                ci.cancel();
            }
        }
        this.buttonList.removeIf(Objects::isNull);
    }

}