package com.unascribed.popcornsmelting;

import java.lang.reflect.Field;

import org.apache.commons.logging.LogFactory;

import com.unascribed.popcornsmelting.PopcornSmeltingRecipes.PopcornSmeltingResult;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
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
	
	private static final Field pickupDelay = ReflectionHelper.findField(EntityItem.class, "field_145804_b", "pickupDelay");
	private static final Field age = ReflectionHelper.findField(EntityItem.class, "field_70292_b", "age");
	static {
		pickupDelay.setAccessible(true);
		age.setAccessible(true);
	}
	
	private boolean fireproofAll = false;

	private static final String INHERIT_DESC =
			"If true, all furnace recipes will be valid popcorn smelting recipes.\n" +
			"If false, there will be no popcorn smelting recipes. You will have\n" +
			"to add some, e.g. with CraftTweaker.\n" +
			"\n" +
			"When true, popcorn smelting specific recipes can still be added, and\n" +
			"they will take precedence over normal smelting recipes.\n" +
			"\n" +
			"Calling removeAll on the smelting registry from CraftTweaker will\n" +
			"implicitly set this config setting to false for that session.";
	
	private static final String FIREPROOF_DESC = 
			"If true, all items will be made fireproof, instead of just items that\n" +
			"can be popcorn smelted.";
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		Configuration cfg = new Configuration(e.getSuggestedConfigurationFile());
		cfg.load();
		PopcornSmeltingRecipes.setInherit(cfg.getBoolean("inheritFurnaceRecipes", "General", true, INHERIT_DESC));
		fireproofAll = cfg.getBoolean("fireproofAllItems", "General", false, FIREPROOF_DESC);
		cfg.save();
		EntityRegistry.registerModEntity(new ResourceLocation(MODID, "smeltable_item"), EntitySmeltableItem.class, "smeltable_item", 0, this, 64, 1, true);
		EntityRegistry.registerModEntity(new ResourceLocation(MODID, "fireproof_xp_orb"), EntityFireproofXPOrb.class, "fireproof_xp_orb", 1, this, 64, 4, true);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent e) {
		if (e.getEntity() instanceof EntityItem && !(e.getEntity() instanceof EntitySmeltableItem)) {
			EntityItem ei = (EntityItem)e.getEntity();
			ItemStack is = ei.getItem();
			PopcornSmeltingResult res = PopcornSmeltingRecipes.getResult(is);
			if (res != null || fireproofAll) {
				try {
					e.setCanceled(true);
					EntitySmeltableItem esi = new EntitySmeltableItem(ei.world, ei.posX, ei.posY, ei.posZ, is);
					esi.setPickupDelay(pickupDelay.getInt(ei));
					age.set(esi, age.getInt(ei));
					esi.setResult(res);
					esi.motionX = ei.motionX;
					esi.motionY = ei.motionY;
					esi.motionZ = ei.motionZ;
					ei.world.spawnEntity(esi);
				} catch (Throwable t) {
					LogFactory.getLog("PopcornSmelting").error("Failed to replace item entity with smeltable item entity", t);
				}
			}
		}
	}
	
}
