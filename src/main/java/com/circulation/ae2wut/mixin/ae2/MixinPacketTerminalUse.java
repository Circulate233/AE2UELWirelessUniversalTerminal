package com.circulation.ae2wut.mixin.ae2;

import appeng.core.sync.AppEngPacket;
import appeng.core.sync.network.INetworkInfo;
import appeng.core.sync.packets.PacketTerminalUse;
import appeng.items.tools.powered.Terminal;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
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
        NonNullList<ItemStack> mainInventory = player.inventory.mainInventory;
        for(int i = 0; i < mainInventory.size(); ++i) {
            ItemStack is = mainInventory.get(i);
            if (is.getItem() == ItemWirelessUniversalTerminal.INSTANCE && is.getTagCompound() != null) {
                int mode = ae2WirelessUniversalTerminal$determineMode(terminal.name());
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
    private int ae2WirelessUniversalTerminal$determineMode(String value) {
        switch (value){
            case "WIRELESS_CRAFTING_TERMINAL" :
                return 1;
            case "WIRELESS_PATTERN_TERMINAL":
                return 3;
            case "WIRELESS_FLUID_TERMINAL" :
                return 2;
            case "WIRELESS_INTERFACE_TERMINAL" :
                return 10;
            default:return 0;
        }
    }
}
