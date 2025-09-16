package com.circulation.ae2wut.network;

import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class WirelessTerminalRefresh implements IMessage {

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<WirelessTerminalRefresh, IMessage> {
        @Override
        public IMessage onMessage(WirelessTerminalRefresh message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> ItemWirelessUniversalTerminal.INSTANCE.nbtChangeB(player));
            return null;
        }
    }
}