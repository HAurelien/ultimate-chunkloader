package com.habermacheraurelien.ultimatechunkloader.datagen;

import com.habermacheraurelien.ultimatechunkloader.UltimateChunkLoaderMod;
import com.habermacheraurelien.ultimatechunkloader.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {

    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, UltimateChunkLoaderMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.CHUNK_ANCHOR.get());
        tag(BlockTags.NEEDS_IRON_TOOL).add(ModBlocks.CHUNK_ANCHOR.get());
        tag(BlockTags.DRAGON_IMMUNE).add(ModBlocks.CHUNK_ANCHOR.get());
        tag(BlockTags.ENDERMAN_HOLDABLE).remove(ModBlocks.CHUNK_ANCHOR.get());
    }
}
