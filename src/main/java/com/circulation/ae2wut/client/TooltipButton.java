package com.circulation.ae2wut.client;

import appeng.client.gui.widgets.ITooltip;
import com.circulation.ae2wut.client.model.ItemWUTBakedModel;
import com.circulation.ae2wut.handler.GuiHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

@SideOnly(Side.CLIENT)
public final class TooltipButton extends GuiButton implements ITooltip {

    private final byte t;
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

    public void drawButton(@NotNull Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            mc.renderEngine.bindTexture(GuiHandler.wut$guiRl);
            this.drawTexturedModalRect(this.x, this.y, 240, 240, 16, 16);
            this.drawTexturedModalRect(this.x, this.y, ((t < 0 ? nowGui : this.t) - 1) * 16, 0, 16, 16);

            this.mouseDragged(mc, mouseX, mouseY);
            GlStateManager.popMatrix();
        }
    }

    public String getMessage() {
        return this.t >= 0 ? ItemWUTBakedModel.getIconItem(this.t).getDisplayName() : I18n.format("wut.btn.name");
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