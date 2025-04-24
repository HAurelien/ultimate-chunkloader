package com.habermacheraurelien.ultimatechunkloader.component;

import com.habermacheraurelien.ultimatechunkloader.UltimateChunkLoaderMod;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, UltimateChunkLoaderMod.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ANCHOR_COORDINATE_X =
            register("chunk_anchor_coordinate_x", builder -> builder.persistent(Codec.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> ANCHOR_COORDINATE_Z =
            register("chunk_anchor_coordinate_z", builder -> builder.persistent(Codec.INT));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> ANCHOR_DIMENSION =
            register("chunk_anchor_dimension", builder -> builder.persistent(Codec.STRING));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> REMOTE_CHUNK_LOADED =
            register("chunk_loaded_by_remote", builder -> builder.persistent(Codec.BOOL));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> DEBUG_STICK_MODE =
            register("debug_stick_mode", builder -> builder.persistent(Codec.INT));

    private static <T>DeferredHolder<DataComponentType<?>,
            DataComponentType<T>> register (String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator){
        return DATA_COMPONENT_TYPES.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}
