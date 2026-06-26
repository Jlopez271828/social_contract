package jlopez271828.social_contract;

import jlopez271828.social_contract.mixin.LightingAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class Scoring {

    private static int MAX = 200;
    private static final Logger logger = LoggerFactory.getLogger("social_contract");

    public static int scoreRoom(BlockPos start, ServerLevel level){



        LightEngine<?, ?> lightEngine = ((LightingAccessor) level.getLightEngine()).social_contract2$getBlockEngine();
        if(lightEngine == null){
            logger.error("Could not get lightEngine");
            return 0;
        }

        Collection<UUID> roommates = new ArrayList<>();

        Set<BlockPos> visited = new HashSet<BlockPos>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        int count = 1;

        //if we start on the bed, we might get weird logic pertaining to solid objects.
        queue.add(start.above());

        BlockPos current;
        while(count < MAX && !queue.isEmpty()){

            current = queue.remove();

//            logger.info("current block is {}", current.toString());

            if(visited.contains(current)){
//                logger.info("has been visited");
                continue;
            }

            if(isSolid(current, level)){
//                logger.info("is solid");

                visited.add(current);//because checking if a block is solid required a bit of work, better to store its hash

                BlockState state = level.getBlockState(current);
                if(state.getBlock() instanceof BedBlock && state.getValue(BedBlock.PART) == BedPart.HEAD ){
                    logger.info("found bed head at {}", current);

                    BlockEntity blockEntity = level.getBlockEntity(current);

                    if (blockEntity != null){
                        UUID id = blockEntity.getAttached(AttachmentTypes.BED_OWNER_ATTACHMENT);
                        if(id != null && level.getEntity(id) != null){
                            roommates.add(id);
                        }
                    }

                }

                continue;

            }

            //checking if this block has a 0 light value, independent from sky light
            if(lightEngine.getLightValue(current) < 0){
                logger.info("has bad light");
                return 0;
            }

//            logger.info("is good");
            visited.add(current);

            queue.add(current.below());
            queue.add(current.west());
            queue.add(current.south());
            queue.add(current.east());
            queue.add(current.north());
            queue.add(current.above());

            count = count + 1;

        }

        int resultScore;

        if(count >= MAX - 1){
            logger.info("We hit the maximum and have concluded that the room is not enclosed");
            resultScore = 0;
        }else{
            logger.info("First score: {}", count);

            resultScore = count / (roommates.size() + 1);
            logger.info("Score after accounting for {} roommates: {}", roommates.size(), resultScore);
        }


        for (UUID roommateID : roommates){
            logger.info("roommate exists with UUID {}", roommateID);
            Entity entity = level.getEntity(roommateID);
            if(entity instanceof Villager){

                ((Villager) entity).getBrain().setMemory(CustomMemoryModuleType.HOME_SCORE, resultScore);
            }

        }

        return resultScore;

    }

    private static Boolean isSolid(BlockPos pos, ServerLevel level){

        BlockState state = level.getBlockState(pos);

        if(state.is(BlockTags.DOORS)){
            return true;
        }

        if(state.is(BlockTags.TRAPDOORS) || state.is(BlockTags.FENCE_GATES)){
            return false;
        }

        return !state.getCollisionShape(level, pos, CollisionContext.empty()).isEmpty();

    }

}
