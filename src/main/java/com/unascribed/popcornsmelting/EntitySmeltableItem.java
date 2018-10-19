package com.unascribed.popcornsmelting;

import com.unascribed.popcornsmelting.PopcornSmeltingRecipes.PopcornSmeltingResult;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntitySmeltableItem extends EntityItem {

	private PopcornSmeltingResult result;
	private int bounces = PopcornSmelting.bouncesToSmelt == 0 ? 0 : -1;
	private int cooldown = 0;
	
	public EntitySmeltableItem(World worldIn, double x, double y, double z, ItemStack stack) {
		super(worldIn, x, y, z, stack);
	}

	public EntitySmeltableItem(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}

	public EntitySmeltableItem(World worldIn) {
		super(worldIn);
	}
	
	public void setResult(PopcornSmeltingResult result) {
		this.result = result;
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		if (cooldown > 0) cooldown--;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (this.world.isRemote || this.isDead) return false;
		FireproofItemBehavior behavior = FireproofItemBehavior.forDamageSource(source);
		if (behavior != null && behavior.resistsFire()) {
			motionX = rand.nextGaussian()/10;
			motionY = (bounces > PopcornSmelting.bouncesToBounceHigher ? 0.45f : 0.2f);
			motionZ = rand.nextGaussian()/10;
			markVelocityChanged();
			bounces++;
			if (result != null && behavior.canSmelt()) {
				world.playSound(null, posX, posY, posZ, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
				if (cooldown <= 0) {
					cooldown = 10;
					EntitySmeltableItem resultEnt = new EntitySmeltableItem(world, posX, posY, posZ);
					if (getItem().getCount() > 1) {
						ItemStack is = getItem().copy();
						is.setCount(1);
						resultEnt.setItem(is);
						resultEnt.setResult(result);
						resultEnt.bounces = 0;
						bounces = 0;
					} else if (bounces >= PopcornSmelting.bouncesToSmelt) {
						resultEnt.setItem(result.stack.copy());
						int totalExp = 1;
	
						if (result.exp == 0) {
							totalExp = 0;
						} else if (result.exp < 1) {
							totalExp = MathHelper.floor(totalExp * result.exp);
							if (totalExp < MathHelper.ceil(totalExp * result.exp) && Math.random() < (totalExp * result.exp - totalExp)) {
								totalExp += 1;
							}
						}
						
						while (totalExp > 0) {
							int amt = EntityXPOrb.getXPSplit(totalExp);
							totalExp -= amt;
							world.spawnEntity(new EntityFireproofXPOrb(world, posX, posY, posZ, amt));
						}
					} else {
						return false;
					}
					resultEnt.motionX = rand.nextGaussian()/8;
					resultEnt.motionY = 0.15f;
					resultEnt.motionZ = rand.nextGaussian()/8;
					resultEnt.setDefaultPickupDelay();
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
			}
			return false;
		}
		return super.attackEntityFrom(source, amount);
	}
	
	@Override
	public boolean combineItems(EntityItem other) {
		if (bounces > -1 && result != null) return false;
		if (other.isBurning() || this.isBurning()) return false;
		return super.combineItems(other);
	}
	
	@Override
	public boolean isEntityInvulnerable(DamageSource source) {
		return source == DamageSource.ON_FIRE || super.isEntityInvulnerable(source);
	}
	
}
