package com.circulation.ae2wut.network;

import baubles.api.BaublesApi;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class UpdateItemModeMessage implements IMessage,IMessageHandler<UpdateItemModeMessage, IMessage> {
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
        EntityPlayer player = switch (ctx.side) {
            case SERVER -> ctx.getServerHandler().player;
            case CLIENT -> getClientPlayer();
        };

        ItemStack stack = ItemStack.EMPTY;

        if (!message.isBaubles)
            stack = player.inventory.getStackInSlot(message.slot);
        else if (Loader.isModLoaded("baubles"))
            stack = getStackInBaubleSlot(player, message.slot);

        if (stack.getItem() instanceof ItemWirelessUniversalTerminal && stack.getTagCompound() != null) {
            stack.getTagCompound().setInteger("mode", message.mode);
        }
        return null;
    }

    @Optional.Method(modid = "baubles")
    private ItemStack getStackInBaubleSlot(EntityPlayer player, int slot) {
        return slot >= 0 && slot < BaublesApi.getBaublesHandler(player).getSlots() ? BaublesApi.getBaublesHandler(player).getStackInSlot(slot) : ItemStack.EMPTY;
    }

    @SideOnly(Side.CLIENT)
    private EntityPlayer getClientPlayer() {
        return Minecraft.getMinecraft().player;
    }
}