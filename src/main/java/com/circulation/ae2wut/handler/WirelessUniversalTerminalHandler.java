package com.circulation.ae2wut.handler;

import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class WirelessUniversalTerminalHandler {

    public static final WirelessUniversalTerminalHandler INSTANCE = new WirelessUniversalTerminalHandler();

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ItemWirelessUniversalTerminal.INSTANCE.nbtChangeB(event.player);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(ItemWirelessUniversalTerminal.INSTANCE);
    }

}