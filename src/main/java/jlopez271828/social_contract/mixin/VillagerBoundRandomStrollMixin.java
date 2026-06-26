package jlopez271828.social_contract.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.VillageBoundRandomStroll;
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


@Mixin(VillageBoundRandomStroll.class)
abstract class VillagerBoundRandomStrollMixin {

    private static final int MAX_TRIES = 10;

    @WrapOperation(method = "lambda$create$2",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/util/LandRandomPos;getPos(Lnet/minecraft/world/entity/PathfinderMob;II)Lnet/minecraft/world/phys/Vec3;"))
    private static Vec3 getLandPos(PathfinderMob mob, int horizontalDist, int verticalDist, Operation<Vec3> original){

        LightEngine<?, ?> blockEngine =  ((LightingAccessor) mob.level().getLightEngine()).social_contract2$getBlockEngine();

        Logger logger = LoggerFactory.getLogger("social_contract");

        boolean restriction = GoalUtils.mobRestricted(mob, horizontalDist);

        Vec3 result = RandomPos.generateRandomPos(() -> {
            BlockPos direction = RandomPos.generateRandomDirection(mob.getRandom(), horizontalDist, verticalDist);
            BlockPos pos = LandRandomPos.generateRandomPosTowardDirection(mob, horizontalDist, restriction, direction);
//            if(pos == null){
//                logger.info("got null pos 3");
//            }else{
//                logger.info("pos before moving up: {}", pos.toString());
                pos = pos != null ? LandRandomPos.movePosUpOutOfSolid(mob, pos) : null;
//                if(pos != null){
//                    logger.info("pos after moving up: {}", pos.toString());
//                }else{
//                    logger.info("got null pos 4");
//                }
//            }

            return pos;
        },
                (pos) -> {
            int lightLevel = blockEngine.getLightValue(pos);
//            logger.info("light level at {} is {}", pos, lightLevel);
            return Math.floor((lightLevel / 8f) + 1);

        });

//        logger.info("tried to find random stroll target at {}", result.toString());

        if(result == null || blockEngine.getLightValue(new BlockPos((int)result.x(), (int)result.y(), (int)result.z())) <= 0){
//            logger.info("could not find suitable walk target in the first try");
            result =  RandomPos.generateRandomPos(() -> {
                BlockPos direction = RandomPos.generateRandomDirection(mob.getRandom(), horizontalDist , verticalDist);
                BlockPos pos = LandRandomPos.generateRandomPosTowardDirection(mob, horizontalDist, restriction, direction);
                if(pos != null) {
//                    logger.info("(second try) pos before moving upwards: {}", pos);
                    pos = LandRandomPos.movePosUpOutOfSolid(mob, pos);
                    if(pos != null) {
//                        logger.info("(second try) pos after moving upwards: {}", pos);
                        return pos;
                    }else{
//                        logger.info("(second try) got null pos 2");
                        return null;
                    }
                }else{
//                    logger.info("(second try) got null pos 1");
                    return null;
                }


            }, (pos) -> {
                int lightLevel = blockEngine.getLightValue(pos);
//                logger.info("(second try) light level at {} is {}", pos.toString(), lightLevel);
                return Math.floor((lightLevel / 8f) + 1);

            });

            if(result != null && blockEngine.getLightValue(new BlockPos((int)result.x(), (int)result.y(), (int)result.z())) > 0){
//                logger.info("(second try) found valid result at {}", result);
                return result;
            }else{
                if(blockEngine.getLightValue(mob.blockPosition()) > 0){
//                    logger.info("could not find valid result, and the light level that the mob currently is in is {}, doing nothing", mob.blockPosition().toString());
                    return null;
                }else{
//                    logger.info("could not find valid result with light, and the mob is currently not in light, moving randomly");
                    return result;
                }
            }


        }else{
//            logger.info("found result at {}", result);
            return result;
        }



    }

//    private static Vec3 getGoodLandPos(PathfinderMob mob, int horizontalDist, int verticalDist){
//
//        LightEngine<?, ?> blockEngine =  ((LightingAccessor) mob.level().getLightEngine()).social_contract2$getBlockEngine();
//
//        Logger logger = LoggerFactory.getLogger("social_contract");
//
//        boolean restriction = GoalUtils.mobRestricted(mob, horizontalDist);
//
//        BlockPos feet = mob.blockPosition();
//
//        RandomSource random = mob.getRandom();
//
//        BlockPos target;
//
//        Level level = mob.level();
//
//        for(int i = 0; i < MAX_TRIES; i++){
//
//            int dx = random.nextInt(2 * horizontalDist) - horizontalDist;
//            int dz = random.nextInt(2 * horizontalDist) - horizontalDist;
//
//            target = new BlockPos(feet.offset(dx, 0, dz));
//
//            if(level.getBlockState(target).isAir()){
//                //go down until we encounter first solid block
//                for(int k = 0; k < verticalDist; k++){
//                    target = target.below();
//                    if(!level.getBlockState(target).isAir()){
//                        return Vec3.atBottomCenterOf(target.above());
//
//                    }
//
//                }
//
//            }else{
//
//                //go up untill we encounter first air block
//                for(int k = 0; k < verticalDist; k++){
//                    target = target.above();
//                    if(level.getBlockState(target).isAir()){
//                        return Vec3.atBottomCenterOf(target);
//
//                    }
//                }
//
//            }
//
//
//        }
//
//    }




}
