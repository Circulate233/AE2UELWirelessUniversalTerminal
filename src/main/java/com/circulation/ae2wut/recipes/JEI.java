package com.circulation.ae2wut.recipes;

import mezz.jei.api.*;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.plugins.vanilla.crafting.ShapelessRecipeWrapper;

@JEIPlugin
public class JEI implements IModPlugin {

    @Override
    public void register(final IModRegistry registry) {
        registry.handleRecipes(DynamicUniversalRecipe.class, recipe -> new ShapelessRecipeWrapper<>(registry.getJeiHelpers(), recipe), VanillaRecipeCategoryUid.CRAFTING);
    }

}
