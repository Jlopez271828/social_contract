package jlopez271828.social_contract.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

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
