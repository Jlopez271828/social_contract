package jlopez271828.social_contract.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.npc.villager.Villager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Villager.class)
abstract class VillagerSubMixinMob extends MobMixin {


    @Override
    protected int alterMaxFallDistance(Operation<Integer> original){
        if(((Villager) (Object) this).isPanicking()){
            return original.call();
        }else{
            return 1;
        }
    }

}
