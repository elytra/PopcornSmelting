package com.unascribed.popcornsmelting;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public enum FireproofKind {
	ALL,
	ALL_BUT_FUELS,
	SMELTABLE_ONLY,
	;
	
	public boolean shouldFireproof(ItemStack item) {
		switch (this) {
			case ALL: return true;
			case ALL_BUT_FUELS: return TileEntityFurnace.getItemBurnTime(item) <= 0;
			case SMELTABLE_ONLY: return PopcornSmeltingRecipes.getResult(item) != null;
			default: throw new AssertionError("missing case for "+this);
		}
	}
}
