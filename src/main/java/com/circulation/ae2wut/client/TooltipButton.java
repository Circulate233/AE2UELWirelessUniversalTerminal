package com.circulation.ae2wut.client;

import appeng.client.gui.widgets.ITooltip;
import com.circulation.ae2wut.client.model.ItemWUTBakedModel;
import com.circulation.ae2wut.handler.GuiHandler;
import com.circulation.ae2wut.recipes.AllWUTRecipe;
import com.circulation.ae2wut.utils.AtlasRegion;
import com.circulation.ae2wut.utils.ComponentAtlas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TooltipButton extends GuiButton implements ITooltip {

    private final byte t;
    private byte nowGui;

    public TooltipButton(int x, int y, byte t) {
        super(0, x, y, "");
        this.width = 16;
        this.height = 16;
        this.t = t;
    }

    public TooltipButton(int x, int y, byte t, byte nowGui) {
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
            draw("button");
            draw(AllWUTRecipe.itemList.get(t < 0 ? nowGui : this.t).getItem().getRegistryName().getPath());

            this.mouseDragged(mc, mouseX, mouseY);
            GlStateManager.popMatrix();
        }
    }

    protected void draw(String id) {
        ComponentAtlas atlas = ComponentAtlas.INSTANCE;
        if (!atlas.isReady()) return;

        atlas.bind();
        int ax = x;
        int ay = y;
        Tessellator tess = Tessellator.getInstance();

        AtlasRegion region = atlas.getRegion(id);
        if (region == null) return;
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buf.pos(ax, ay + height, 0).tex(region.u0(), region.v1()).endVertex();
        buf.pos(ax + width, ay + height, 0).tex(region.u1(), region.v1()).endVertex();
        buf.pos(ax + width, ay, 0).tex(region.u1(), region.v0()).endVertex();
        buf.pos(ax, ay, 0).tex(region.u0(), region.v0()).endVertex();
        tess.draw();
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