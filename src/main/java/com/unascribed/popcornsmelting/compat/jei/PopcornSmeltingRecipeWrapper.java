package com.unascribed.popcornsmelting.compat.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

public class PopcornSmeltingRecipeWrapper implements IRecipeWrapper {

	public final ItemStack input;
	public final ItemStack output;
	
	public PopcornSmeltingRecipeWrapper(ItemStack input, ItemStack output) {
		this.input = input;
		this.output = output;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInput(ItemStack.class, input);
		ingredients.setOutput(ItemStack.class, output);
	}

}
