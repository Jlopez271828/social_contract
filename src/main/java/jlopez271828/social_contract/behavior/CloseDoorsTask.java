package jlopez271828.social_contract.behavior;

import com.google.common.collect.ImmutableMap;
import jlopez271828.social_contract.Social_contract;
import jlopez271828.social_contract.types.AttachmentTypes;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CloseDoorsTask extends Behavior<Villager> {

    private int currentDoorIndex = 0;
    private List<GlobalPos> doorList;


    private static final int timeout = 30 * 20;
    private static final int closeEnough = 2;
    private static final Logger logger = Social_contract.LOGGER;

    public CloseDoorsTask(){
        super(
                ImmutableMap.of(
                        MemoryModuleType.HOME, MemoryStatus.VALUE_PRESENT,
                        MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED
                ), timeout
        );
    }

    public boolean checkExtraStartConditions(ServerLevel level, Villager villager){

        List<GlobalPos> doorList = villager.getAttached(AttachmentTypes.ROOM_DOORS);

        //In case the schedule hasn't properly updated
        if(level.getOverworldClockTime() < 13000){
            return false;
        }

        if(doorList == null || doorList.isEmpty()){
            return false;
        }


        Optional<GlobalPos> homeOpt = villager.getBrain().getMemory(MemoryModuleType.HOME);
        if(homeOpt.isPresent()){
            GlobalPos home = homeOpt.get();
            if(home.dimension() != level.dimension() || villager.distanceToSqr(home.pos().getBottomCenter()) > 9.0){
                return false;
            }
        }else{
            //if there is no home memory, a villager has no room to check doors in
            return false;
        }

        // the task shouldn't run if all doors are already closed.
        for(GlobalPos pos : doorList){
            BlockState state = level.getBlockState(pos.pos());
            if(state.getValue(DoorBlock.OPEN)){
                return true;
            }

        }

        return false;

    }

    public boolean canStillUse(ServerLevel level, Villager villager, long timestamp){
        return this.doorList != null && this.currentDoorIndex < doorList.size();
    }

    public void start(ServerLevel level, Villager villager, long timestamp){

        if(villager.isSleeping()){
            villager.stopSleeping();
        }

        this.currentDoorIndex = 0;
        this.doorList = getOpenedDoors(level, villager.getAttachedOrElse(AttachmentTypes.ROOM_DOORS, List.of()));
        this.targetNextDoor(level, villager);

    }

    public void stop(ServerLevel level, Villager villager, long timestamp){

        villager.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        this.currentDoorIndex = 0;

    }

    @Override
    protected void tick(ServerLevel level, Villager villager, long gameTime) {

        GlobalPos targetDoorPos = doorList.get(this.currentDoorIndex);


        if (!targetDoorPos.dimension().equals(level.dimension())
                || villager.getNavigation().createPath(targetDoorPos.pos(), closeEnough) == null
        ) {
            this.currentDoorIndex++;
            if (this.currentDoorIndex < doorList.size()) {
                this.targetNextDoor(level, villager);
            }
            return;
        }

        double distanceSq = villager.distanceToSqr(targetDoorPos.pos().getBottomCenter());


        if (distanceSq <= closeEnough * closeEnough) {
            BlockState state = level.getBlockState(targetDoorPos.pos());
            if (state.getBlock() instanceof DoorBlock && state.getValue(DoorBlock.OPEN)) {

                ((DoorBlock) state.getBlock()).setOpen(villager, level, state, targetDoorPos.pos(), false);
            }


            this.currentDoorIndex++;
            if (this.currentDoorIndex < doorList.size()) {
                this.targetNextDoor(level, villager);
            }
        }
    }

    public void targetNextDoor(ServerLevel level, Villager villager){
        GlobalPos target = this.doorList.get(this.currentDoorIndex);
        if(target == null){
            return;
        }
        villager.getBrain().setMemory(
                MemoryModuleType.WALK_TARGET,
                new WalkTarget(target.pos(), 0.5f, closeEnough)
        );
    }

    public static List<GlobalPos> getOpenedDoors(ServerLevel level, final List<GlobalPos> original){

        List<GlobalPos> toReturn = new ArrayList<>();

        for(GlobalPos pos : original){

            BlockState state = level.getBlockState(pos.pos());
            if(state.getValue(DoorBlock.OPEN)){
                toReturn.add(pos);
            }

        }

        return toReturn;


    }


}
