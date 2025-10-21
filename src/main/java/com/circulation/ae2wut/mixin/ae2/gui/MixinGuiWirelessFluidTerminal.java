package com.circulation.ae2wut.mixin.ae2.gui;

import appeng.fluids.client.gui.GuiMEPortableFluidCell;
import appeng.fluids.client.gui.GuiWirelessFluidTerminal;
import appeng.fluids.container.ContainerWirelessFluidTerminal;
import appeng.helpers.WirelessTerminalGuiObject;
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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.List;

@Mixin(value = GuiWirelessFluidTerminal.class, remap = false)
public class MixinGuiWirelessFluidTerminal extends GuiMEPortableFluidCell {

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

    public MixinGuiWirelessFluidTerminal(InventoryPlayer inventoryPlayer, WirelessTerminalGuiObject te, ContainerWirelessFluidTerminal c) {
        super(inventoryPlayer, te, c);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void onInit(InventoryPlayer inventoryPlayer, WirelessTerminalGuiObject obj, CallbackInfo ci) {
        if ((this.wut$guiItem = obj.getItemStack()).getItem() == ItemWirelessUniversalTerminal.INSTANCE) {
            this.wut$isWut = true;
            this.wut$obj = obj;
            return;
        }
        this.wut$guiItem = ItemStack.EMPTY;
    }

    @Intrinsic
    @Override
    public void initGui() {
        super.initGui();
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

    @Inject(method = "getJEIExclusionArea", at = @At("RETURN"), remap = false)
    public void getJEIExclusionArea(CallbackInfoReturnable<List<Rectangle>> cir) {
        if (wut$enableSwitching)
            cir.getReturnValue().addAll(GuiHandler.getRectangle(wut$Map));
    }

    @Intrinsic
    @Override
    public void actionPerformed(GuiButton btn) throws IOException {
        if (wut$isWut) {
            if (btn == this.wut$t) {
                final boolean newValue = !this.wut$enableSwitching;
                for (TooltipButton value : wut$Map.values()) {
                    value.visible = newValue;
                }
                this.wut$enableSwitching = newValue;
                return;
            } else if (wut$Map != null) {
                for (Byte2ObjectMap.Entry<TooltipButton> entry : wut$Map.byte2ObjectEntrySet()) {
                    if (btn == entry.getValue()) {
                        AE2UELWirelessUniversalTerminal.openWirelessTerminalGui(wut$obj, entry.getByteKey());
                        return;
                    }
                }
            }
        }
        super.actionPerformed(btn);
    }
}