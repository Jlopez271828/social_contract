package jlopez271828.social_contract;

import jlopez271828.social_contract.mixin.VillagerExtendedData;
import jlopez271828.social_contract.types.CustomMemoryModuleType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import java.util.Optional;

public class FollowFriendGoal {

    private static Logger logger = Social_contract.LOGGER;

    public static OneShot<PathfinderMob> create(final float speedModifier,
                                                final int closeEnoughDist,
                                                final int tooFarDistance,
                                                final int giveUpDistance,
                                                final int tooLongUnreachableDuration){

        return BehaviorBuilder.create(i -> i.group(i.registered(MemoryModuleType.WALK_TARGET), i.present(CustomMemoryModuleType.PLAYER_TO_FOLLOW), i.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)).apply(i,
                (walktarget, toFollow, cantReachSince) ->
                        (level, body, timestamp) -> {

//            logger.info("starting follow goal");
            Player player = i.get(toFollow);
            BlockPos pos = player.blockPosition();
//            logger.info("player at {} villager at {}", player.blockPosition(), body.blockPosition());
            Optional<Long> cantReachTargetSince = i.tryGet(cantReachSince);
            if(player.level().dimension() == level.dimension()
                    && (!cantReachTargetSince.isPresent() || level.getGameTime() - (Long) cantReachTargetSince.get() <= tooLongUnreachableDuration)){

                int dist = pos.distManhattan(body.blockPosition());



                if(dist > tooFarDistance){

                    if(dist < giveUpDistance){
                        Path path = body.getNavigation().createPath(pos, giveUpDistance);
                        if(path != null){
                            logger.info("found suitable path straight to player");
                            walktarget.set(new WalkTarget(pos, speedModifier, closeEnoughDist));
                            return true;
                        }else{
                            logger.info("could not find suitable path straight to player");
                            //this continues to the rest of the program
                            // TODO: make this execution flow less messy
                        }
                    }
                    else{

                        logger.info("distance is too large, giving up");
                        toFollow.erase();
                        cantReachSince.set(timestamp);
                        body.getBrain().setActiveActivityIfPossible(Activity.IDLE);
                        if(body.getBrain().isActive(Activity.IDLE)){
                            logger.info("Successfully set activity back to idle");
                        }else{
                            logger.info("failed to set activity back to idle");
                        }
                        return true;
                    }

                    Vec3 towardsTargetPos = null;
                    int tries = 0;
                    int MAX_TRIES = 1000;

                    while (towardsTargetPos == null || BlockPos.containing(towardsTargetPos).distManhattan(body.blockPosition()) > tooFarDistance) {
                        towardsTargetPos = DefaultRandomPos.getPosTowards(body, 20, 7, Vec3.atBottomCenterOf(pos), (float) (Math.PI / 4));
                        if (++tries == MAX_TRIES) {
                            logger.info("could not find suitable path after 500 tries");
                            toFollow.erase();
                            cantReachSince.set(timestamp);
                            body.getBrain().setActiveActivityIfPossible(Activity.IDLE);
                            if(body.getBrain().isActive(Activity.IDLE)){
                                logger.info("Successfully set activity back to idle");
                            }else{
                                logger.info("failed to set activity back to idle");
                            }
                            return true;
                        }
                    }

                    logger.info("suitable target found after {} tries", tries);

                    walktarget.set(new WalkTarget(towardsTargetPos, speedModifier, closeEnoughDist));
                }else if(dist <= closeEnoughDist){
                    logger.info("setting walktarget to player");
                    walktarget.set(new WalkTarget(player, speedModifier, closeEnoughDist));
                }else{
                    logger.info("distance is further than closeEnoughDist");
                }

            }else{//our entity cant reach the player
                logger.info("first check failed, can't reach player");
                toFollow.erase();
                cantReachSince.set(timestamp);
                body.getBrain().setActiveActivityIfPossible(Activity.CORE);
            }




            return true;

        }));

    }
}
