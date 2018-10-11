package com.unascribed.popcornsmelting.compat.jei;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import org.lwjgl.opengl.GL11;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class PopcornSmeltingCategory implements IRecipeCategory<PopcornSmeltingRecipeWrapper> {

	private IDrawable bg;
	private IDrawable icon;
	
	public PopcornSmeltingCategory(IGuiHelper helper) {
		bg = helper.createDrawable(new ResourceLocation("popcornsmelting", "textures/gui/jei.png"), 0, 0, 89, 47, 89, 47);
		icon = helper.createDrawable(new ResourceLocation("popcornsmelting", "textures/gui/popcorn.png"), 0, 0, 16, 16, 16, 16);
	}
	
	@Override
	public String getUid() {
		return "popcornsmelting";
	}

	@Override
	public String getTitle() {
		return I18n.format("gui.jei.category.popcornsmelting");
	}

	@Override
	public String getModName() {
		return "Popcorn Smelting";
	}

	@Override
	public IDrawable getBackground() {
		return bg;
	}
	
	@Override @Nullable
	public IDrawable getIcon() {
		return icon;
	}
	
	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		if (mouseX >= 21 && mouseX <= 69 && mouseY >= 27 && mouseY <= 43) {
			return Collections.singletonList(I18n.format("gui.popcornsmelting.drop"));
		}
		return IRecipeCategory.super.getTooltipStrings(mouseX, mouseY);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, PopcornSmeltingRecipeWrapper recipeWrapper, IIngredients ingredients) {
		recipeLayout.getItemStacks().init(0, true, 4, 6);
		recipeLayout.getItemStacks().init(1, false, 68, 6);
		recipeLayout.getItemStacks().set(ingredients);
	}
	
	@Override
	public void drawExtras(Minecraft mc) {
		mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		String str = ((Minecraft.getSystemTime() / 2000)%2) == 0 ? "minecraft:blocks/lava_still" : "minecraft:blocks/fire_layer_1";
		TextureAtlasSprite tas = mc.getTextureMapBlocks().getAtlasSprite(str);
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder bb = tess.getBuffer();
		GlStateManager.color(1, 1, 1);
		bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		rect(bb, 21, 27, tas);
		rect(bb, 37, 27, tas);
		rect(bb, 53, 27, tas);
		tess.draw();
	}

	private void rect(BufferBuilder bb, int x, int y, TextureAtlasSprite tas) {
		bb.pos(x, y+16, 0).tex(tas.getMinU(), tas.getMaxV()).endVertex();
		bb.pos(x+16, y+16, 0).tex(tas.getMaxU(), tas.getMaxV()).endVertex();
		bb.pos(x+16, y, 0).tex(tas.getMaxU(), tas.getMinV()).endVertex();
		bb.pos(x, y, 0).tex(tas.getMinU(), tas.getMinV()).endVertex();
	}

}
