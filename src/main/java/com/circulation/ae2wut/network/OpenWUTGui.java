package com.circulation.ae2wut.network;

import appeng.helpers.WirelessTerminalGuiObject;
import baubles.api.BaublesApi;
import com.circulation.ae2wut.AE2UELWirelessUniversalTerminal;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class OpenWUTGui implements IMessage, IMessageHandler<OpenWUTGui, IMessage> {

    private boolean isBauble;
    private byte mode;
    private byte slot;

    public OpenWUTGui() {
    }

    public OpenWUTGui(WirelessTerminalGuiObject obj, byte mode){
        this.mode = mode;
        this.isBauble = obj.isBaubleSlot();
        this.slot = (byte) obj.getInventorySlot();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.isBauble = buf.readBoolean();
        this.mode = buf.readByte();
        this.slot = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(isBauble);
        buf.writeByte(mode);
        buf.writeByte(slot);
    }

    @Override
    public IMessage onMessage(OpenWUTGui message, MessageContext ctx) {
        ItemStack terminal;
        var player = ctx.getServerHandler().player;
        if (!message.isBauble){
            terminal = player.inventory.getStackInSlot(message.slot);
        } else if (Loader.isModLoaded("baubles") && message.isBauble) {
            terminal = getBaubleItem(player,message.slot);
        } else {
            terminal = ItemStack.EMPTY;
        }
        if (terminal.getItem() == ItemWirelessUniversalTerminal.INSTANCE) {
            AE2UELWirelessUniversalTerminal
                    .openWirelessTerminalGui(
                            terminal, player, message.mode, message.slot, message.isBauble
                    );
        }
        return null;
    }

    @Optional.Method(modid = "baubles")
    protected ItemStack getBaubleItem(EntityPlayer player, int slot) {
        return BaublesApi.getBaublesHandler(player).getStackInSlot(slot);
    }
}
