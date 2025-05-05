package com.habermacheraurelien.ultimatechunkloader.datagen;

import com.habermacheraurelien.ultimatechunkloader.UltimateChunkLoaderMod;
import com.habermacheraurelien.ultimatechunkloader.block.ModBlocks;
import com.habermacheraurelien.ultimatechunkloader.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModBlocks.CHUNK_ANCHOR.get())
                .pattern("III")
                .pattern("IEI")
                .pattern("IDI")
                .define('I', Blocks.IRON_BLOCK.asItem())
                .define('E', Items.ENDER_PEARL)
                .define('D', Blocks.DIAMOND_BLOCK.asItem())
                .unlockedBy(UltimateChunkLoaderMod.MOD_ID + "anchor_diamond_block", has(Blocks.DIAMOND_BLOCK.asItem()))
                .unlockedBy(UltimateChunkLoaderMod.MOD_ID + "anchor_iron", has(Blocks.IRON_BLOCK.asItem()))
                .unlockedBy(UltimateChunkLoaderMod.MOD_ID + "anchor_ender_pearl", has(Items.ENDER_PEARL))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, ModItems.CHUNK_REMOTE.get())
                .pattern("ILI")
                .pattern("RGR")
                .pattern("RRR")
                .define('I', Items.IRON_INGOT)
                .define('L', Items.LAPIS_LAZULI)
                .define('G', Items.GLASS)
                .define('R', Items.REDSTONE)
                .unlockedBy(UltimateChunkLoaderMod.MOD_ID + "anchor_remote_iron", has(Items.IRON_INGOT))
                .unlockedBy(UltimateChunkLoaderMod.MOD_ID + "anchor_lapis_lazuli", has(Items.LAPIS_LAZULI))
                .unlockedBy(UltimateChunkLoaderMod.MOD_ID + "anchor_remote_glass", has(Blocks.GLASS))
                .unlockedBy(UltimateChunkLoaderMod.MOD_ID + "anchor_remote_redstone", has(Items.REDSTONE))
                .save(recipeOutput);
    }
}
