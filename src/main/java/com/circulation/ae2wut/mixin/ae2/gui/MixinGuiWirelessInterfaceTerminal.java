package com.circulation.ae2wut.mixin.ae2.gui;

import appeng.client.gui.implementations.GuiInterfaceTerminal;
import appeng.client.gui.implementations.GuiWirelessInterfaceTerminal;
import appeng.helpers.WirelessTerminalGuiObject;
import appeng.parts.reporting.PartInterfaceTerminal;
import com.circulation.ae2wut.AE2UELWirelessUniversalTerminal;
import com.circulation.ae2wut.client.TooltipButton;
import com.circulation.ae2wut.handler.GuiHandler;
import com.circulation.ae2wut.handler.WutRegisterHandler;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
import java.io.IOException;
import java.util.List;

@Mixin(GuiWirelessInterfaceTerminal.class)
public class MixinGuiWirelessInterfaceTerminal extends GuiInterfaceTerminal {

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

    public MixinGuiWirelessInterfaceTerminal(InventoryPlayer inventoryPlayer, PartInterfaceTerminal te) {
        super(inventoryPlayer, te);
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
    public List<Rectangle> getJEIExclusionArea() {
        var out = super.getJEIExclusionArea();
        if (wut$enableSwitching)
            out.addAll(GuiHandler.getRectangle(wut$Map));
        return out;
    }

    @Intrinsic
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (wut$isWut) {
            if (wut$t == null) {
                this.wut$Map = GuiHandler.initGui(
                        this.guiTop, this.guiLeft, this.buttonList,
                        WutRegisterHandler.Client.getGuiType(this.getClass()),
                        this.wut$guiItem
                );
                this.wut$enableSwitching = false;
                for (TooltipButton value : this.wut$Map.values()) {
                    value.drawButton(this.mc, mouseX, mouseY, partialTicks);
                    this.drawTooltip(value, mouseX, mouseY);
                }
                this.wut$t = this.wut$Map.get((byte) -1);
                this.wut$Map.remove((byte) -1);
                this.buttonList = new ObjectArrayList<>(this.buttonList);
            } else {
                this.buttonList.add(this.wut$t);
                this.wut$t.drawButton(this.mc, mouseX, mouseY, partialTicks);
                this.drawTooltip(this.wut$t, mouseX, mouseY);
                for (TooltipButton value : wut$Map.values()) {
                    this.buttonList.add(value);
                    value.drawButton(this.mc, mouseX, mouseY, partialTicks);
                    this.drawTooltip(value, mouseX, mouseY);
                }
            }
        }
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