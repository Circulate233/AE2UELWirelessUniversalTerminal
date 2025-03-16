package com.circulation.ae2wut;

import com.circulation.ae2wut.network.UpdateItemModeMessage;
import com.circulation.ae2wut.network.WirelessTerminalRefresh;
import com.circulation.ae2wut.proxy.CommonProxy;
import net.minecraft.launchwrapper.LogWrapper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = "ae2wut", name = Tags.MOD_NAME, version = Tags.VERSION,
        dependencies = "required:mixinbooter@[8.0,);" +
                       "required:appliedenergistics2@[v0.56.7,);"
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

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        int start = 0;

        NET_CHANNEL.registerMessage(UpdateItemModeMessage.Handler.class,UpdateItemModeMessage.class, start++, Side.SERVER);
        NET_CHANNEL.registerMessage(WirelessTerminalRefresh.Handler.class, WirelessTerminalRefresh.class, start++, Side.SERVER);

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

}
