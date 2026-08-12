package jlopez271828.social_contract.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Monster.class)
public abstract class MonsterMixin {

    @Shadow public abstract LivingEntity.Fallsounds getFallSounds();

    @WrapMethod(method = "shouldDropLoot")
    protected boolean overrideLootDrop(ServerLevel level, Operation<Boolean> original){
        return original.call(level);
    }

}
