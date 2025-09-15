package com.circulation.ae2wut.client;

import appeng.client.gui.widgets.ITooltip;
import com.circulation.ae2wut.handler.GuiHandler;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

@SideOnly(Side.CLIENT)
public final class TooltipButton extends GuiButton implements ITooltip {
    private final byte t;
    private static final Byte2ObjectMap<ItemStack> iconMap = new Byte2ObjectOpenHashMap<>();
    private byte nowGui;

    public TooltipButton(int x, int y,byte t) {
        super(0, x, y, "");
        if (t < 0) {
            this.width = 18;
            this.height = 20;
        } else {
            this.width = 16;
            this.height = 16;
        }
        this.t = t;
    }

    public TooltipButton(int x, int y,byte t,byte nowGui) {
        this(x, y, t);
        if (t < 0) {
            this.nowGui = nowGui;
        }
    }

    public static void regIcon(byte id,ItemStack icon){
        iconMap.put(id,icon);
    }

    public void drawButton(@NotNull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            GlStateManager.pushMatrix();
            mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            if (t < 0) {
                mc.renderEngine.bindTexture(GuiHandler.wut$guiRl);
                this.drawTexturedModalRect(this.x, this.y, 240, 240, 16, 16);
                mc.getRenderItem().renderItemIntoGUI(iconMap.get(nowGui), this.x, this.y);
            } else {
                mc.getRenderItem().renderItemIntoGUI(iconMap.get(this.t), this.x, this.y);
            }

            this.mouseDragged(mc, mouseX, mouseY);
            GlStateManager.popMatrix();
        }

    }

    public String getMessage() {
        return this.t >= 0 ? iconMap.get(this.t).getDisplayName() : I18n.format("wut.btn.name");
    }

    public int xPos() {
        return this.x;
    }

    public int yPos() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean isVisible() {
        return this.visible;
    }
}