package jlopez271828.social_contract.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import jlopez271828.SocialContractGamerules;
import jlopez271828.social_contract.SocialContractConfig;
import jlopez271828.social_contract.Social_contract;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.VillageBoundRandomStroll;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;


@Mixin(VillageBoundRandomStroll.class)
abstract class VillagerBoundRandomStrollMixin {

    private static final int MAX_TRIES = 10;

    @WrapOperation(method = "lambda$create$2",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/util/LandRandomPos;getPos(Lnet/minecraft/world/entity/PathfinderMob;II)Lnet/minecraft/world/phys/Vec3;"))
    private static Vec3 getLandPos(PathfinderMob mob, int horizontalDist, int verticalDist, Operation<Vec3> original){

        LightEngine<?, ?> blockEngine =  ((LightingAccessor) mob.level().getLightEngine()).social_contract$getBlockEngine();

        Logger logger = LoggerFactory.getLogger("social_contract");

        boolean restriction = GoalUtils.mobRestricted(mob, horizontalDist);

        Supplier<BlockPos> posSupplier = () -> {

            BlockPos direction = RandomPos.generateRandomDirection(mob.getRandom(), horizontalDist, verticalDist);
            BlockPos pos = LandRandomPos.generateRandomPosTowardDirection(mob, horizontalDist, restriction, direction);

            pos = pos != null ? LandRandomPos.movePosUpOutOfSolid(mob, pos) : null;


            return pos;
        };

        ToDoubleFunction<BlockPos> positionWeightFunction = (pos) -> {
            int lightLevel = blockEngine.getLightValue(pos);
            return Math.floor((lightLevel / 8f) + 1);

        };

        Vec3 result = RandomPos.generateRandomPos(posSupplier, positionWeightFunction);



        if(result == null || blockEngine.getLightValue(posFromVec(result)) <= 0){
//            logger.info("could not find suitable walk target in the first try");
            result =  RandomPos.generateRandomPos(posSupplier, positionWeightFunction);

            if(result != null && blockEngine.getLightValue(posFromVec(result)) > 0){
//                logger.info("(second try) found valid result at {}", result);
                return checkHomeDistance(mob, result);
            }else{
                if(blockEngine.getLightValue(mob.blockPosition()) > 0){
//                    logger.info("could not find valid result, and the light level that the mob currently is in is {}, doing nothing", mob.blockPosition().toString());
                    return null;
                }else{
//                    logger.info("could not find valid result with light, and the mob is currently not in light, moving randomly");
                    return checkHomeDistance(mob, result);
                }
            }


        }else{
//            logger.info("found result at {}", result);
            return checkHomeDistance(mob, result);
        }



    }

    private static BlockPos posFromVec(Vec3 vec){

        return new BlockPos(Mth.floor(vec.x()), Mth.floor(vec.y()), Mth.floor(vec.z()));

    }

    private static Vec3 checkHomeDistance(PathfinderMob mob, Vec3 toCheck){
        if(toCheck == null){
            return  null;
        }

        GlobalPos home = mob.getBrain().getMemory(MemoryModuleType.HOME).orElse(null);

        if(home == null){
            return toCheck;
        }

        Level level = mob.level();

        int max_range;
        if(level instanceof ServerLevel){
            max_range = ((ServerLevel) level).getGameRules().get(SocialContractGamerules.MAX_VILLAGER_WONDER_DISTANCE);
        }else{
            Social_contract.LOGGER.warn("for some reason this code is being ran on the client");
            max_range = SocialContractConfig.MAX_HOME_WANDER_DISTANCE;
        }

        if(home.dimension() == mob.level().dimension() &&  toCheck.distanceToSqr(home.pos().getBottomCenter()) < max_range * max_range){
            return toCheck;
        }else{
            return null;
        }
    }


}
