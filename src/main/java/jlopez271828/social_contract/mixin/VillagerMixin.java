package jlopez271828.social_contract.mixin;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import jlopez271828.social_contract.old.PathfinderMobMixin;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.lighting.LightEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import jlopez271828.social_contract.CustomSensorTypes;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@Mixin(Villager.class)
public abstract class VillagerMixin  {

//    @Unique
//    UUID playerToFollow = null;
//
//    @Override
//    public UUID social_contract2$getPlayerToFollow(){
//        return this.playerToFollow;
//    }
//
//    @Override
//    public void social_contract2$setPlayerToFollow(UUID id){
//        this.playerToFollow = id;
//    }

    @ModifyArg(method = "<clinit>",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/Brain;provider(Ljava/util/Collection;Lnet/minecraft/world/entity/ai/Brain$ActivitySupplier;)Lnet/minecraft/world/entity/ai/Brain$Provider;"),
            index=0 )
    private static Collection<SensorType<?>> addCustomVillagerSensors(Collection<SensorType<?>> original){
        Collection<SensorType<?>> altered = new ArrayList<SensorType<?>>(original);
        altered.add(CustomSensorTypes.ROOM_SCORE_SENSOR);

        return altered;

    }

    @Shadow
    public abstract Brain<?> getBrain();


    @Inject(method = "releaseAllPois", at = @At(value = "HEAD"))
    private void clearBedAttachments(CallbackInfo ci){

        Logger logger = LoggerFactory.getLogger("social_contract");

        Brain<?> brain = this.getBrain();

        GlobalPos gpos = brain.getMemory(MemoryModuleType.HOME).orElse(null);

//        if(gpos != null){
//
//        }

    }




}
