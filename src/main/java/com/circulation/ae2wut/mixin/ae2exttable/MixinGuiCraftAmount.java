package com.circulation.ae2wut.mixin.ae2exttable;

import appeng.client.gui.AEBaseGui;
import appeng.client.gui.implementations.GuiCraftAmount;
import appeng.client.gui.widgets.GuiTabButton;
import appeng.container.AEBaseContainer;
import appeng.helpers.WirelessTerminalGuiObject;
import com._0xc4de.ae2exttable.client.gui.AE2ExtendedGUIs;
import com._0xc4de.ae2exttable.items.ItemRegistry;
import com._0xc4de.ae2exttable.network.ExtendedTerminalNetworkHandler;
import com._0xc4de.ae2exttable.network.packets.PacketSwitchGui;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value= GuiCraftAmount.class, remap=false)
public abstract class MixinGuiCraftAmount extends AEBaseGui {

    @Shadow
    private GuiTabButton originalGuiBtn;

    @Unique
    private AE2ExtendedGUIs ae2WirelessUniversalTerminal$extendedOriginalGui;

    public MixinGuiCraftAmount(Container container) {
        super(container);
    }

    @Inject(method = "initGui", at = @At(value = "RETURN"), remap = true)
    private void onInitGui(CallbackInfo ci) {
        Object target = ((AEBaseContainer) this.inventorySlots).getTarget();
        if (target instanceof WirelessTerminalGuiObject term)
            if (term.getItemStack().getItem() instanceof ItemWirelessUniversalTerminal item) {
                ItemStack itemstack = term.getItemStack();
                if (itemstack.getTagCompound() != null) {
                    switch (itemstack.getTagCompound().getInteger("mode")) {
                        case 6, 7, 8, 9: {
                            for (Object btn : new ObjectArrayList<>(this.buttonList)) {
                                if (btn instanceof GuiTabButton b) {
                                    this.buttonList.remove(b);
                                }
                            }
                            this.ae2WirelessUniversalTerminal$extendedOriginalGui = ItemWirelessUniversalTerminal.getGuiType(term.getItemStack());
                            ItemStack myIcon = new ItemStack(ItemRegistry.partByGuiType(this.ae2WirelessUniversalTerminal$extendedOriginalGui));
                            this.buttonList.add((this.originalGuiBtn = new GuiTabButton(this.guiLeft + 154, this.guiTop, myIcon, myIcon.getDisplayName(), this.itemRender)));
                        }
                    }
                }
            }
    }

    @Inject(method = "actionPerformed", at = @At(value = "INVOKE", target = "Lappeng/client/gui/AEBaseGui;actionPerformed(Lnet/minecraft/client/gui/GuiButton;)V", shift = At.Shift.AFTER), cancellable = true, remap = true)
    protected void actionPerformedGuiSwitch(GuiButton btn, CallbackInfo ci) {
        if (btn == this.originalGuiBtn && this.ae2WirelessUniversalTerminal$extendedOriginalGui != null) {
            ExtendedTerminalNetworkHandler.instance().sendToServer(new PacketSwitchGui(this.ae2WirelessUniversalTerminal$extendedOriginalGui));
            ci.cancel();
        }
    }
}