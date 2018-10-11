package com.unascribed.popcornsmelting;

import java.lang.reflect.Field;
import java.util.Locale;

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

	public static FireproofKind fireproof = FireproofKind.SMELTABLE_ONLY;
	public static FireproofItemBehavior lavaBehavior = FireproofItemBehavior.SMELT;
	public static FireproofItemBehavior fireBehavior = FireproofItemBehavior.SMELT;
	public static int bouncesToSmelt = 2;
	public static int bouncesToBounceHigher = 8;

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
			"What kinds of items should be made fireproof.\n" +
			"Possible values: all, all_but_fuels, smeltable_only";
	
	private static final String BEHAVIOR_DESC =
			"How dropped fireproof items should behave on $$.\n" +
			"Possible values: smelt, bounce, burn";
	
	private static final String BEHAVIOR_FIRE_DESC = BEHAVIOR_DESC.replace("$$", "fire");
	private static final String BEHAVIOR_LAVA_DESC = BEHAVIOR_DESC.replace("$$", "lava");
	
	private static final String BOUNCES_TO_SMELT_DESC =
			"How many times a fireproof item must bounce before it smelts.\n" +
			"Higher values encourage building smelters, lower values encourage\n" +
			"ad-hoc usage with a flint-and-steel.";
	
	private static final String BOUNCES_TO_BOUNCE_HIGHER_DESC =
			"How many times a fireproof item must bounce before it will start\n" +
			"bouncing higher. Makes it easier to build autocollecting smelter pits.\n" +
			"-1 disables this behavior.";
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e) {
		Configuration cfg = new Configuration(e.getSuggestedConfigurationFile());
		cfg.load();
		PopcornSmeltingRecipes.setInherit(cfg.getBoolean("inheritFurnaceRecipes", "General", true, INHERIT_DESC));
		cfg.getCategory("General").remove("fireproofAllItems");
		fireproof = FireproofKind.valueOf(cfg.getString("fireproof", "General", "smeltable_only", FIREPROOF_DESC).toUpperCase(Locale.ROOT));
		fireBehavior = FireproofItemBehavior.valueOf(cfg.getString("fireBehavior", "General", "smelt", BEHAVIOR_FIRE_DESC).toUpperCase(Locale.ROOT));
		lavaBehavior = FireproofItemBehavior.valueOf(cfg.getString("lavaBehavior", "General", "smelt", BEHAVIOR_LAVA_DESC).toUpperCase(Locale.ROOT));
		bouncesToSmelt = cfg.getInt("bouncesToSmelt", "General", 2, 0, 128, BOUNCES_TO_SMELT_DESC);
		bouncesToBounceHigher = cfg.getInt("bouncesToBounceHigher", "General", 8, -1, 128, BOUNCES_TO_BOUNCE_HIGHER_DESC);
		if (bouncesToBounceHigher == -1) bouncesToBounceHigher = Integer.MAX_VALUE;
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
			if (fireproof.shouldFireproof(is)) {
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
