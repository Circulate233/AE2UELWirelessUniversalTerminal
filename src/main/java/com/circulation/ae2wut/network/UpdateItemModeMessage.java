package com.circulation.ae2wut.network;

import baubles.api.BaublesApi;
import com.circulation.ae2wut.AE2UELWirelessUniversalTerminal;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class UpdateItemModeMessage implements IMessage, IMessageHandler<UpdateItemModeMessage, IMessage> {
    private byte slot;
    private byte mode;
    private boolean isBaubles;

    public UpdateItemModeMessage() {
    }

    public UpdateItemModeMessage(int slot, int mode, boolean isBaubles) {
        this.slot = (byte) slot;
        this.mode = (byte) mode;
        this.isBaubles = isBaubles;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slot = buf.readByte();
        mode = buf.readByte();
        isBaubles = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(slot);
        buf.writeByte(mode);
        buf.writeBoolean(isBaubles);
    }

    @Override
    public IMessage onMessage(UpdateItemModeMessage message, MessageContext ctx) {
        if (ctx.side == Side.SERVER) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                ItemStack stack = getTerminal(player, message.slot, message.isBaubles);
                if (stack.getItem() instanceof ItemWirelessUniversalTerminal wut && wut.hasMode(stack, message.mode)) {
                    stack.getTagCompound().setByte("mode", message.mode);
                    AE2UELWirelessUniversalTerminal.NET_CHANNEL.sendTo(
                        new UpdateItemModeMessage(message.slot, message.mode, message.isBaubles), player);
                }
            });
            return null;
        }

        ItemStack stack = getTerminal(getClientPlayer(), message.slot, message.isBaubles);
        if (stack.getItem() instanceof ItemWirelessUniversalTerminal && stack.getTagCompound() != null) {
            stack.getTagCompound().setByte("mode", message.mode);
        }
        return null;
    }

    private static ItemStack getTerminal(EntityPlayer player, byte slot, boolean isBaubles) {
        if (player == null || slot < 0) return ItemStack.EMPTY;
        if (!isBaubles) return player.inventory.getStackInSlot(slot);
        return Loader.isModLoaded("baubles") ? getStackInBaubleSlot(player, slot) : ItemStack.EMPTY;
    }

    @Optional.Method(modid = "baubles")
    private static ItemStack getStackInBaubleSlot(EntityPlayer player, int slot) {
        return slot < BaublesApi.getBaublesHandler(player).getSlots() ? BaublesApi.getBaublesHandler(player).getStackInSlot(slot) : ItemStack.EMPTY;
    }

    @SideOnly(Side.CLIENT)
    private static EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().player;
    }
}
