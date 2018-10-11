package com.unascribed.popcornsmelting;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityFireproofXPOrb extends EntityXPOrb {

	public EntityFireproofXPOrb(World worldIn, double x, double y, double z, int expValue) {
		super(worldIn, x, y, z, expValue);
	}

	public EntityFireproofXPOrb(World worldIn) {
		super(worldIn);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.world.isRemote || this.isDead) return false;
		FireproofItemBehavior behavior = FireproofItemBehavior.forDamageSource(source);
		if (behavior != null && behavior.resistsFire()) {
			motionX = rand.nextGaussian()/8;
			motionY = 0.25f;
			motionZ = rand.nextGaussian()/8;
			markVelocityChanged();
			return false;
		}
		return super.attackEntityFrom(source, amount);
	}
	
	@Override
	public boolean isEntityInvulnerable(DamageSource source) {
		return source == DamageSource.ON_FIRE || super.isEntityInvulnerable(source);
	}
	
}
