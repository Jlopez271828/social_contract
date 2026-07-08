package jlopez271828.social_contract.mixin;

import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LevelLightEngine.class)
public interface LightingAccessor {

    @Accessor("blockEngine")
    LightEngine<?, ?> social_contract$getBlockEngine();


}


