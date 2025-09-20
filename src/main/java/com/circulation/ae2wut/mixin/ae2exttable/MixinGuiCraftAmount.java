package com.circulation.ae2wut.mixin.ae2exttable;

import appeng.client.gui.AEBaseGui;
import appeng.client.gui.implementations.GuiCraftAmount;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.container.AEBaseContainer;
import appeng.helpers.WirelessTerminalGuiObject;
import com._0xc4de.ae2exttable.client.gui.AE2ExtendedGUIs;
import com._0xc4de.ae2exttable.items.ItemRegistry;
import com.circulation.ae2wut.AE2UELWirelessUniversalTerminal;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(value= GuiCraftAmount.class, remap = false)
public abstract class MixinGuiCraftAmount extends AEBaseGui {

    @Shadow
    private GuiTabButton originalGuiBtn;

    @Unique
    private AE2ExtendedGUIs wut$extendedOriginalGui;

    @Unique
    private WirelessTerminalGuiObject wut$obj;

    public MixinGuiCraftAmount(Container container) {
        super(container);
    }

    @Inject(method = "initGui", at = @At(value = "RETURN"), remap = true)
    private void onInitGui(CallbackInfo ci) {
        Object target = ((AEBaseContainer) this.inventorySlots).getTarget();
        if (target instanceof WirelessTerminalGuiObject term) {
            if (term.getItemStack().getItem() instanceof ItemWirelessUniversalTerminal item) {
                ItemStack itemstack = term.getItemStack();
                if (itemstack.getTagCompound() != null) {
                    switch (itemstack.getTagCompound().getInteger("mode")) {
                        case 6, 7, 8, 9: {
                            this.wut$extendedOriginalGui = ItemWirelessUniversalTerminal.getGuiType(term.getItemStack());
                            ItemStack myIcon = new ItemStack(ItemRegistry.partByGuiType(this.wut$extendedOriginalGui));
                            this.originalGuiBtn.visible = false;
                            this.buttonList.remove(this.originalGuiBtn);
                            this.buttonList.add((this.originalGuiBtn = new GuiTabButton(this.guiLeft + 154, this.guiTop, myIcon, myIcon.getDisplayName(), this.itemRender)));
                            this.wut$obj = term;
                        }
                    }
                }
            }
        }
        this.buttonList.removeIf(Objects::isNull);
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true, remap = true)
    protected void actionPerformedGuiSwitch(GuiButton btn, CallbackInfo ci) {
        if (btn == this.originalGuiBtn && this.wut$extendedOriginalGui != null) {
            AE2UELWirelessUniversalTerminal.openWirelessTerminalGui(this.wut$obj, ItemWirelessUniversalTerminal.getAE2EMode(this.wut$extendedOriginalGui));
            ci.cancel();
        }
    }
}