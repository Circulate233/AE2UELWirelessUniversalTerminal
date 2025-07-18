package com.circulation.ae2wut.mixin.ae2exttable;

import appeng.core.sync.network.INetworkInfo;
import appeng.me.GridAccessException;
import appeng.util.Platform;
import baubles.api.BaublesApi;
import com._0xc4de.ae2exttable.client.gui.AE2ExtendedGUIs;
import com._0xc4de.ae2exttable.interfaces.ITerminalGui;
import com._0xc4de.ae2exttable.network.ExtendedTerminalPacket;
import com._0xc4de.ae2exttable.network.packets.PacketOpenWirelessGui;
import com.circulation.ae2wut.AE2UELWirelessUniversalTerminal;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import com.circulation.ae2wut.network.UpdateItemModeMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Optional;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PacketOpenWirelessGui.class,remap = false)
public abstract class MixinPacketOpenWirelessGui {

    @Final
    @Shadow
    private AE2ExtendedGUIs gui;

    @Shadow protected abstract void openGui(ItemStack itemStack, int slotIndex, EntityPlayer player, boolean isBauble) throws GridAccessException;

    /**
     * @author circulation
     * @reason 覆写方法处理
     * TODO:等待ae2exttable更新后修改为Inject
     */
    @Overwrite
    public void serverPacketData(final INetworkInfo manager,
                                 final ExtendedTerminalPacket packet,
                                 final EntityPlayer player) throws GridAccessException {
        NonNullList<ItemStack> inventory = player.inventory.mainInventory;
        var mode = ae2WirelessUniversalTerminal$determineMode();

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack is = inventory.get(i);
            if ((is.getItem() instanceof ItemWirelessUniversalTerminal wut && wut.hasMode(is, mode)) || (is.getItem() instanceof ITerminalGui wt && wt.getGuiType() == this.gui)) {
                openGui(is, i, player, false);
                return;
            }
        }
        if (Platform.isModLoaded("baubles")) {
            r$tryOpenBauble(player);
        }
    }

    @Unique
    @Optional.Method(modid = "baubles")
    private void r$tryOpenBauble(EntityPlayer player) throws GridAccessException {
        var mode = ae2WirelessUniversalTerminal$determineMode();
        for (int i = 0; i< BaublesApi.getBaublesHandler(player).getSlots(); i++) {
            ItemStack is = BaublesApi.getBaublesHandler(player).getStackInSlot(i);
            if ((is.getItem() instanceof ItemWirelessUniversalTerminal wut && wut.hasMode(is, mode)) || (is.getItem() instanceof ITerminalGui wt && wt.getGuiType() == this.gui)) {
                openGui(is, i, player, true);
                return;
            }
        }
    }

    @Inject(method = "openGui", at = @At("HEAD"))
    private void openGuiMixin(ItemStack terminal, int slotIndex, EntityPlayer player, boolean isBauble, CallbackInfo ci) {
        if (terminal.getItem() instanceof ItemWirelessUniversalTerminal wut){
            wut.nbtChangeB(terminal);
            final var mode = ae2WirelessUniversalTerminal$determineMode();
            wut.nbtChange(terminal,mode);
            if (player instanceof EntityPlayerMP playerMP)
                AE2UELWirelessUniversalTerminal.NET_CHANNEL.sendTo(new UpdateItemModeMessage(slotIndex,mode,isBauble),playerMP);
        }
    }

    @Unique
    private byte ae2WirelessUniversalTerminal$determineMode() {
        return switch (this.gui){
            case WIRELESS_BASIC_CRAFTING_TERMINAL -> 6;
            case WIRELESS_ADVANCED_CRAFTING_TERMINAL -> 7;
            case WIRELESS_ELITE_CRAFTING_TERMINAL -> 8;
            case WIRELESS_ULTIMATE_CRAFTING_TERMINAL -> 9;
            default -> 0;
        };
    }
}
