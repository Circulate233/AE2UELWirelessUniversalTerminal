package com.circulation.ae2wut.utils;

import com.circulation.ae2wut.AE2UELWirelessUniversalTerminal;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.zip.CRC32;

@SideOnly(Side.CLIENT)
public final class ComponentAtlas {

    public static final ComponentAtlas INSTANCE = new ComponentAtlas();

    private static final String COMPONENT_DIR = "textures/gui/";
    private static final String DOMAIN = AE2UELWirelessUniversalTerminal.MOD_ID;
    private static final int PADDING = 1;

    private final Int2ObjectMap<AtlasRegion> regions = new Int2ObjectOpenHashMap<>();

    private CompletableFuture<StitchResult> future;
    private int glTextureId = 0;
    private File cacheDir;
    private boolean init;

    private ComponentAtlas() {
    }

    private static StitchResult stitch(List<SpriteData> sprites) {
        List<SpriteData> sorted = new ObjectArrayList<>(sprites);
        sorted.sort(Comparator.comparingInt((SpriteData s) -> s.image.getWidth() * s.image.getHeight())
                              .reversed());

        return tryPackForceFit(sorted);
    }


    private static StitchResult tryPackForceFit(List<SpriteData> sprites) {
        BufferedImage atlas = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = atlas.createGraphics();
        g.setBackground(new Color(0, 0, 0, 0));
        g.clearRect(0, 0, 256, 256);

        List<AtlasRegion> regions = new ObjectArrayList<>();
        int shelfX = PADDING, shelfY = PADDING, shelfHeight = 0;

        for (SpriteData sprite : sprites) {
            int sw = sprite.image.getWidth();
            int sh = sprite.image.getHeight();

            if (shelfX + sw + PADDING > 256) {
                shelfX = PADDING;
                shelfY += shelfHeight + PADDING;
                shelfHeight = 0;
            }
            if (shelfY + sh + PADDING > 256) {
                continue;
            }

            g.drawImage(sprite.image, shelfX, shelfY, null);
            regions.add(new AtlasRegion(sprite.name, shelfX, shelfY, sw, sh, 256));
            shelfHeight = Math.max(shelfHeight, sh);
            shelfX += sw + PADDING;
        }

        g.dispose();
        return new StitchResult(atlas, regions);
    }

    /**
     * Recomputes region coordinates from a cached atlas image.
     * The layout is deterministic given the name-sorted sprite list.
     */
    private static StitchResult buildRegions(List<SpriteData> sprites, BufferedImage cachedImage) {
        int size = cachedImage.getWidth();
        List<SpriteData> sorted = new ObjectArrayList<>(sprites);
        sorted.sort(Comparator.comparingInt((SpriteData s) -> s.image.getWidth() * s.image.getHeight())
                              .reversed());

        List<AtlasRegion> regions = new ObjectArrayList<>();
        int shelfX = PADDING, shelfY = PADDING, shelfHeight = 0;

        for (SpriteData sprite : sorted) {
            int sw = sprite.image.getWidth();
            int sh = sprite.image.getHeight();

            if (shelfX + sw + PADDING > size) {
                shelfX = PADDING;
                shelfY += shelfHeight + PADDING;
                shelfHeight = 0;
            }
            if (shelfY + sh + PADDING > size) break;

            regions.add(new AtlasRegion(sprite.name, shelfX, shelfY, sw, sh, size));
            shelfHeight = Math.max(shelfHeight, sh);
            shelfX += sw + PADDING;
        }

        return new StitchResult(cachedImage, regions);
    }

    private static int uploadImage(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);

        ByteBuffer buf = ByteBuffer.allocateDirect(width * height * 4).order(ByteOrder.nativeOrder());
        for (int pixel : pixels) {
            buf.put((byte) ((pixel >> 16) & 0xFF));
            buf.put((byte) ((pixel >> 8) & 0xFF));
            buf.put((byte) (pixel & 0xFF));
            buf.put((byte) ((pixel >> 24) & 0xFF));
        }
        buf.flip();

        int texId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height,
            0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

