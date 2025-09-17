package com.circulation.ae2wut.handler;

import appeng.api.AEApi;
import appeng.api.features.IWirelessTermHandler;
import appeng.client.gui.AEBaseGui;
import appeng.client.gui.implementations.GuiWirelessCraftingTerminal;
import appeng.client.gui.implementations.GuiWirelessInterfaceTerminal;
import appeng.client.gui.implementations.GuiWirelessPatternTerminal;
import appeng.client.gui.implementations.GuiWirelessTerm;
import appeng.container.AEBaseContainer;
import appeng.container.implementations.ContainerWirelessCraftingTerminal;
import appeng.container.implementations.ContainerWirelessInterfaceTerminal;
import appeng.container.implementations.ContainerWirelessPatternTerminal;
import appeng.fluids.client.gui.GuiWirelessFluidTerminal;
import appeng.fluids.container.ContainerWirelessFluidTerminal;
import appeng.helpers.WirelessTerminalGuiObject;
import com._0xc4de.ae2exttable.client.container.wireless.ContainerAdvancedWirelessTerminal;
import com._0xc4de.ae2exttable.client.container.wireless.ContainerBasicWirelessTerminal;
import com._0xc4de.ae2exttable.client.container.wireless.ContainerEliteWirelessTerminal;
import com._0xc4de.ae2exttable.client.container.wireless.ContainerUltimateWirelessTerminal;
import com._0xc4de.ae2exttable.client.gui.WirelessTerminalGuiObjectTwo;
import com._0xc4de.ae2exttable.client.gui.wireless.GuiWirelessAdvancedCraftingTerm;
import com._0xc4de.ae2exttable.client.gui.wireless.GuiWirelessBasicCraftingTerm;
import com._0xc4de.ae2exttable.client.gui.wireless.GuiWirelessEliteCraftingTerm;
import com._0xc4de.ae2exttable.client.gui.wireless.GuiWirelessUltimateCraftingTerm;
import com._0xc4de.ae2exttable.items.ItemRegistry;
import com.circulation.ae2wut.AE2UELWirelessUniversalTerminal;
import com.circulation.ae2wut.client.model.ItemWUTBakedModel;
import com.glodblock.github.client.GuiWirelessFluidPatternTerminal;
import com.glodblock.github.client.container.ContainerWirelessFluidPatternTerminal;
import com.glodblock.github.loader.FCItems;
import com.mekeng.github.client.gui.GuiWirelessGasTerminal;
import com.mekeng.github.common.ItemAndBlocks;
import com.mekeng.github.common.container.ContainerWirelessGasTerminal;
import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class WutRegisterHandler {

    private static boolean modload(String name) {
        return Loader.isModLoaded(name);
    }

    public static void registerAllContainer() {
        registerAEContainer();
        if (modload("ae2fc")) registerAE2FCContainer();
        if (modload("mekeng")) registerMEKContainer();
        if (modload("ae2exttable")) registerAE2EContainer();
    }

    private static void registerAEContainer() {
        registerContainer(1, ContainerWirelessCraftingTerminal.class);
        registerContainer(2, ContainerWirelessFluidTerminal.class);
        registerContainer(3, ContainerWirelessPatternTerminal.class);
        registerContainer(10, ContainerWirelessInterfaceTerminal.class);
    }

    @Optional.Method(modid = "ae2fc")
    private static void registerAE2FCContainer() {
        registerContainer(4, ContainerWirelessFluidPatternTerminal.class);
    }

    @Optional.Method(modid = "mekeng")
    private static void registerMEKContainer() {
        registerContainer(5, ContainerWirelessGasTerminal.class);
    }

    @Optional.Method(modid = "ae2exttable")
    private static void registerAE2EContainer() {
        registerAE2EContainer(6, ContainerBasicWirelessTerminal.class);
        registerAE2EContainer(7, ContainerAdvancedWirelessTerminal.class);
        registerAE2EContainer(8, ContainerEliteWirelessTerminal.class);
        registerAE2EContainer(9, ContainerUltimateWirelessTerminal.class);
    }

    @Optional.Method(modid = "ae2exttable")
    private static Constructor<? extends AEBaseContainer> getAE2EContainer(Class<? extends AEBaseContainer> Class) {
        try {
            return Class.getConstructor(InventoryPlayer.class, WirelessTerminalGuiObjectTwo.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    @Optional.Method(modid = "ae2exttable")
    private static void registerAE2EContainer(int id, Class<? extends AEBaseContainer> constructor) {
        var c = getAE2EContainer(constructor);
        if (c != null) {
            AE2UELWirelessUniversalTerminal.instance.registryContainer(
                    (byte) id,
                    (item, player, slot, isBauble) -> {
                        var wth = getWirelessTerminalGuiObjectTwo(item, player, player.world, slot, isBauble);
                        if (wth != null) {
                            try {
                                return c.newInstance(player.inventory, wth);
                            } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    });
        }
    }

    private static Constructor<? extends AEBaseContainer> getConstructor(Class<? extends AEBaseContainer> Class) {
        try {
            return Class.getConstructor(InventoryPlayer.class, WirelessTerminalGuiObject.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static void registerContainer(int id, Class<? extends AEBaseContainer> constructor) {
        var c = getConstructor(constructor);
        if (c != null) {
            AE2UELWirelessUniversalTerminal.instance.registryContainer(
                    (byte) id,
                    (item, player, slot, isBauble) -> {
                        var wth = getWirelessTerminalGuiObject(item, player, player.world, slot, isBauble);
                        if (wth != null) {
                            try {
                                return c.newInstance(player.inventory, wth);
                            } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    });
        }
    }

    @Optional.Method(modid = "ae2exttable")
    private static WirelessTerminalGuiObjectTwo getWirelessTerminalGuiObjectTwo(ItemStack it, EntityPlayer player, World w, int x, int y) {
        if (!it.isEmpty()) {
            IWirelessTermHandler wh = AEApi.instance().registries().wireless().getWirelessTerminalHandler(it);
            if (wh != null) {
                return new WirelessTerminalGuiObjectTwo(wh, it, player, w, x, y, Integer.MIN_VALUE);
            }
        }

        return null;
    }

    private static WirelessTerminalGuiObject getWirelessTerminalGuiObject(ItemStack it, EntityPlayer player, World w, int x, int y) {
        if (!it.isEmpty()) {
            IWirelessTermHandler wh = AEApi.instance().registries().wireless().getWirelessTerminalHandler(it);
            if (wh != null) {
                return new WirelessTerminalGuiObject(wh, it, player, w, x, y, Integer.MIN_VALUE);
            }
        }

        return null;
    }

    @SideOnly(Side.CLIENT)
    public static class Client {

        private static final Object2ByteMap<Class<?>> map = new Object2ByteOpenHashMap<>();

        public static byte getGuiType(Class<?> c) {
            return map.getByte(c);
        }

        private static void regIcon(int id, ItemStack icon) {
            ItemWUTBakedModel.regIcon((byte) id, icon);
        }

        public static void registerAllGui() {
            map.defaultReturnValue(Byte.MIN_VALUE);
            registerAEGui();
            if (modload("ae2fc")) registerAE2FCGUI();
            if (modload("mekeng")) registerMEKGUI();
            if (modload("ae2exttable")) registerAE2EGUI();
        }

        private static void registerAEGui() {
            registerGui(0, GuiWirelessTerm.class);
            registerGui(1, GuiWirelessCraftingTerminal.class);
            registerGui(2, GuiWirelessFluidTerminal.class);
            registerGui(3, GuiWirelessPatternTerminal.class);
            registerGui(10, GuiWirelessInterfaceTerminal.class);

            var item = AEApi.instance().definitions().items();
            regIcon(0, item.wirelessTerminal().maybeStack(1).get());
            regIcon(1, item.wirelessCraftingTerminal().maybeStack(1).get());
            regIcon(2, item.wirelessFluidTerminal().maybeStack(1).get());
            regIcon(3, item.wirelessPatternTerminal().maybeStack(1).get());
            regIcon(10, item.wirelessInterfaceTerminal().maybeStack(1).get());
        }

        @Optional.Method(modid = "ae2fc")
        private static void registerAE2FCGUI() {
            registerGui(4, GuiWirelessFluidPatternTerminal.class);
            regIcon(4, new ItemStack(FCItems.WIRELESS_FLUID_PATTERN_TERMINAL));
        }

        @Optional.Method(modid = "mekeng")
        private static void registerMEKGUI() {
            registerGui(5, GuiWirelessGasTerminal.class);
            regIcon(5, new ItemStack(ItemAndBlocks.WIRELESS_GAS_TERMINAL));
        }

        @Optional.Method(modid = "ae2exttable")
        private static void registerAE2EGUI() {
            registerAE2EGUI(6, GuiWirelessBasicCraftingTerm.class, ContainerBasicWirelessTerminal.class);
            registerAE2EGUI(7, GuiWirelessAdvancedCraftingTerm.class, ContainerAdvancedWirelessTerminal.class);
            registerAE2EGUI(8, GuiWirelessEliteCraftingTerm.class, ContainerEliteWirelessTerminal.class);
            registerAE2EGUI(9, GuiWirelessUltimateCraftingTerm.class, ContainerUltimateWirelessTerminal.class);

            regIcon(6, new ItemStack(ItemRegistry.WIRELESS_BASIC_TERMINAL));
            regIcon(7, new ItemStack(ItemRegistry.WIRELESS_ADVANCED_TERMINAL));
            regIcon(8, new ItemStack(ItemRegistry.WIRELESS_ELITE_TERMINAL));
            regIcon(9, new ItemStack(ItemRegistry.WIRELESS_ULTIMATE_TERMINAL));
        }

        @Optional.Method(modid = "ae2exttable")
        private static Constructor<? extends AEBaseGui> getAE2EGui(Class<? extends AEBaseGui> Class, Class<? extends AEBaseContainer> aClass) {
            try {
                return Class.getConstructor(InventoryPlayer.class, WirelessTerminalGuiObject.class, aClass);
            } catch (NoSuchMethodException e) {
                return null;
            }
        }

        @Optional.Method(modid = "ae2exttable")
        private static void registerAE2EGUI(int id, Class<? extends AEBaseGui> constructor, Class<? extends AEBaseContainer> container) {
            map.put(constructor, (byte) id);
            var c = getAE2EContainer(container);
            var g = getAE2EGui(constructor, container);
            if (c != null && g != null) {
                AE2UELWirelessUniversalTerminal.instance.registryGui(
                        (byte) id,
                        (item, player, slot, isBauble) -> {
                            var wth = getWirelessTerminalGuiObject(item, player, player.world, slot, isBauble);
                            var wth2 = getWirelessTerminalGuiObjectTwo(item, player, player.world, slot, isBauble);
                            if (wth != null) {
                                try {
                                    return g.newInstance(player.inventory, wth, c.newInstance(player.inventory, wth2));
                                } catch (InvocationTargetException | IllegalAccessException |
                                         InstantiationException e) {
                                    return null;
                                }
                            } else {
                                return null;
                            }
                        });
            }
        }

        private static Constructor<? extends AEBaseGui> getGui(Class<? extends AEBaseGui> Class) {
            try {
                return Class.getConstructor(InventoryPlayer.class, WirelessTerminalGuiObject.class);
            } catch (NoSuchMethodException e) {
                return null;
            }
        }

        private static void registerGui(int id, Class<? extends AEBaseGui> constructor) {
            map.put(constructor, (byte) id);
            var c = getGui(constructor);
            if (c != null) {
                AE2UELWirelessUniversalTerminal.instance.registryGui(
                        (byte) id,
                        (item, player, slot, isBauble) -> {
                            var wth = getWirelessTerminalGuiObject(item, player, player.world, slot, isBauble);
                            if (wth != null) {
                                try {
                                    return c.newInstance(player.inventory, wth);
                                } catch (InvocationTargetException | IllegalAccessException |
                                         InstantiationException e) {
                                    return null;
                                }
                            } else {
                                return null;
                            }
                        });
            }
        }
    }
}