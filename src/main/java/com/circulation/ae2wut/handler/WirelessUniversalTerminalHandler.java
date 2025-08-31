package com.circulation.ae2wut.handler;

import com.circulation.ae2wut.AE2UELWirelessUniversalTerminal;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import com.circulation.ae2wut.recipes.AllWUTRecipe;
import com.circulation.ae2wut.recipes.DynamicUniversalRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod.EventBusSubscriber(modid = AE2UELWirelessUniversalTerminal.MOD_ID)
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

    @SubscribeEvent
    public void onRegisterRecipes(RegistryEvent.Register<IRecipe> event) {
        AllWUTRecipe.reciperRegister();
        event.getRegistry().registerAll(DynamicUniversalRecipe.RECIPES.toArray(new DynamicUniversalRecipe[0]));
    }

}