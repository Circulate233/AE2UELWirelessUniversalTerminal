package com.circulation.ae2wut.mixin.ae2fc;

import baubles.api.BaublesApi;
import com.circulation.ae2wut.AE2UELWirelessUniversalTerminal;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import com.glodblock.github.network.CPacketUseKeybind;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CPacketUseKeybind.Handler.class, remap = false)
public class MixinCPacketUseKeybindHandler {

    @Inject(method = "onMessage(Lcom/glodblock/github/network/CPacketUseKeybind;Lnet/minecraftforge/fml/common/network/simpleimpl/MessageContext;)Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;", at = @At(value = "HEAD"), cancellable = true)
    public void onMessageMixin(CPacketUseKeybind message, MessageContext ctx, CallbackInfoReturnable<IMessage> cir) {
        final EntityPlayerMP player = ctx.getServerHandler().player;
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack is = player.inventory.getStackInSlot(i);
            final var tag = is.getTagCompound();
            if (tag != null) {
                IntList list = null;
                if (tag.hasKey("modes")) {
                    list = new IntArrayList(tag.getIntArray("modes"));
                }
                if (is.getItem() instanceof ItemWirelessUniversalTerminal && list != null && list.contains(4)) {
                    int finalI = i;
                    player.getServer().addScheduledTask(() -> AE2UELWirelessUniversalTerminal.openWirelessTerminalGui(is, player, 4, finalI, false));
                    cir.setReturnValue(null);
                    return;
                }
            }
        }
        if (Loader.isModLoaded("baubles")) {
            tryOpenBauble(player);
        }
    }

    @Shadow
    private static void tryOpenBauble(EntityPlayer player) {
    }

    @Inject(method = "tryOpenBauble", at = @At(value = "HEAD"), cancellable = true)
    private static void tryOpenBaubleMixin(EntityPlayer player, CallbackInfo ci) {
        if (Loader.isModLoaded("baubles"))
            aE2UELWirelessUniversalTerminal$d(player, ci);
    }

    @Unique
    @Optional.Method(modid = "baubles")
    private static void aE2UELWirelessUniversalTerminal$d(EntityPlayer player, CallbackInfo ci) {
        for (int i = 0; i < BaublesApi.getBaublesHandler(player).getSlots(); i++) {
            ItemStack is = BaublesApi.getBaublesHandler(player).getStackInSlot(i);
            final var tag = is.getTagCompound();
            if (tag != null && is.getItem() instanceof ItemWirelessUniversalTerminal) {
                IntList list = null;
                if (tag.hasKey("modes")) {
                    list = new IntArrayList(tag.getIntArray("modes"));
                }
                if (list != null && list.contains(4)) {
                    final int finalI = i;
                    player.getServer().addScheduledTask(() -> AE2UELWirelessUniversalTerminal.openWirelessTerminalGui(is, player, 4, finalI, true));
                    ci.cancel();
                    return;
                }
            }
        }
    }
}