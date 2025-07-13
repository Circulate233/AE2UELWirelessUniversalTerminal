package com.circulation.ae2wut.mixin.ae2;

import appeng.core.sync.AppEngPacket;
import appeng.core.sync.network.INetworkInfo;
import appeng.core.sync.packets.PacketTerminalUse;
import appeng.items.tools.powered.Terminal;
import baubles.api.BaublesApi;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(value = PacketTerminalUse.class,remap = false)
public abstract class MixinPacketTerminalUse extends AppEngPacket {

    @Shadow
    Terminal terminal;
    @Shadow
    void openGui(ItemStack itemStack, int slotIdx, EntityPlayer player, boolean isBauble){}

    @Inject(method="serverPacketData", at = @At(value= "HEAD"), cancellable = true)
    public void serverPacketDataMixin(INetworkInfo manager, AppEngPacket packet, EntityPlayer player, CallbackInfo ci) {
        int mode = ae2WirelessUniversalTerminal$determineMode();
        NonNullList<ItemStack> mainInventory = player.inventory.mainInventory;
        for(int i = 0; i < mainInventory.size(); ++i) {
            ItemStack is = mainInventory.get(i);
            if (is.getItem() == ItemWirelessUniversalTerminal.INSTANCE && is.getTagCompound() != null) {
                List<Integer> list = null;
                if (is.getTagCompound().hasKey("modes")) {
                    list = Arrays.stream(is.getTagCompound().getIntArray("modes")).boxed().collect(Collectors.toList());
                }
                if (list != null && list.contains(mode)) {
                    ItemWirelessUniversalTerminal.INSTANCE.nbtChangeB(is);
                    ItemWirelessUniversalTerminal.INSTANCE.nbtChange(is, mode);
                    openGui(is, i, player, false);
                    ci.cancel();
                    return;
                }
            }
        }
    }

    @Unique
    private int ae2WirelessUniversalTerminal$determineMode() {
         return switch (this.terminal){
            case WIRELESS_CRAFTING_TERMINAL -> 1;
            case WIRELESS_PATTERN_TERMINAL-> 3;
            case WIRELESS_FLUID_TERMINAL -> 2;
            case WIRELESS_INTERFACE_TERMINAL -> 10;
            default -> 0;
        };
    }

    @Inject(method="tryOpenBauble", at = @At(value= "HEAD"), cancellable = true)
    void tryOpenBaubleMixin(EntityPlayer player, CallbackInfo ci) {
        if (Loader.isModLoaded("baubles"))
            aE2UELWirelessUniversalTerminal$d(player,ci);
    }

    @Unique
    @Optional.Method(modid = "baubles")
    private void aE2UELWirelessUniversalTerminal$d(EntityPlayer player, CallbackInfo ci){
        int mode = ae2WirelessUniversalTerminal$determineMode();
        for(int i = 0; i < BaublesApi.getBaublesHandler(player).getSlots(); ++i) {
            ItemStack is = BaublesApi.getBaublesHandler(player).getStackInSlot(i);
            if (is.getItem() == ItemWirelessUniversalTerminal.INSTANCE && is.getTagCompound() != null) {
                List<Integer> list = null;
                if (is.getTagCompound().hasKey("modes")) {
                    list = Arrays.stream(is.getTagCompound().getIntArray("modes")).boxed().collect(Collectors.toList());
                }
                if (list != null && list.contains(mode)) {
                    ItemWirelessUniversalTerminal.INSTANCE.nbtChangeB(is);
                    ItemWirelessUniversalTerminal.INSTANCE.nbtChange(is, mode);
                    openGui(is, i, player, true);
                    ci.cancel();
                    return;
                }
            }
        }
    }

}
