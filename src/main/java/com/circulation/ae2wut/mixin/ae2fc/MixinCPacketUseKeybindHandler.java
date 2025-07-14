package com.circulation.ae2wut.mixin.ae2fc;

import baubles.api.BaublesApi;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import com.glodblock.github.inventory.GuiType;
import com.glodblock.github.network.CPacketUseKeybind;
import com.glodblock.github.util.Util;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(value = CPacketUseKeybind.Handler.class, remap = false)
public class MixinCPacketUseKeybindHandler {

    @Inject(method = "onMessage(Lcom/glodblock/github/network/CPacketUseKeybind;Lnet/minecraftforge/fml/common/network/simpleimpl/MessageContext;)Lnet/minecraftforge/fml/common/network/simpleimpl/IMessage;", at = @At(value= "HEAD"))
    public void onMessageMixin(CPacketUseKeybind message, MessageContext ctx, CallbackInfoReturnable<IMessage> cir) {
        final EntityPlayerMP player = ctx.getServerHandler().player;
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stackInSlot = player.inventory.getStackInSlot(i);
            if (stackInSlot.getTagCompound() != null) {
                List<Integer> list = null;
                if (stackInSlot.getTagCompound().hasKey("modes")) {
                    list = Arrays.stream(stackInSlot.getTagCompound().getIntArray("modes")).boxed().collect(Collectors.toList());
                }
                if (stackInSlot.getItem() instanceof ItemWirelessUniversalTerminal && list != null && list.contains(4)) {
                    ItemWirelessUniversalTerminal.INSTANCE.nbtChangeB(stackInSlot);
                    ItemWirelessUniversalTerminal.INSTANCE.nbtChange(stackInSlot, 4);
                    Util.openWirelessTerminal(stackInSlot, i, false, player.world, player, GuiType.WIRELESS_FLUID_PATTERN_TERMINAL);
                    return;
                }
            }
        }
        if (Loader.isModLoaded("baubles")) {
            tryOpenBauble(player);
        }
    }

    @Shadow
    private static void tryOpenBauble(EntityPlayer player){}

    @Inject(method="tryOpenBauble", at = @At(value= "HEAD"), cancellable = true)
    private static void tryOpenBaubleMixin(EntityPlayer player, CallbackInfo ci) {
        if (Loader.isModLoaded("baubles"))
            aE2UELWirelessUniversalTerminal$d(player, ci);
    }

    @Unique
    @Optional.Method(modid = "baubles")
    private static void aE2UELWirelessUniversalTerminal$d(EntityPlayer player, CallbackInfo ci){
        for (int i = 0; i < BaublesApi.getBaublesHandler(player).getSlots(); i++) {
            ItemStack stackInSlot = BaublesApi.getBaublesHandler(player).getStackInSlot(i);
            if (stackInSlot.getItem() instanceof ItemWirelessUniversalTerminal && stackInSlot.getTagCompound() != null) {
                List<Integer> list = null;
                if (stackInSlot.getTagCompound().hasKey("modes")) {
                    list = Arrays.stream(stackInSlot.getTagCompound().getIntArray("modes")).boxed().collect(Collectors.toList());
                }
                if (list != null && list.contains(4)) {
                    ItemWirelessUniversalTerminal.INSTANCE.nbtChangeB(stackInSlot);
                    ItemWirelessUniversalTerminal.INSTANCE.nbtChange(stackInSlot, 4);
                    Util.openWirelessTerminal(stackInSlot, i, true, player.world, player, GuiType.WIRELESS_FLUID_PATTERN_TERMINAL);
                    ci.cancel();
                    return;
                }
            }
        }
    }
}
