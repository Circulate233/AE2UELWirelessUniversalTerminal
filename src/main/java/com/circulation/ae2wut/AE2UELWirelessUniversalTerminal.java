package com.circulation.ae2wut;

import appeng.api.AEApi;
import appeng.api.features.ILocatable;
import appeng.api.features.IWirelessTermHandler;
import appeng.api.features.IWirelessTermRegistry;
import appeng.client.gui.AEBaseGui;
import appeng.container.AEBaseContainer;
import appeng.core.localization.PlayerMessages;
import appeng.helpers.WirelessTerminalGuiObject;
import baubles.api.BaublesApi;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import com.circulation.ae2wut.network.OpenWUTGui;
import com.circulation.ae2wut.network.UpdateItemModeMessage;
import com.circulation.ae2wut.network.WirelessTerminalRefresh;
import com.circulation.ae2wut.proxy.ClientProxy;
import com.circulation.ae2wut.proxy.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.LogWrapper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "ae2wut", name = Tags.MOD_NAME, version = Tags.VERSION,
        dependencies = "required-after:mixinbooter@[8.0,);" +
                "required-after:appliedenergistics2@[v0.56.7,);" +
                "after:ae2exttable@[v1.0.8,);"
)
public class AE2UELWirelessUniversalTerminal {

    public static final String MOD_ID = "ae2wut";
    public static final String CLIENT_PROXY = "com.circulation.ae2wut.proxy.ClientProxy";
    public static final String COMMON_PROXY = "com.circulation.ae2wut.proxy.CommonProxy";

    public static final SimpleNetworkWrapper NET_CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID);

    public static final Logger LOGGER = LogManager.getLogger(Tags.MOD_NAME);

    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = COMMON_PROXY)
    public static CommonProxy proxy = null;

    @Mod.Instance(MOD_ID)
    public static AE2UELWirelessUniversalTerminal instance = null;
    public static LogWrapper logger;

    @SideOnly(Side.CLIENT)
    public static void openWirelessTerminalGui(WirelessTerminalGuiObject obj, byte mode) {
        ItemStack stack = ItemStack.EMPTY;
        if (!obj.isBaubleSlot()) {
            stack = Minecraft.getMinecraft().player.inventory.getStackInSlot(obj.getInventorySlot());
        } else if (Loader.isModLoaded("baubles") && obj.isBaubleSlot()) {
            stack = getBaubleItem(Minecraft.getMinecraft().player, obj.getInventorySlot());
        }
        if (!stack.isEmpty()) {
            Minecraft.getMinecraft().player.closeScreen();
            ItemWirelessUniversalTerminal.INSTANCE.nbtChangeB(stack);
            ItemWirelessUniversalTerminal.INSTANCE.nbtChange(stack, mode);
            NET_CHANNEL.sendToServer(new OpenWUTGui(obj, mode));
        }
    }

    @Optional.Method(modid = "baubles")
    public static ItemStack getBaubleItem(EntityPlayer player, int slot) {
        return BaublesApi.getBaublesHandler(player).getStackInSlot(slot);
    }

    public static void openWirelessTerminalGui(ItemStack terminal, EntityPlayer player, int mode, int slot, boolean isBauble) {
        if (player instanceof EntityPlayerMP playerMP) {
            IWirelessTermRegistry registry = AEApi.instance().registries().wireless();
            if (!registry.isWirelessTerminal(terminal)) {
                player.sendMessage(PlayerMessages.DeviceNotWirelessTerminal.get());
            } else {
                IWirelessTermHandler handler = registry.getWirelessTerminalHandler(terminal);
                String unparsedKey = handler.getEncryptionKey(terminal);
                if (unparsedKey.isEmpty()) {
                    player.sendMessage(PlayerMessages.DeviceNotLinked.get());
                } else {
                    long parsedKey = Long.parseLong(unparsedKey);
                    ILocatable securityStation = AEApi.instance().registries().locatable().getLocatableBy(parsedKey);
                    if (securityStation == null) {
                        player.sendMessage(PlayerMessages.StationCanNotBeLocated.get());
                    } else {
                        if (handler.hasPower(player, 0.5F, terminal)) {
                            playerMP.getServer().addScheduledTask(() -> {
                                ItemWirelessUniversalTerminal.INSTANCE.nbtChangeB(terminal);
                                ItemWirelessUniversalTerminal.INSTANCE.nbtChange(terminal, (byte) mode);
                                player.openGui(AE2UELWirelessUniversalTerminal.instance, mode, player.world, slot, isBauble ? 1 : 0, Integer.MIN_VALUE);
                            });
                        } else {
                            player.sendMessage(PlayerMessages.DeviceNotPowered.get());
                        }
                    }
                }
            }
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        int start = 0;

        NET_CHANNEL.registerMessage(UpdateItemModeMessage.class, UpdateItemModeMessage.class, start++, Side.SERVER);
        NET_CHANNEL.registerMessage(WirelessTerminalRefresh.class, WirelessTerminalRefresh.class, start++, Side.SERVER);
        NET_CHANNEL.registerMessage(OpenWUTGui.class, OpenWUTGui.class, start++, Side.SERVER);

        NET_CHANNEL.registerMessage(UpdateItemModeMessage.class, UpdateItemModeMessage.class, start++, Side.CLIENT);

        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit();
    }

    public void registryContainer(byte id, GetGui<? extends AEBaseContainer> targetContainer) {
        proxy.registryContainer(id, targetContainer);
    }

    @SideOnly(Side.CLIENT)
    public void registryGui(byte id, GetGui<? extends AEBaseGui> targetGui) {
        ((ClientProxy) proxy).registryGui(id, targetGui);
    }

    @FunctionalInterface
    public interface GetGui<T> {
        T get(ItemStack item, EntityPlayer player, int slot, int isBauble);
    }
}