package jlopez271828.social_contract;

import com.mojang.serialization.Codec;
import com.sun.jna.Memory;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

public abstract class CustomMemoryModuleType {

    public static final MemoryModuleType<Integer> HOME_SCORE = register("home_score", Codec.INT);

//    public static final MemoryModuleType<UUID> following

    private static final Logger logger = LoggerFactory.getLogger("social_contract");

    private static <U> MemoryModuleType<U> register(final String name, final Codec<U> codec) {
        return Registry.register(BuiltInRegistries.MEMORY_MODULE_TYPE, Identifier.withDefaultNamespace(name), new MemoryModuleType<>(Optional.of(codec)));
    }

    public static void initialize(){
        // for static initialization
        logger.info("initializing CustomMemoryModuleType");
    }

}
