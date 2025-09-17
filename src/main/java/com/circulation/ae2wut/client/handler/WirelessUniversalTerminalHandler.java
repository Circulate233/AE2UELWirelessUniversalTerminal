package com.circulation.ae2wut.client.handler;

import appeng.client.gui.AEBaseGui;
import com.circulation.ae2wut.AE2UELWirelessUniversalTerminal;
import com.circulation.ae2wut.handler.GuiHandler;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

@SideOnly(Side.CLIENT)
public class WirelessUniversalTerminalHandler {
    private GuiScreen gui;
    private final Minecraft mc = FMLClientHandler.instance().getClient();

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.getGui() != null) {
            if (event.getGui() instanceof AEBaseGui) {
                gui = event.getGui();
            } else {
                gui = null;
            }
        } else {
            GuiHandler.clearCache();
            if (gui != null) {
                AE2UELWirelessUniversalTerminal.NET_CHANNEL.sendToServer(new WirelessTerminalRefresh());
            }
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
            int delta = -Mouse.getEventDWheel();
            if (delta % 120 == 0) {
                delta = delta / 120;
            }
            if (delta != 0 && stack.getItem() instanceof ItemWirelessUniversalTerminal) {
                final var tag = stack.getTagCompound();
                if (tag == null) return;

                IntList list;
                if (tag.hasKey("modes", 11)) {
                    var modes = tag.getIntArray("modes");
                    if (modes.length == 0) return;
                    list = new IntArrayList(modes);
                    list.rem(0);
                    if (list.isEmpty()) return;
                } else
                    return;

                int[] modes = list.toIntArray();

                int index = list.indexOf(tag.getInteger("mode"));
                if (index < 0) index = 0;
                int newVal = Math.floorMod(index + delta, modes.length);

                if (newVal < 0 || newVal >= modes.length) {
                    newVal = 0;
                }

                tag.setInteger("mode", modes[newVal]);
                mc.player.sendStatusMessage(new TextComponentString(stack.getDisplayName()), true);
                AE2UELWirelessUniversalTerminal.NET_CHANNEL.sendToServer(new UpdateItemModeMessage(mc.player.inventory.currentItem, modes[newVal], false));

                event.setCanceled(true);
            }
        }
    }
}