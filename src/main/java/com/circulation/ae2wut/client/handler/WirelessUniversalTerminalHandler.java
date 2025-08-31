package com.circulation.ae2wut.client.handler;

import appeng.client.gui.AEBaseGui;
import com.circulation.ae2wut.AE2UELWirelessUniversalTerminal;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import com.circulation.ae2wut.network.UpdateItemModeMessage;
import com.circulation.ae2wut.network.WirelessTerminalRefresh;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class WirelessUniversalTerminalHandler {

    public static final WirelessUniversalTerminalHandler INSTANCE = new WirelessUniversalTerminalHandler();
    private GuiScreen gui;
    private final Minecraft mc = FMLClientHandler.instance().getClient();

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.getGui() != null){
            if (event.getGui() instanceof AEBaseGui){
                gui = event.getGui();
            } else {
                gui = null;
            }
        } else if (gui != null){
            AE2UELWirelessUniversalTerminal.NET_CHANNEL.sendToServer(new WirelessTerminalRefresh());
        }
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        ItemWirelessUniversalTerminal item = ItemWirelessUniversalTerminal.INSTANCE;
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }

    @SubscribeEvent
    public void onMouseEvent(MouseEvent event) {
        if (mc.player != null && mc.player.isSneaking()) {
            ItemStack stack = mc.player.getHeldItemMainhand();
            int delta = - Mouse.getEventDWheel();
            if (delta % 120 == 0){
                delta = delta / 120 ;
            }
            if (delta != 0 && stack.getItem() instanceof ItemWirelessUniversalTerminal) {
                final var tag = stack.getTagCompound();
                if (tag != null) {
                    IntList list = null;
                    if (tag.hasKey("modes")) {
                        list = new IntArrayList(tag.getIntArray("modes"));
                        if (!list.contains(0)){
                            list.add(0);
                        }
                    }
                    if (list != null) {
                        int[] modes = stack.getTagCompound().getIntArray("modes");
                        int max = Integer.MIN_VALUE;
                        for (int mode : modes) {
                            if (mode > max) max = mode;
                        }
                        final int listMax = modes.length > 0 ? max + 1 : 1;

                        int newVal = (tag.getInteger("mode") + delta) % listMax;

                        while (!list.contains(newVal)) {
                            if (newVal < 0) {
                                newVal = newVal + listMax;
                                break;
                            }
                            if (delta > 0){
                                newVal = (newVal + 1) % listMax;
                            } else {
                                newVal = (newVal - 1) % listMax;
                            }
                        }

                        tag.setInteger("mode", newVal);
                        mc.player.sendStatusMessage(new TextComponentString(stack.getDisplayName()),true);
                        AE2UELWirelessUniversalTerminal.NET_CHANNEL.sendToServer(new UpdateItemModeMessage(mc.player.inventory.currentItem,(byte) newVal,false));

                        event.setCanceled(true);
                    }
                }
            }
        }
    }
}
