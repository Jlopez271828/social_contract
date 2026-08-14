package jlopez271828.social_contract.behavior;

import jlopez271828.social_contract.Social_contract;
import jlopez271828.social_contract.types.AttachmentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

import java.util.Optional;

public class FollowFriendGoal {

    private static Logger logger = Social_contract.LOGGER;

    //TODO: reimplement this as a state machine
    // I kinda hate this declarative builder

    public static OneShot<PathfinderMob> create(final float speedModifier,
                                                final int closeEnoughDist,
                                                final int tooFarDistance,
                                                final int giveUpDistance,
                                                final int tooLongUnreachableDuration){

        return BehaviorBuilder.create(i -> i.group(i.registered(MemoryModuleType.WALK_TARGET), i.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)).apply(i,
                (walktarget, cantReachSince) ->
                        (level, body, timestamp) -> {


            Player player = body.getAttached(AttachmentTypes.PLAYER_TO_FOLLOW);
            if(player == null){
                body.getBrain().setActiveActivityIfPossible(Activity.IDLE);
                return true;
            }
            BlockPos pos = player.blockPosition();
            Optional<Long> cantReachTargetSince = i.tryGet(cantReachSince);
            if(player.level().dimension() == level.dimension()
                    && (!cantReachTargetSince.isPresent() || level.getGameTime() -  cantReachTargetSince.get() <= tooLongUnreachableDuration)
            ){

                int dist = pos.distManhattan(body.blockPosition());

                // TODO: make this execution flow less messy

                if(dist > tooFarDistance){

                    if(dist < giveUpDistance){
                        Path path = body.getNavigation().createPath(pos, giveUpDistance);
                        if(path != null){
                            walktarget.set(new WalkTarget(pos, speedModifier, closeEnoughDist));
                            return true;
                        }
                    }
                    else{

//                        logger.info("distance is too large, giving up");
                        body.removeAttached(AttachmentTypes.PLAYER_TO_FOLLOW);
                        cantReachSince.set(timestamp);
                        body.getBrain().setActiveActivityIfPossible(Activity.IDLE);
                        return true;
                    }

                    Vec3 towardsTargetPos = null;
                    int tries = 0;
                    int MAX_TRIES = 1000;

                    while (towardsTargetPos == null || BlockPos.containing(towardsTargetPos).distManhattan(body.blockPosition()) > tooFarDistance) {
                        towardsTargetPos = DefaultRandomPos.getPosTowards(body, 20, 7, Vec3.atBottomCenterOf(pos), (float) (Math.PI / 4));
                        if (++tries == MAX_TRIES) {
                            logger.info("could not find suitable path after 500 tries");
                            body.removeAttached(AttachmentTypes.PLAYER_TO_FOLLOW);
                            cantReachSince.set(timestamp);
                            body.getBrain().setActiveActivityIfPossible(Activity.IDLE);

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
                body.removeAttached(AttachmentTypes.PLAYER_TO_FOLLOW);
                cantReachSince.set(timestamp);
                body.getBrain().setActiveActivityIfPossible(Activity.IDLE);
            }




            return true;

        }));

    }
}
