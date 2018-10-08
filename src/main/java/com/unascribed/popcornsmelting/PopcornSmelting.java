package com.unascribed.popcornsmelting;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@Mod(modid=PopcornSmelting.MODID, name=PopcornSmelting.NAME, version=PopcornSmelting.VERSION, acceptableRemoteVersions="*")
public class PopcornSmelting {

	public static final String MODID = "popcornsmelting";
	public static final String NAME = "Popcorn Smelting";
	public static final String VERSION = "@VERSION@";
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		EntityRegistry.registerModEntity(new ResourceLocation(MODID, "smeltable_item"), EntitySmeltableItem.class, "smeltabled_item", 0, this, 64, 4, true);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent e) {
		if (e.getEntity() instanceof EntityItem && !(e.getEntity() instanceof EntitySmeltableItem)) {
			EntityItem ei = (EntityItem)e.getEntity();
			ItemStack is = ei.getItem();
			ItemStack result = FurnaceRecipes.instance().getSmeltingResult(is);
			if (!result.isEmpty()) {
				e.setCanceled(true);
				EntitySmeltableItem esi = new EntitySmeltableItem(ei.world, ei.posX, ei.posY, ei.posZ, is);
				// TODO keep field lookup cached
				esi.setPickupDelay(ReflectionHelper.getPrivateValue(EntityItem.class, ei, "field_145804_b", "pickupDelay"));
				ReflectionHelper.setPrivateValue(EntityItem.class, esi, ei.getAge(), "field_70292_b", "age");
				esi.setResult(result);
				esi.setVelocity(ei.motionX, ei.motionY, ei.motionZ);
				ei.world.spawnEntity(esi);
			}
		}
	}
	
}
