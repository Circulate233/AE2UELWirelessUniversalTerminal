package com.circulation.ae2wut.handler;

import com.circulation.ae2wut.AE2UELWirelessUniversalTerminal;
import com.circulation.ae2wut.client.TooltipButton;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.awt.Rectangle;
import java.util.List;

public class GuiHandler {
    public static final ResourceLocation wut$guiRl = new ResourceLocation(AE2UELWirelessUniversalTerminal.MOD_ID, "textures/gui/control.png");

    public static Byte2ObjectMap<TooltipButton> initGui(int guiTop, int guiLeft, List<GuiButton> buttonList, byte nowGui, ItemStack terminal) {
        final Byte2ObjectMap<TooltipButton> map = new Byte2ObjectOpenHashMap<>();
        final int left = guiLeft - 18;
        final int top = getTop(guiTop, guiLeft, buttonList);

        var modeSet = new IntArrayList(terminal.getTagCompound().getIntArray("modes"));
        modeSet.rem(nowGui);
        var modes = modeSet.toIntArray();
        for (byte i = 0; i < modes.length; i++) {
            final byte mode = (byte) modes[i];
            final var btn = new TooltipButton(left - ((i / 4) * 22) - 30, top + ((i % 4) * 22) + 20, mode);
            btn.visible = false;
            map.put(mode, btn);
            buttonList.add(btn);
        }

        final var swtich = new TooltipButton(left, top + 20, (byte) -1, nowGui);
        map.put((byte) -1, swtich);
        buttonList.add(swtich);

        return map;
    }

    public static int getTop(int guiTop, int guiLeft, List<GuiButton> buttonList) {
        int top = guiTop + 8;
        final int left = guiLeft - 18;
        for (GuiButton guiButton : buttonList) {
            if (guiButton.x != left) continue;
            if (top < guiButton.y) top = guiButton.y;
        }
        return top;
    }

    private static final int width = 72;
    private static final int height = 94;

    public static void drawGui(GuiScreen gui, int x, int y) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        gui.mc.getTextureManager().bindTexture(wut$guiRl);
        gui.drawTexturedModalRect(x, y - 6, 0, 0, width, height);
    }

    public static Rectangle getRectangle(int x, int y) {
        return new Rectangle(x, y, width, height);
    }
}