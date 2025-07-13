package com.circulation.ae2wut.mixin.ae2exttable;

import appeng.api.storage.ITerminalHost;
import appeng.client.gui.AEBaseGui;
import appeng.client.gui.implementations.GuiCraftConfirm;
import appeng.core.localization.GuiText;
import appeng.helpers.WirelessTerminalGuiObject;
import com._0xc4de.ae2exttable.client.gui.AE2ExtendedGUIs;
import com._0xc4de.ae2exttable.network.ExtendedTerminalNetworkHandler;
import com._0xc4de.ae2exttable.network.packets.PacketSwitchGui;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiCraftConfirm.class, remap = false)
public abstract class MixinGuiCraftConfirm extends AEBaseGui {

    @Shadow
    private GuiButton cancel;

    @Unique
    private AE2ExtendedGUIs ae2WirelessUniversalTerminal$extendedOriginalGui;

    public MixinGuiCraftConfirm(Container container) {
        super(container);
    }

    @SuppressWarnings("InjectIntoConstructor")
    @Inject(method="<init>(Lnet/minecraft/entity/player/InventoryPlayer;Lappeng/api/storage/ITerminalHost;)V",
            at= @At(value = "INVOKE", target = "Lappeng/container/implementations/ContainerCraftConfirm;setGui(Lappeng/client/gui/implementations/GuiCraftConfirm;)V", shift = At.Shift.AFTER))
    private void onInit(final InventoryPlayer inventoryPlayer, final ITerminalHost te, CallbackInfo ci) {
        if (te instanceof WirelessTerminalGuiObject term) {
            if (term.getItemStack().getItem() instanceof ItemWirelessUniversalTerminal t) {
                this.ae2WirelessUniversalTerminal$extendedOriginalGui = ItemWirelessUniversalTerminal.getGuiType(term.getItemStack());
            }
        }
    }

    @Inject(method="initGui", at=@At(value="RETURN"), remap=true)
    private void onInitGui(CallbackInfo ci) {
        if (this.ae2WirelessUniversalTerminal$extendedOriginalGui != null) {
            this.buttonList.remove(null);
            this.cancel = new GuiButton(0, this.guiLeft + 6, this.guiTop + this.ySize - 25, 50, 20, GuiText.Cancel.getLocal());
            this.buttonList.add(this.cancel);
        }
    }

    @Inject(method="actionPerformed", at = @At(value="INVOKE", target="Lappeng/client/gui/AEBaseGui;actionPerformed(Lnet/minecraft/client/gui/GuiButton;)V", shift = At.Shift.AFTER), cancellable = true, remap=true)
    protected void actionPerformed(GuiButton btn, CallbackInfo ci) {
        if (this.ae2WirelessUniversalTerminal$extendedOriginalGui != null) {
            if (btn == this.cancel) {
                ExtendedTerminalNetworkHandler.instance().sendToServer(new PacketSwitchGui(this.ae2WirelessUniversalTerminal$extendedOriginalGui));
                ci.cancel();
            }
        }
    }
}
