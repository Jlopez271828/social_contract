package jlopez271828.social_contract.mixin;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractVillager.class)
public interface AbstractVillagerAccessor {

    @Invoker("addParticlesAroundSelf")
    void social_contract$addParticlesAroundSelf(final ParticleOptions particle);

}
