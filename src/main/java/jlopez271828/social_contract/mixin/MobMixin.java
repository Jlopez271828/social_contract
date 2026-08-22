package jlopez271828.social_contract.mixin;


import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Mob.class)
abstract class MobMixin  {



    @WrapMethod(method = "getMaxFallDistance")
    protected int alterMaxFallDistance(Operation<Integer> original){
        return original.call();
    }

}
