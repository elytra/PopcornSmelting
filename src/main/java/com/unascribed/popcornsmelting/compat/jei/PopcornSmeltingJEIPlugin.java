package com.unascribed.popcornsmelting.compat.jei;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.unascribed.popcornsmelting.PopcornSmeltingRecipes;
import com.unascribed.popcornsmelting.PopcornSmeltingRecipes.PopcornSmeltingResult;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

@JEIPlugin
public class PopcornSmeltingJEIPlugin implements IModPlugin {

	@Override
	public void register(IModRegistry registry) {
		List<PopcornSmeltingRecipeWrapper> li = Lists.newArrayList();
		for (Map.Entry<ItemStack, PopcornSmeltingResult> en : PopcornSmeltingRecipes.all()) {
			li.add(new PopcornSmeltingRecipeWrapper(en.getKey(), en.getValue().stack));
		}
		if (PopcornSmeltingRecipes.isInherit()) {
			for (Map.Entry<ItemStack, ItemStack> en : FurnaceRecipes.instance().getSmeltingList().entrySet()) {
				li.add(new PopcornSmeltingRecipeWrapper(en.getKey(), en.getValue()));
			}
		}
		registry.addRecipes(li, "popcornsmelting");
	}
	
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		registry.addRecipeCategories(new PopcornSmeltingCategory(registry.getJeiHelpers().getGuiHelper()));
	}
	
}
