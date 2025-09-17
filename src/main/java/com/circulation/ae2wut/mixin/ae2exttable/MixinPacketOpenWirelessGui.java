package com.circulation.ae2wut.mixin.ae2exttable;

import appeng.core.sync.network.INetworkInfo;
import appeng.util.Platform;
import baubles.api.BaublesApi;
import com._0xc4de.ae2exttable.client.gui.AE2ExtendedGUIs;
import com._0xc4de.ae2exttable.network.ExtendedTerminalPacket;
import com._0xc4de.ae2exttable.network.packets.PacketOpenWirelessGui;
import com.circulation.ae2wut.AE2UELWirelessUniversalTerminal;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Optional;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PacketOpenWirelessGui.class,remap = false)
public abstract class MixinPacketOpenWirelessGui {

    @Final
    @Shadow
    private AE2ExtendedGUIs gui;

    @Inject(method = "serverPacketData", at = @At("HEAD"))
    public void serverPacketData(INetworkInfo manager, ExtendedTerminalPacket packet, EntityPlayer player, CallbackInfo ci) {
        NonNullList<ItemStack> inventory = player.inventory.mainInventory;
        var mode = ItemWirelessUniversalTerminal.getAE2EMode(this.gui);

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack is = inventory.get(i);
            if (is.getItem() instanceof ItemWirelessUniversalTerminal wut && wut.hasMode(is, mode)) {
                AE2UELWirelessUniversalTerminal.openWirelessTerminalGui(is, player, mode, i, false);
                return;
            }
        }
        if (Platform.isModLoaded("baubles")) {
            r$tryOpenBauble(player);
        }
    }

    @Unique
    @Optional.Method(modid = "baubles")
    private void r$tryOpenBauble(EntityPlayer player) {
        var mode = ItemWirelessUniversalTerminal.getAE2EMode(this.gui);
        for (int i = 0; i < BaublesApi.getBaublesHandler(player).getSlots(); i++) {
            ItemStack is = BaublesApi.getBaublesHandler(player).getStackInSlot(i);
            if (is.getItem() instanceof ItemWirelessUniversalTerminal wut && wut.hasMode(is, mode)) {
                AE2UELWirelessUniversalTerminal.openWirelessTerminalGui(is, player, mode, i, true);
                return;
            }
        }
    }
}