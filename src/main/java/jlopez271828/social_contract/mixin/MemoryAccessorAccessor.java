package jlopez271828.social_contract.mixin;

import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MemoryAccessor.class)
public interface MemoryAccessorAccessor {

    //Why in the world is there not a getter in the game for this information?????
    @Accessor("memoryType")
    MemoryModuleType<?> social_contract$getMemoryModuleType();
    
    
}
