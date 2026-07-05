package jlopez271828.social_contract;

import com.google.common.collect.ImmutableSet;
import jlopez271828.social_contract.types.AttachmentTypes;
import jlopez271828.social_contract.types.CustomMemoryModuleType;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.Set;


public class RoomScoreSensor extends Sensor<LivingEntity> {
    private static final int COOLDOWN = 5 * 30; // ticks, 5 minutes
    private int timer = 0;
    private static final Logger logger = LoggerFactory.getLogger("social_contract");
    private static final int MIN_GOOD_SCORE = 4;

    @Override
    public Set<MemoryModuleType<?>> requires(){
        return ImmutableSet.of(MemoryModuleType.HOME, CustomMemoryModuleType.HOME_SCORE);
    }

    @Override
    protected void doTick(final ServerLevel level, final LivingEntity entity){

        if(timer > 0){
//            logger.info("timer is {}", Integer.toString(timer));
            timer = timer - 1;
            if(timer % 60 == 0){
                logger.info("30 ticks have passed");
            }
        }else{
            logger.info("begging score room task\n");
            RandomSource random = level.getRandom();
            int score = 0;
            Brain<?> brain = entity.getBrain();
            GlobalPos memory =  brain.getMemory(MemoryModuleType.HOME).orElse(null);
            if(memory != null){
                logger.info("memory found");
                if(memory.dimension() == level.dimension()) {
                    logger.info("memory in same dimension as entity, commencing scoring");
                    score = Scoring.scoreRoom(memory.pos(), level);
                    logger.info("room scored with a score of {}", Integer.toString(score));
                    BlockEntity blockEntity = level.getBlockEntity(memory.pos());
                    if (blockEntity != null) { // I don't like it but this seems like the best place to set the UUID in the bed.
                        logger.info("saving this villagers UUID into bed at {}", memory.pos());
                        blockEntity.setAttached(AttachmentTypes.BED_OWNER_ATTACHMENT, entity.getUUID());
                    }
                }


            }else{
                logger.info("No home memory");
                brain.setMemory(CustomMemoryModuleType.HOME_SCORE, score);
                level.broadcastEntityEvent(entity, (byte)13);
                timer = random.nextInt(20, 100);
                logger.info("timer after no home memory found: {}", timer);
                return;
            }

            brain.setMemory(CustomMemoryModuleType.HOME_SCORE, score);
            if(score > MIN_GOOD_SCORE){
                level.broadcastEntityEvent(entity, (byte)14);

            }else{
                level.broadcastEntityEvent(entity, (byte)13);
            }

            logger.info("timer before reset is {}", Integer.toString(timer));
            timer = COOLDOWN + random.nextInt(20, 120);
            logger.info("timer after reset is {}", Integer.toString(timer));
        }


    }

}
