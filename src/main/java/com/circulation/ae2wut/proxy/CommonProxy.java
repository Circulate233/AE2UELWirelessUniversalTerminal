package com.circulation.ae2wut.proxy;

import appeng.api.AEApi;
import appeng.api.config.Upgrades;
import appeng.api.util.AEPartLocation;
import appeng.container.AEBaseContainer;
import appeng.container.ContainerOpenContext;
import baubles.api.BaublesApi;
import com.circulation.ae2wut.AE2UELWirelessUniversalTerminal;
import com.circulation.ae2wut.handler.WirelessUniversalTerminalHandler;
import com.circulation.ae2wut.handler.WutRegisterHandler;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import com.circulation.ae2wut.recipes.AllWUTRecipe;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.jetbrains.annotations.Nullable;

public class CommonProxy implements IGuiHandler {

    protected static final Byte2ObjectMap<AE2UELWirelessUniversalTerminal.GetGui<? extends AEBaseContainer>> ContainerMap = new Byte2ObjectLinkedOpenHashMap<>();

    public CommonProxy() {
    }

    public void construction() {

    }

    public void preInit() {
        NetworkRegistry.INSTANCE.registerGuiHandler(AE2UELWirelessUniversalTerminal.MOD_ID, this);
        MinecraftForge.EVENT_BUS.register(new WirelessUniversalTerminalHandler());
    }

    public void init() {
        AEApi.instance().registries().wireless().registerWirelessHandler(ItemWirelessUniversalTerminal.INSTANCE);
        WutRegisterHandler.registerAllContainer();
    }

    public void postInit() {
        AllWUTRecipe.reciperRegister();
        Upgrades.MAGNET.registerItem(new ItemStack(ItemWirelessUniversalTerminal.INSTANCE), 1);
    }

    @Override
    public @Nullable Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        ItemStack terminal;
        final byte mode = (byte) ID;
        if (y == 0) {
            terminal = player.inventory.getStackInSlot(x);
        } else if (Loader.isModLoaded("baubles") && y == 1) {
            terminal = getBaubleItem(player, x);
        } else {
            terminal = ItemStack.EMPTY;
        }

        if (!terminal.isEmpty()) {
            if (terminal.getItem() instanceof ItemWirelessUniversalTerminal wut) {
                if (wut.hasMode(terminal, mode)) {
                    var bc = ContainerMap.get(mode).get(terminal, player, x, y);
                    if (bc != null) {
                        var containerOpenContext = new ContainerOpenContext(terminal);
                        containerOpenContext.setWorld(world);
                        containerOpenContext.setX(x);
                        containerOpenContext.setY(y);
                        containerOpenContext.setZ(z);
                        containerOpenContext.setSide(AEPartLocation.DOWN);
                        bc.setOpenContext(containerOpenContext);
                    }
                    return bc;
                }
            }
        }

        return null;
    }

    @Override
    public @Nullable Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    @Optional.Method(modid = "baubles")
    protected ItemStack getBaubleItem(EntityPlayer player, int slot) {
        return BaublesApi.getBaublesHandler(player).getStackInSlot(slot);
    }

    public void registryContainer(byte id, AE2UELWirelessUniversalTerminal.GetGui<? extends AEBaseContainer> function) {
        ContainerMap.put(id, function);
    }

    private final IntSet allModeSet = new IntOpenHashSet();

    public IntSet getAllModeSet() {
        if (allModeSet.size() == ContainerMap.size()) {
            return allModeSet;
        }

        for (Byte2ObjectMap.Entry<AE2UELWirelessUniversalTerminal.GetGui<? extends AEBaseContainer>> entry : ContainerMap.byte2ObjectEntrySet()) {
            if (!allModeSet.contains(entry.getByteKey())) {
                allModeSet.add(entry.getByteKey());
            }
        }

        return allModeSet;
    }

    public int[] getAllMode() {
        var size = ContainerMap.size();
        int[] modes = new int[size];

        int i = 0;

        for (Byte2ObjectMap.Entry<AE2UELWirelessUniversalTerminal.GetGui<? extends AEBaseContainer>> constructorEntry : ContainerMap.byte2ObjectEntrySet()) {
            modes[i++] = constructorEntry.getByteKey();
        }

        return modes;
    }
}