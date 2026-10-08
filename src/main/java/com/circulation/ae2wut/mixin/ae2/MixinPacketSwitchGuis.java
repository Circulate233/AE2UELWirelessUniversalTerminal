package com.circulation.ae2wut.mixin.ae2;

import appeng.api.AEApi;
import appeng.api.features.IWirelessTermHandler;
import appeng.container.AEBaseContainer;
import appeng.core.sync.AppEngPacket;
import appeng.core.sync.GuiBridge;
import appeng.core.sync.network.INetworkInfo;
import appeng.core.sync.packets.PacketSwitchGuis;
import appeng.helpers.WirelessTerminalGuiObject;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The wireless terminal bridges carried by PacketSwitchGuis are picked by the client, and the craft
 * confirm GUI derives them from the item NBT the client happens to see. Those are checked against the
 * server side item so a client cannot open a terminal this item does not have.
 */
@Mixin(value = PacketSwitchGuis.class, remap = false)
public abstract class MixinPacketSwitchGuis {

    @Final
    @Shadow
    private GuiBridge newGui;

    @Inject(method = "serverPacketData", at = @At("HEAD"), cancellable = true)
    public void wut$validateTargetGui(INetworkInfo manager, AppEngPacket packet, EntityPlayer player, CallbackInfo ci) {
        // PacketSwitchGuis is also how a terminal reaches unrelated GUIs (the crafting status, for
        // one), so anything that is not a wireless terminal bridge is left to the vanilla handling.
        if (!wut$isWirelessTerminalBridge(this.newGui)) return;
        if (!(player.openContainer instanceof AEBaseContainer container)) return;
        if (container.getOpenContext() == null) return;
        if (!(container.getTarget() instanceof WirelessTerminalGuiObject obj)) return;
        ItemStack stack = obj.getItemStack();
        if (!(stack.getItem() instanceof ItemWirelessUniversalTerminal)) return;
        IWirelessTermHandler handler = AEApi.instance().registries().wireless().getWirelessTerminalHandler(stack);
        if (handler == null || handler.getGuiHandler(stack) != this.newGui) {
            ci.cancel();
        }
    }

    @Unique
    private static boolean wut$isWirelessTerminalBridge(GuiBridge bridge) {
        return bridge == GuiBridge.GUI_WIRELESS_TERM
            || bridge == GuiBridge.GUI_WIRELESS_CRAFTING_TERMINAL
            || bridge == GuiBridge.GUI_WIRELESS_PATTERN_TERMINAL
            || bridge == GuiBridge.GUI_WIRELESS_FLUID_TERMINAL
            || bridge == GuiBridge.GUI_WIRELESS_INTERFACE_TERMINAL;
    }
}
