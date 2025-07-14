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

public class UpdateItemModeMessage implements IMessage {
    private int slot;
    private byte mode;
    private boolean isBaubles;

    public UpdateItemModeMessage() {}

    public UpdateItemModeMessage(int slot, byte mode,boolean isBaubles) {
        this.slot = slot;
        this.mode = mode;
        this.isBaubles = isBaubles;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slot = buf.readInt();
        mode = buf.readByte();
        isBaubles = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slot);
        buf.writeByte(mode);
        buf.writeBoolean(isBaubles);
    }

    public int getSlot(){
        return slot;
    }

    public byte getMode() {
        return mode;
    }

    public boolean isBaubles() {
        return isBaubles;
    }

    public static class Handler implements IMessageHandler<UpdateItemModeMessage, IMessage>{

        @Override
        public IMessage onMessage(UpdateItemModeMessage message, MessageContext ctx) {
            EntityPlayer player = switch (ctx.side){
                case SERVER -> ctx.getServerHandler().player;
                case CLIENT -> getClientPlayer();
            };

            ItemStack stack = ItemStack.EMPTY;

            if (!message.isBaubles())
                stack = player.inventory.getStackInSlot(message.getSlot());
            else if (message.isBaubles() && Loader.isModLoaded("baubles"))
                stack = getStackInBaubleSlot(player, message.getSlot());

            if (stack.getItem() instanceof ItemWirelessUniversalTerminal && stack.getTagCompound() != null) {
                stack.getTagCompound().setInteger("mode", message.getMode());
            }
            return null;
        }

        @Optional.Method(modid = "baubles")
        private ItemStack getStackInBaubleSlot(EntityPlayer player,int slot){
            return slot >= 0 && slot < BaublesApi.getBaublesHandler(player).getSlots() ? BaublesApi.getBaublesHandler(player).getStackInSlot(slot) : ItemStack.EMPTY;
        }

        @SideOnly(Side.CLIENT)
        private EntityPlayer getClientPlayer(){
            return Minecraft.getMinecraft().player;
        }
    }
}