package jlopez271828.social_contract.old;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.LevelReader;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PathfinderMob.class)
public class PathfinderMobMixin {

    @WrapMethod(method = "getWalkTargetValue(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/LevelReader;)F")
    protected float overrideGetWalkTargetValue(BlockPos pos, LevelReader level, Operation<Float> original){

        return original.call(pos, level);

    }

}
