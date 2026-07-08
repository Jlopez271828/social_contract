package jlopez271828.social_contract.types;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

public abstract class CustomMemoryModuleType {

    public static final MemoryModuleType<Integer> HOME_SCORE = register("home_score", Codec.INT);

    public static final MemoryModuleType<Player> PLAYER_TO_FOLLOW = register("player_to_follow");


    private static final Logger logger = LoggerFactory.getLogger("social_contract");

    private static <U> MemoryModuleType<U> register(final String name, final Codec<U> codec) {
        return Registry.register(BuiltInRegistries.MEMORY_MODULE_TYPE, Identifier.withDefaultNamespace(name), new MemoryModuleType<>(Optional.of(codec)));
    }

    private static <U> MemoryModuleType<U> register(final String name){
        return Registry.register(BuiltInRegistries.MEMORY_MODULE_TYPE, Identifier.withDefaultNamespace(name), new MemoryModuleType<>(Optional.empty()));
    }

    // TODO: see if I can change namespace to my own space, I am currently using Mojang's

    public static void initialize(){
        // for static initialization
        logger.info("initializing CustomMemoryModuleType");
    }

}
