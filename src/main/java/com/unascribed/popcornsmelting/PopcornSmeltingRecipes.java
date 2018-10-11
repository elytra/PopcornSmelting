package com.unascribed.popcornsmelting;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public class PopcornSmeltingRecipes {

	public static class PopcornSmeltingResult {
		public final ItemStack stack;
		public final float exp;
		
		public PopcornSmeltingResult(ItemStack stack, float exp) {
			this.stack = stack;
			this.exp = exp;
		}
	}
	
	public static final Map<ItemStack, PopcornSmeltingResult> recipes = Maps.newHashMap();
	
	public static PopcornSmeltingResult getResult(ItemStack stack) {
		for (Map.Entry<ItemStack, PopcornSmeltingResult> en : recipes.entrySet()) {
			if (itemMatchesTemplate(stack, en.getKey())) {
				return en.getValue();
			}
		}
		if (inherit) {
			ItemStack result = FurnaceRecipes.instance().getSmeltingResult(stack);
			if (!result.isEmpty()) {
				return new PopcornSmeltingResult(result, FurnaceRecipes.instance().getSmeltingExperience(result));
			}
		}
		return null;
	}
	
	private static boolean itemMatchesTemplate(ItemStack stack, ItemStack template) {
		return template.getItem() == stack.getItem() && (template.getMetadata() == 32767 || template.getMetadata() == stack.getMetadata());
	}
	
	public static void removeAll() {
		recipes.clear();
	}
	
	public static void addRecipe(ItemStack input, ItemStack output, float exp) {
		recipes.put(input, new PopcornSmeltingResult(output, exp));
	}
	
	public static Set<Map.Entry<ItemStack, PopcornSmeltingResult>> all() {
		return recipes.entrySet();
	}
	
	private static boolean inherit = true;
	
	public static void setInherit(boolean inherit) {
		PopcornSmeltingRecipes.inherit = inherit;
	}
	
	public static boolean isInherit() {
		return inherit;
	}

}
