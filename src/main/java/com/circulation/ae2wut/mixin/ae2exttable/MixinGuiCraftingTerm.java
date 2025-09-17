package com.circulation.ae2wut.mixin.ae2exttable;

import appeng.api.storage.ITerminalHost;
import appeng.container.implementations.ContainerMEMonitorable;
import appeng.helpers.WirelessTerminalGuiObject;
import com._0xc4de.ae2exttable.client.gui.ExtendedCraftingGUIConstants;
import com._0xc4de.ae2exttable.client.gui.GuiCraftingTerm;
import com._0xc4de.ae2exttable.client.gui.GuiMEMonitorableTwo;
import com.circulation.ae2wut.AE2UELWirelessUniversalTerminal;
import com.circulation.ae2wut.client.TooltipButton;
import com.circulation.ae2wut.handler.GuiHandler;
import com.circulation.ae2wut.handler.WutRegisterHandler;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.Rectangle;
import java.util.List;

@Mixin(GuiCraftingTerm.class)
public class MixinGuiCraftingTerm extends GuiMEMonitorableTwo {

    @Unique
    private TooltipButton wut$t;
    @Unique
    private Byte2ObjectMap<TooltipButton> wut$Map;
    @Unique
    private boolean wut$isWut = false;
    @Unique
    private boolean wut$enableSwitching;
    @Unique
    private ItemStack wut$guiItem;
    @Unique
    private WirelessTerminalGuiObject wut$obj;

    public MixinGuiCraftingTerm(InventoryPlayer inventoryPlayer, ITerminalHost te) {
        super(inventoryPlayer, te);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(InventoryPlayer inventoryPlayer, ITerminalHost te, ContainerMEMonitorable c, ExtendedCraftingGUIConstants guiConst, CallbackInfo ci) {
        if (te instanceof WirelessTerminalGuiObject obj && (this.wut$guiItem = obj.getItemStack()).getItem() == ItemWirelessUniversalTerminal.INSTANCE) {
            this.wut$isWut = true;
            this.wut$obj = obj;
            return;
        }
        this.wut$guiItem = ItemStack.EMPTY;
    }

    @Inject(method = "initGui", at = @At("TAIL"))
    public void initGui(CallbackInfo ci) {
        if (wut$isWut) {
            this.wut$Map = GuiHandler.initGui(
                    this.guiTop, this.guiLeft, this.buttonList,
                    WutRegisterHandler.Client.getGuiType(this.getClass()),
                    this.wut$guiItem
            );
            this.wut$enableSwitching = false;
            this.wut$t = this.wut$Map.get((byte) -1);
            this.wut$Map.remove((byte) -1);
        }
    }

    @Intrinsic
    public List<Rectangle> getJEIExclusionArea() {
        var out = super.getJEIExclusionArea();
        if (wut$enableSwitching)
            out.addAll(GuiHandler.getRectangle(wut$Map));
        return out;
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"), cancellable = true)
    public void swtichDisplay(GuiButton btn, CallbackInfo ci) {
        if (!wut$isWut) return;
        if (btn == this.wut$t) {
            final boolean newValue = !this.wut$enableSwitching;
            for (TooltipButton value : wut$Map.values()) {
                value.visible = newValue;
            }
            this.wut$enableSwitching = newValue;
            ci.cancel();
        } else if (wut$Map != null) {
            for (Byte2ObjectMap.Entry<TooltipButton> entry : wut$Map.byte2ObjectEntrySet()) {
                if (btn == entry.getValue()) {
                    AE2UELWirelessUniversalTerminal.openWirelessTerminalGui(wut$obj, entry.getByteKey());
                    ci.cancel();
                    return;
                }
            }
        }
    }
}