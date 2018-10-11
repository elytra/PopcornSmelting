package com.unascribed.popcornsmelting.compat.crafttweaker;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.unascribed.popcornsmelting.PopcornSmeltingRecipes;
import com.unascribed.popcornsmelting.PopcornSmeltingRecipes.PopcornSmeltingResult;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import crafttweaker.api.recipes.FurnaceRecipe;
import crafttweaker.api.recipes.IFurnaceRecipe;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.popcornsmelting")
@ZenRegister
public class PopcornSmeltingTweaker {

	@ZenMethod
	public static void remove(IIngredient output, @Optional IIngredient input) {
		if (output == null) throw new IllegalArgumentException("output cannot be null");
		Iterator<Map.Entry<ItemStack, PopcornSmeltingResult>> iter = PopcornSmeltingRecipes.all().iterator();
		int count = 0;
		while (iter.hasNext()) {
			Map.Entry<ItemStack, PopcornSmeltingResult> en = iter.next();
			if (output.matches(CraftTweakerMC.getIItemStack(en.getValue().stack)) && (input == null || input.matches(CraftTweakerMC.getIItemStack(en.getValue().stack)))) {
				iter.remove();
				count++;
			}
		}
		CraftTweakerAPI.logInfo(count+" popcorn smelting recipes removed for: "+output);
	}

	@ZenMethod
	public static void removeAll() {
		PopcornSmeltingRecipes.setInherit(false);
		PopcornSmeltingRecipes.removeAll();
	}

	@ZenMethod
	public static void addRecipe(IItemStack ioutput, IIngredient iinput, @Optional double xp) {
		List<IItemStack> iitems = iinput.getItems();
		if (iitems == null) {
			CraftTweakerAPI.logError("Cannot turn "+iinput +" into a popcorn smelting recipe");
		}
		ItemStack[] items = CraftTweakerMC.getItemStacks(iitems);
		ItemStack output = CraftTweakerMC.getItemStack(ioutput);
		for (ItemStack is : items) {
			PopcornSmeltingRecipes.addRecipe(is, output, (float)xp);
		}
	}

	@ZenGetter("all")
	public static List<IFurnaceRecipe> getAll() {
		List<IFurnaceRecipe> li = Lists.newArrayList();
		for (Map.Entry<ItemStack, PopcornSmeltingResult> en : PopcornSmeltingRecipes.all()) {
			li.add(new FurnaceRecipe(CraftTweakerMC.getIItemStack(en.getKey()), CraftTweakerMC.getIItemStack(en.getValue().stack), en.getValue().exp));
		}
		if (PopcornSmeltingRecipes.isInherit()) {
			li.addAll(CraftTweakerAPI.furnace.getAll());
		}
		return li;
	}

}