        return texId;
    }

    private static BufferedImage createFallback() {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        img.setRGB(0, 0, 0xFFFFFFFF);
        return img;
    }

    private static String computeHash(List<SpriteData> sortedSprites) {
        CRC32 crc = new CRC32();
        for (SpriteData s : sortedSprites) {
            crc.update(s.name);
            int w = s.image.getWidth();
            int h = s.image.getHeight();
            int[] pixels = new int[w * h];
            s.image.getRGB(0, 0, w, h, pixels, 0, w);
            ByteBuffer buf = ByteBuffer.allocate(pixels.length * 4);
            for (int p : pixels) buf.putInt(p);
            crc.update(buf.array());
        }
        return String.format("%016x", crc.getValue());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void startAsync(File modConfigDir) {
        if (init) return;
        init = true;

        cacheDir = modConfigDir;
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        Minecraft mc = Minecraft.getMinecraft();
        int[] names = AE2UELWirelessUniversalTerminal.proxy.getAllMode();

        List<RawSprite> rawSprites = new ObjectArrayList<>();

        {
            ResourceLocation loc = new ResourceLocation(DOMAIN, COMPONENT_DIR + "button.png");
            try (InputStream is = mc.getResourceManager().getResource(loc).getInputStream()) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] chunk = new byte[8192];
                int n;
                while ((n = is.read(chunk)) != -1) baos.write(chunk, 0, n);
                rawSprites.add(new RawSprite(Integer.MAX_VALUE, baos.toByteArray()));
            } catch (Exception ignored) {
            }
        }

        for (int name : names) {
            ResourceLocation loc = new ResourceLocation(DOMAIN, COMPONENT_DIR + name + ".png");
            try (InputStream is = mc.getResourceManager().getResource(loc).getInputStream()) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] chunk = new byte[8192];
                int n;
                while ((n = is.read(chunk)) != -1) baos.write(chunk, 0, n);
                rawSprites.add(new RawSprite(name, baos.toByteArray()));
            } catch (Exception ignored) {
            }
        }

        if (rawSprites.isEmpty()) {
            future = CompletableFuture.completedFuture(StitchResult.EMPTY);
            return;
        }

        future = CompletableFuture.supplyAsync(() -> {
            try {
                List<SpriteData> sprites = new ObjectArrayList<>();
                for (RawSprite raw : rawSprites) {
                    BufferedImage img = ImageIO.read(new ByteArrayInputStream(raw.bytes));
                    if (img != null) sprites.add(new SpriteData(raw.name, img));
                }
                if (sprites.isEmpty()) return StitchResult.EMPTY;

                sprites.sort(Comparator.comparing(s -> s.name));

                String hash = computeHash(sprites);
                File cacheFile = new File(cacheDir, "atlas_" + hash + ".png");

                if (cacheFile.exists()) {
                    BufferedImage cached = ImageIO.read(cacheFile);
                    if (cached != null) {
                        return buildRegions(sprites, cached);
                    }
                }

                StitchResult result = stitch(sprites);

                File[] old = cacheDir.listFiles(f ->
                    f.isFile() && f.getName().startsWith("atlas_") && f.getName().endsWith(".png"));
                if (old != null) {
                    for (File f : old) f.delete();
                }
                try {
                    ImageIO.write(result.image, "PNG", cacheFile);
                } catch (IOException ignored) {
                }
                return result;

            } catch (Exception e) {
                return StitchResult.EMPTY;
            }
        });
    }

    public void restart() {
        if (cacheDir != null) {
            init = false;
            startAsync(cacheDir);
        }
    }

    public void awaitReady() {
        if (future == null || glTextureId != 0) return;

        StitchResult result;
        try {
            result = future.join();
        } catch (Exception e) {
            return;
        }

        if (result == StitchResult.EMPTY || result.image == null) {
            glTextureId = uploadImage(createFallback());
        } else {
            glTextureId = uploadImage(result.image);
            regions.clear();
            for (AtlasRegion r : result.regions) {
                regions.put(r.name, r);
            }
        }
    }

    public void bind() {
        if (glTextureId != 0) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, glTextureId);
        }
    }

    public boolean isReady() {
        return glTextureId != 0;
    }

    @Nullable
    public AtlasRegion getRegion(int name) {
        return regions.get(name);
    }

    public void dispose() {
        if (glTextureId != 0) {
            GL11.glDeleteTextures(glTextureId);
            glTextureId = 0;
        }
        regions.clear();
        future = null;
    }

    private static final class RawSprite {
        final int name;
        final byte[] bytes;

        RawSprite(int name, byte[] bytes) {
            this.name = name;
            this.bytes = bytes;
        }
    }

    /**
     * Decoded sprite image ready for stitching.
     */
    private static final class SpriteData {
        final int name;
        final BufferedImage image;

        SpriteData(int name, BufferedImage image) {
            this.name = name;
            this.image = image;
        }
    }

    /**
     * Result produced by the background stitching thread.
     */
    static final class StitchResult {
        static final StitchResult EMPTY = new StitchResult(null, Collections.emptyList());

        final BufferedImage image;
        final List<AtlasRegion> regions;

        StitchResult(BufferedImage image, List<AtlasRegion> regions) {
            this.image = image;
            this.regions = regions;
        }
    }
}
