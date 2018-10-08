package com.unascribed.popcornsmelting;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntitySmeltableItem extends EntityItem {

	private ItemStack result;
	
	public EntitySmeltableItem(World worldIn, double x, double y, double z, ItemStack stack) {
		super(worldIn, x, y, z, stack);
	}

	public EntitySmeltableItem(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}

	public EntitySmeltableItem(World worldIn) {
		super(worldIn);
	}
	
	public void setResult(ItemStack result) {
		this.result = result;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.world.isRemote || this.isDead) return false;
		if (source == DamageSource.LAVA || source == DamageSource.IN_FIRE) {
			if (result != null) {
				ItemStack copy = result.copy();
				EntityItem resultEnt = new EntitySmeltableItem(world, posX, posY, posZ, copy);
				resultEnt.setVelocity(rand.nextGaussian()/8, 0.25f, rand.nextGaussian()/8);
				resultEnt.setDefaultPickupDelay();
				setVelocity(rand.nextGaussian()/8, 0.25f, rand.nextGaussian()/8);
				world.spawnEntity(resultEnt);
				setFire(4);
				resultEnt.setFire(2);
				ItemStack remainder = getItem().copy();
				remainder.shrink(1);
				if (remainder.isEmpty()) {
					setDead();
				}
				setItem(remainder);
			}
			return false;
		}
		return super.attackEntityFrom(source, amount);
	}
	
	@Override
	public boolean isEntityInvulnerable(DamageSource source) {
		return source == DamageSource.ON_FIRE || super.isEntityInvulnerable(source);
	}
	
}
