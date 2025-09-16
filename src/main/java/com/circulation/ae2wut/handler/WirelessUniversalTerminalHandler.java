package com.circulation.ae2wut.handler;

import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import com.circulation.ae2wut.recipes.DynamicUniversalRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class WirelessUniversalTerminalHandler {

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ItemWirelessUniversalTerminal.INSTANCE.nbtChangeB(event.player);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(ItemWirelessUniversalTerminal.INSTANCE);
    }

    @SubscribeEvent
    public void onRegisterRecipes(RegistryEvent.Register<IRecipe> event) {
        event.getRegistry().registerAll(DynamicUniversalRecipe.RECIPES.toArray(new DynamicUniversalRecipe[0]));
    }

}