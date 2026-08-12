package jlopez271828.social_contract.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @WrapMethod(method = "actuallyHurt")
    protected void overrideForVillager(ServerLevel level, DamageSource source, float dmg, Operation<Void> original){
        original.call(level, source, dmg);
    }



}
