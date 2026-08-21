package jlopez271828.social_contract.types;

import com.mojang.serialization.Codec;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

import java.util.List;
import java.util.Optional;

public class CustomMemoryModuleTypes {

    public static final MemoryModuleType<List<GlobalPos>> KNOWN_LIGHTS = register("known_lights", GlobalPos.CODEC.listOf());

    private static <U> MemoryModuleType<U> register(final String name, final Codec<U> codec) {
        return Registry.register(BuiltInRegistries.MEMORY_MODULE_TYPE, Identifier.withDefaultNamespace(name), new MemoryModuleType<>(Optional.of(codec)));
    }

    public static void initialize(){

    }

}
