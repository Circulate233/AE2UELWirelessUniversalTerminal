package com.circulation.ae2wut.utils;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Immutable record of a single sprite's position and size within the
 * {@link ComponentAtlas} texture sheet.
 * 单个精灵在 {@link ComponentAtlas} 纹理图集中的位置和尺寸（不可变）。
 *
 * <p>All coordinates and dimensions are in <em>texels</em> (integer pixel positions
 * within the atlas image). Use {@link #u0()}/{@link #v0()} etc. for normalized
 * [0, 1] UV coordinates ready for GL calls.
 * 所有坐标和尺寸均为纹素（整数像素位置）。使用 u0()/v0() 等方法获取归一化 [0,1] UV 坐标。
 */
@SideOnly(Side.CLIENT)
public final class AtlasRegion {

    /**
     * Sprite name — the file's base name without extension, e.g. {@code "button_idle"}.
     */
    public final int name;

    /**
     * Left edge of the sprite within the atlas, in texels.
     */
    public final int x;
    /**
     * Top edge of the sprite within the atlas, in texels.
     */
    public final int y;
    /**
     * Width of the sprite, in texels.
     */
    public final int width;
    /**
     * Height of the sprite, in texels.
     */
    public final int height;

    /**
     * Side-length of the square atlas texture this region belongs to, in texels.
     */
    public final int atlasSize;

    AtlasRegion(int name, int x, int y, int width, int height, int atlasSize) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.atlasSize = atlasSize;
    }

    // -------------------------------------------------------------------------
    // Normalised UV helpers (for use with GL / drawTexturedModalRect)
    // -------------------------------------------------------------------------

    /**
     * Normalised left UV (0–1).
     */
    public float u0() {
        return (float) x / atlasSize;
    }

    /**
     * Normalised top UV (0–1).
     */
    public float v0() {
        return (float) y / atlasSize;
    }

    /**
     * Normalised right UV (0–1).
     */
    public float u1() {
        return (float) (x + width) / atlasSize;
    }

    /**
     * Normalised bottom UV (0–1).
     */
    public float v1() {
        return (float) (y + height) / atlasSize;
    }

    @Override
    public String toString() {
        return "AtlasRegion{" + name + " @(" + x + "," + y + ") " + width + "x" + height
            + " atlas=" + atlasSize + "}";
    }
}
