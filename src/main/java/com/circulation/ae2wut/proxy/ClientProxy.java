package com.circulation.ae2wut.proxy;

import appeng.client.gui.AEBaseGui;
import com.circulation.ae2wut.AE2UELWirelessUniversalTerminal;
import com.circulation.ae2wut.client.handler.WirelessUniversalTerminalHandler;
import com.circulation.ae2wut.handler.WutRegisterHandler;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import org.jetbrains.annotations.Nullable;

public class ClientProxy extends CommonProxy {

    protected static final Byte2ObjectMap<AE2UELWirelessUniversalTerminal.GetGui<? extends AEBaseGui>> GuiMap = new Byte2ObjectOpenHashMap<>();

    @Override
    public void construction() {
        super.construction();
    }

    @Override
    public void preInit() {
        super.preInit();
        MinecraftForge.EVENT_BUS.register(new WirelessUniversalTerminalHandler());
    }

    @Override
    public void init() {
        super.init();
        WutRegisterHandler.Client.registerAllGui();
    }

    @Override
    public void postInit() {
        super.postInit();
    }

    @Override
    public @Nullable Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        ItemStack terminal;
        final byte mode = (byte) ID;
        if (y == 0) {
            terminal = player.inventory.getStackInSlot(x);
        } else if (Loader.isModLoaded("baubles") && y == 1) {
            terminal = AE2UELWirelessUniversalTerminal.getBaubleItem(player, x);
        } else {
            terminal = ItemStack.EMPTY;
        }

        if (!terminal.isEmpty()) {
            if (terminal.getItem() instanceof ItemWirelessUniversalTerminal wut) {
                if (wut.hasMode(terminal, mode)) {
                    return GuiMap.get(mode).get(terminal, player, x, y);
                }
            }
        }

        return null;
    }

    public void registryGui(byte id, AE2UELWirelessUniversalTerminal.GetGui<? extends AEBaseGui> function) {
        GuiMap.put(id, function);
    }

    public boolean isRegistered(byte id) {
        return GuiMap.containsKey(id);
    }
}