package com.circulation.ae2wut.recipes;

import appeng.api.AEApi;
import appeng.api.definitions.IDefinitions;
import appeng.api.definitions.IItems;
import appeng.util.Platform;
import com._0xc4de.ae2exttable.items.ItemRegistry;
import com.blakebr0.extendedcrafting.crafting.table.TableRecipeManager;
import com.circulation.ae2wut.AE2UELWirelessUniversalTerminal;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import com.glodblock.github.loader.FCItems;
import com.mekeng.github.common.ItemAndBlocks;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.List;

import static com.circulation.ae2wut.item.ItemWirelessUniversalTerminal.NAME;

public class AllWUTRecipe {

    static IDefinitions appEngApi = AEApi.instance().definitions();
    static IItems AEItems = appEngApi.items();
    static ItemStack ItemWireless = new ItemStack(ItemWirelessUniversalTerminal.INSTANCE);

    private static NBTTagCompound getNBT() {
        NBTTagCompound nbt = Platform.openNbtData(ItemWireless);
        nbt.setIntArray("modes", AE2UELWirelessUniversalTerminal.proxy.getAllMode());
        return nbt;
    }

    public static final Int2ObjectMap<ItemStack> itemList = getIngredient();

    private static Int2ObjectMap<ItemStack> getIngredient() {
        Int2ObjectMap<ItemStack> map = new Int2ObjectOpenHashMap<>();
        map.put(1, AEItems.wirelessCraftingTerminal().maybeStack(1).get());
        map.put(2, AEItems.wirelessFluidTerminal().maybeStack(1).get());
        map.put(3, AEItems.wirelessPatternTerminal().maybeStack(1).get());
        map.put(10, AEItems.wirelessInterfaceTerminal().maybeStack(1).get());

        if (Loader.isModLoaded("ae2fc")) {
            addAE2FC(map);
        }

        if (Loader.isModLoaded("mekeng")) {
            addMEKEng(map);
        }

        if (Loader.isModLoaded("ae2exttable")) {
            addAE2Exttable(map);
        }

        return map;
    }

    @Optional.Method(modid = "ae2fc")
    private static void addAE2FC(Int2ObjectMap<ItemStack> map) {
        map.put(4, new ItemStack(FCItems.WIRELESS_FLUID_PATTERN_TERMINAL));
    }

    @Optional.Method(modid = "mekeng")
    private static void addMEKEng(Int2ObjectMap<ItemStack> map) {
        map.put(5, new ItemStack(ItemAndBlocks.WIRELESS_GAS_TERMINAL));
    }

    @Optional.Method(modid = "ae2exttable")
    private static void addAE2Exttable(Int2ObjectMap<ItemStack> map) {
        map.put(6, new ItemStack(ItemRegistry.WIRELESS_BASIC_TERMINAL));
        map.put(7, new ItemStack(ItemRegistry.WIRELESS_ADVANCED_TERMINAL));
        map.put(8, new ItemStack(ItemRegistry.WIRELESS_ELITE_TERMINAL));
        map.put(9, new ItemStack(ItemRegistry.WIRELESS_ULTIMATE_TERMINAL));
    }

    public static void reciperRegister(){
        List<Ingredient> inputs = new ObjectArrayList<>();
        for (ItemStack item : itemList.values()){
            inputs.add(Ingredient.fromStacks(item));
        }
        GameRegistry.addShapedRecipe(
                new ResourceLocation(AE2UELWirelessUniversalTerminal.MOD_ID, NAME),
                null,
                ItemWireless,
                "ABC",
                'A', appEngApi.materials().wirelessReceiver().maybeStack(1).get(),
                'B', appEngApi.parts().terminal().maybeStack(1).get(),
                'C', appEngApi.blocks().energyCellDense().maybeStack(1).get()
        );
        GameRegistry.addShapelessRecipe(
                new ResourceLocation(AE2UELWirelessUniversalTerminal.MOD_ID, NAME + 1),
                null,
                ItemWireless,
                Ingredient.fromItem(AEItems.wirelessTerminal().maybeItem().get())
        );
        ItemStack ItemWirelessALL = ItemWireless.copy();
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setIntArray("modes",AE2UELWirelessUniversalTerminal.proxy.getAllMode());
        ItemWirelessALL.setTagCompound(nbt);
        if (inputs.size() < 10) {
            GameRegistry.addShapelessRecipe(
                    new ResourceLocation(AE2UELWirelessUniversalTerminal.MOD_ID, NAME + "all"),
                    null,
                    ItemWirelessALL,
                    inputs.toArray(new Ingredient[0])
            );
        } else if (Loader.isModLoaded("extendedcrafting")){
            extendedcraftingRecipe(inputs,ItemWirelessALL);
        }
    }

    @Optional.Method(modid = "extendedcrafting")
    public static void extendedcraftingRecipe(List<Ingredient> input,ItemStack outpput){
        NonNullList<Ingredient> inputs = NonNullList.create();
        inputs.addAll(input);
        TableRecipeManager.getInstance().addShapeless(outpput,inputs);
    }
}
