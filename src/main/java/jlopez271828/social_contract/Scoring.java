package jlopez271828.social_contract;

import jlopez271828.SocialContractGamerules;
import jlopez271828.social_contract.mixin.LightingAccessor;
import jlopez271828.social_contract.types.AttachmentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class Scoring {


    private static final Logger logger = LoggerFactory.getLogger("social_contract");

    /**
     * This is a simple flood fill that will originate at the provided start variable. This will check every block in
     * a room to first check if it is either solid (it takes up the full block, the exception being doors, calls {@link #isSolid(BlockPos, ServerLevel, List<GlobalPos>)})
     * or non-solid. If it is non-solid, it checks the block light level (light that does not come from the sky) to ensure that
     * it is greater than 0 (safe from mob spawning). If any non-solid block fails this test, a result of 0 will be returned.
     * @param start The block to start the flood fill from
     * @param level The level the space being scored exists within
     * @return The score of the room, which is the total volume of the enclosed space, or 0, if the space
     * is not enclosed, or it is unsafe.
     */
    public static ScoreResult scoreRoom(BlockPos start, ServerLevel level){



        LightEngine<?, ?> lightEngine = ((LightingAccessor) level.getLightEngine()).social_contract$getBlockEngine();
        if(lightEngine == null){
            logger.error("Could not get lightEngine");
            return null;
        }

        Collection<UUID> roommates = new ArrayList<>();

        Set<BlockPos> visited = new HashSet<BlockPos>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        List<GlobalPos> attachedDoors = new ArrayList<>();
        int count = 1;

        //if we start on the bed, we might get weird logic pertaining to solid objects.
        queue.add(start.above());

        final int max_room_size = level.getGameRules().get(SocialContractGamerules.MAX_ROOM_VOLUME_GAMERULE);



        BlockPos current;
        while(count < max_room_size && !queue.isEmpty()){


            current = queue.remove();



            if(visited.contains(current)){
                continue;
            }

            if(isSolid(current, level, attachedDoors)){

                visited.add(current);

                BlockState state = level.getBlockState(current);

                if(state.getBlock() instanceof BedBlock && state.getValue(BedBlock.PART) == BedPart.HEAD ){

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
            if(lightEngine.getLightValue(current) <= 0){
                return null;
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

        if(count >= max_room_size - 1){
            logger.info("We hit the maximum and have concluded that the room is not enclosed");
            resultScore = 0;
        }else if(!roommates.isEmpty()){
            resultScore = count / (roommates.size());

        }else{
            resultScore = count;
        }

        //since each roommate must be in the same space, they must have the same score
        //perhaps this shouldn't be a linear function of the number of roommates
        for (UUID roommateID : roommates){
            Entity entity = level.getEntity(roommateID);
            if(entity instanceof Villager villager){
                Happiness.setHappiness(resultScore, villager, Happiness.HappinessType.ROOM);

            }

        }

        return new ScoreResult(resultScore, attachedDoors);

    }

    /**
     * Checks is a block at the specified position is solid as seen by {@link #scoreRoom(BlockPos, ServerLevel)}
     *
     * Returns true only if the block is either a Door (except for Iron Doors), or the block takes up the full 1 cubic meter.
     *
     * @param pos the position of the block
     * @param level the level this position exists in
     * @return whether
     */
    private static Boolean isSolid(BlockPos pos, ServerLevel level, List<GlobalPos> doorList){

        BlockState state = level.getBlockState(pos);

        if(state.is(BlockTags.DOORS)){

            if(state.is(Blocks.IRON_DOOR)){
                return false;
            }

            if(state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
                doorList.add(new GlobalPos(level.dimension(), pos));
            }
            return true;

        }

        //I think I might be able to remove this conditional as it might get caught by the proceeding check.
        if(state.is(BlockTags.TRAPDOORS) || state.is(BlockTags.FENCE_GATES)){
            return false;
        }

        return !state.getCollisionShape(level, pos, CollisionContext.empty()).isEmpty();

    }

}
