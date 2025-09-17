package com.circulation.ae2wut.client.model;

import com.circulation.ae2wut.client.TooltipButton;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;

@SideOnly(Side.CLIENT)
class ItemWUTBakedModel implements IBakedModel {
    private final IBakedModel baseModel;
    private final WutOverrideList overrides;

    ItemWUTBakedModel(@NotNull IBakedModel baseModel) {
        this.baseModel = baseModel;
        this.overrides = new WutOverrideList();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return this.baseModel.getQuads(state, side, rand);
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        return this.baseModel.handlePerspective(cameraTransformType);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return this.baseModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.baseModel.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return this.baseModel.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.baseModel.getParticleTexture();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return this.overrides;
    }

    private class WutOverrideList extends ItemOverrideList {
        WutOverrideList() {
            super(ItemWUTBakedModel.this.baseModel.getOverrides().getOverrides());
        }

        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
            if (stack.hasTagCompound() && !stack.getTagCompound().hasKey("Nova")) {
                byte mode = stack.getTagCompound().getByte("mode");
                if (mode > 0) {
                    var icon = TooltipButton.getIconMap().get(mode);
                    if (!icon.isEmpty()) {
                        return Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(icon, world, entity);
                    }
                }
            }

            return ItemWUTBakedModel.this.baseModel.getOverrides().handleItemState(originalModel, stack, world, entity);
        }
    }
}