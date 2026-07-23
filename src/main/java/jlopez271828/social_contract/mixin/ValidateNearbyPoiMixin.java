package jlopez271828.social_contract.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import jlopez271828.social_contract.types.AttachmentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.ValidateNearbyPoi;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(ValidateNearbyPoi.class)
abstract class ValidateNearbyPoiMixin {


    @Inject(method = "lambda$create$2",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/behavior/declarative/MemoryAccessor;erase()V"
            )
    )
    private static void removeAttachments(
            BehaviorBuilder.Instance i,
            MemoryAccessor memory,
            Predicate poiType,
            ServerLevel level,
            LivingEntity body,
            long timestamp,
            CallbackInfoReturnable<Boolean> cir,
            @Local(name = "pos") BlockPos pos
            ){

        if(body instanceof Villager villager){

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(blockEntity != null && blockEntity.hasAttached(AttachmentTypes.BED_OWNER_ATTACHMENT)){
                blockEntity.removeAttached(AttachmentTypes.BED_OWNER_ATTACHMENT);
            }

        }

    }

}
