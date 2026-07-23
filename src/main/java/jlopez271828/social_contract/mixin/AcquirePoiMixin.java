package jlopez271828.social_contract.mixin;


import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import jlopez271828.social_contract.Happiness;
import jlopez271828.social_contract.Social_contract;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.AcquirePoi;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.npc.villager.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.function.Predicate;

@Mixin(AcquirePoi.class)
public class AcquirePoiMixin {

    @Inject(method = "lambda$create$7",
            at = @At("TAIL")
    )
    private static void onPoiAcquisition(
            PoiManager poiManager,
            Predicate poiType,
            BlockPos targetPos,
            MemoryAccessor toAcquire,
            ServerLevel level,
            Optional onPoiAcquisitionEvent,
            PathfinderMob body,
            Long2ObjectMap batchCache,
            Holder type,
            CallbackInfo ci)
    {

        if(body instanceof Villager villager && ((MemoryAccessorAccessor) toAcquire).social_contract$getMemoryModuleType().equals(MemoryModuleType.HOME)){

            Happiness.increaseHappiness(Social_contract.HAPPINESS_FOR_BED, villager);

        }

        //TODO: maybe make this into a proper event and propose it be added to the fabric api

    }

}
