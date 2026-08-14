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
    private int timer = 0;
    private GlobalPos currentTarget;


    private State state;

    private enum State {
        CONFIGURE,
        DELAY,
        TARGETING,
        WALKING_TO_DOOR,
        FINISHED
    }

    private static final int BASEDELAY = 3 * 20;
    private static final int timeout = 30 * 20;
    private static final int closeEnough = 2;

    public CloseDoorsTask(){
        super(
                ImmutableMap.of(
                        MemoryModuleType.HOME, MemoryStatus.VALUE_PRESENT,
                        MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED
                ), timeout
        );
    }

    public boolean checkExtraStartConditions(ServerLevel level, Villager villager){

        //In case the schedule hasn't properly updated
        if(level.getOverworldClockTime() < 13000){
            return false;
        }

        List<GlobalPos> doorList = villager.getAttached(AttachmentTypes.ROOM_DOORS);



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
        return this.state != State.FINISHED;
    }

    public void start(ServerLevel level, Villager villager, long timestamp){

        //staggering so a whole village doesn't do this at once
        this.timer = BASEDELAY + villager.getRandom().nextInt(0, 40);
        this.currentDoorIndex = -1;
        this.state = State.DELAY;

    }

    public void stop(ServerLevel level, Villager villager, long timestamp){

        villager.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        this.currentDoorIndex = 0;

    }

    @Override
    protected void tick(ServerLevel level, Villager villager, long gameTime) {

        switch (this.state){

            case DELAY -> {
                if(timer > 0){
                    timer--;
                    return;
                }else{
                    this.state = State.CONFIGURE;
                }
            }

            case CONFIGURE -> {
                List<GlobalPos> temp = villager.getAttached(AttachmentTypes.ROOM_DOORS);

                if(temp == null){
                    this.state = State.FINISHED;
                    return;
                }

                this.doorList = getOpenedDoors(level, temp);

                if(this.doorList.isEmpty()){
                    this.state = State.FINISHED;
                    return;
                }

                if(villager.isSleeping()){
                    villager.stopSleeping();
                }

                this.state = State.TARGETING;

                return;

            }

            case TARGETING -> {

                this.currentDoorIndex++;

                if(this.currentDoorIndex < this.doorList.size()) {


                    this.currentTarget = this.targetNextDoor(level, villager);

                    if (this.currentTarget == null) {
                        this.state = State.FINISHED;
                        return;
                    }

                    if (!this.currentTarget.dimension().equals(level.dimension())) {
                        return;
                    }

                    this.state = State.WALKING_TO_DOOR;

                }else{
                    this.state = State.FINISHED;
                }

            }

            case WALKING_TO_DOOR -> {

                double distanceSq = villager.distanceToSqr(this.currentTarget.pos().getBottomCenter());

                if (distanceSq <= closeEnough * closeEnough + 0.8) {
                    BlockState state = level.getBlockState(this.currentTarget.pos());
                    if (state.getBlock() instanceof DoorBlock && state.getValue(DoorBlock.OPEN)) {

                        ((DoorBlock) state.getBlock()).setOpen(villager, level, state, this.currentTarget.pos(), false);
                    }

                    this.state = State.TARGETING;

                }


            }

        }

    }

    public GlobalPos targetNextDoor(ServerLevel level, Villager villager){
        GlobalPos target = this.doorList.get(this.currentDoorIndex);
        if(target == null){
            return null;
        }


        villager.getBrain().setMemory(
                MemoryModuleType.WALK_TARGET,
                new WalkTarget(target.pos(), 0.5f, closeEnough)
        );

        return target;
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
