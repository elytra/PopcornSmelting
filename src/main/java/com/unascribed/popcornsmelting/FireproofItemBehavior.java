package com.unascribed.popcornsmelting;

import javax.annotation.Nullable;

import net.minecraft.util.DamageSource;

public enum FireproofItemBehavior {
	SMELT,
	BOUNCE,
	BURN,
	;
	
	public boolean resistsFire() {
		return this == SMELT || this == BOUNCE;
	}
	
	public boolean canSmelt() {
		return this == SMELT;
	}
	
	@Nullable
	public static FireproofItemBehavior forDamageSource(DamageSource ds) {
		if (ds == DamageSource.LAVA) return PopcornSmelting.lavaBehavior;
		if (ds == DamageSource.IN_FIRE) return PopcornSmelting.fireBehavior;
		return null;
	}
	
}
