package jlopez271828.social_contract.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin extends Entity {

    protected LivingEntityMixin(EntityType<? extends Mob> type, Level level) {
        super(type, level);
    }

    @WrapMethod(method = "actuallyHurt")
    protected void overrideForVillager(ServerLevel level, DamageSource source, float dmg, Operation<Void> original){
        original.call(level, source, dmg);
    }



}
