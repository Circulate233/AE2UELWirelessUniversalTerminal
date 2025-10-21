package com.circulation.ae2wut.recipes;

import com.circulation.ae2wut.AE2UELWirelessUniversalTerminal;
import com.circulation.ae2wut.item.ItemWirelessUniversalTerminal;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.Arrays;
import java.util.List;

import static com.circulation.ae2wut.recipes.AllWUTRecipe.itemList;

public class DynamicUniversalRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    public static final List<DynamicUniversalRecipe> RECIPES = registerRecipes();
    private final ItemStack inputTerminal = new ItemStack(ItemWirelessUniversalTerminal.INSTANCE);
    private final ItemStack inputTerminalOut = new ItemStack(ItemWirelessUniversalTerminal.INSTANCE);
    private final ItemStack additionalItem;
    private final int mode;

    public DynamicUniversalRecipe(ItemStack additionalItem, int mode) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("mode", mode);
        nbt.setIntArray("modes", new int[]{mode});
        this.inputTerminalOut.setTagCompound(nbt);
        this.additionalItem = additionalItem;
        this.mode = mode;
        this.setRegistryName(new ResourceLocation(AE2UELWirelessUniversalTerminal.MOD_ID, "universal" + mode));
    }

    private static List<DynamicUniversalRecipe> registerRecipes() {
        List<DynamicUniversalRecipe> RECIPES = new ObjectArrayList<>();
        itemList.forEach((mode, item) -> RECIPES.add(new DynamicUniversalRecipe(item, mode)));
        return RECIPES;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        boolean foundTerminal = false;
        boolean foundItem = false;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() == inputTerminal.getItem()) {
                    if (foundTerminal) return false;
                    foundTerminal = true;
                } else if (additionalItem.getItem() == stack.getItem()) {
                    if (foundItem) return false;
                    foundItem = true;
                } else {
                    return false;
                }
            }
        }
        return foundTerminal && foundItem;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack terminal = ItemStack.EMPTY;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.getItem() == inputTerminal.getItem()) {
                terminal = stack.copy();
                break;
            }
        }

        if (terminal.isEmpty()) return ItemStack.EMPTY;

        NBTTagCompound tag = terminal.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            terminal.setTagCompound(tag);
        }

        int[] modes = null;
        if (tag.hasKey("modes", 11)) {
            modes = tag.getIntArray("modes");
        }

        if (modes != null) {
            for (int existingMode : modes) {
                if (existingMode == mode) {
                    return ItemStack.EMPTY;
                }
            }
        }

        int[] newModes = Arrays.copyOf(modes, modes.length + 1);
        newModes[newModes.length - 1] = mode;
        tag.setTag("modes", new NBTTagIntArray(newModes));

        return terminal;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return inputTerminalOut.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        final NonNullList<Ingredient> list = NonNullList.create();
        list.add(Ingredient.fromStacks(inputTerminal));
        list.add(Ingredient.fromStacks(additionalItem));
        return list;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}