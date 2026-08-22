package jlopez271828.social_contract.behavior;

import com.google.common.collect.ImmutableMap;
import jlopez271828.social_contract.DecorationResult;
import jlopez271828.social_contract.Social_contract;
import jlopez271828.social_contract.types.AttachmentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.decoration.painting.Painting;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class DecorateRoomTask extends Behavior<Villager> {



    private State state;
    private GlobalPos home;
    private Vec3 homeVec;
    private BlockPos decorationFloor;
    private BlockPos decorationSpot;
    private BlockPos lootSpot;
    private Vec3 lookSpotVec;
    private Direction direction;
    private BlockPos base;


    //those states do be finite
    private enum State {
        MOVING_TO_HOME,
        MOVING_TO_LOOK_SPOT,
        FINDING_SPOT,
        MOVING_TO_DECORATION_SPOT,
        DECORATING,
        FINISHED
    }


    public DecorateRoomTask(){
        super(
                ImmutableMap.of(
                        MemoryModuleType.HOME, MemoryStatus.VALUE_PRESENT,
                        MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED
                ),
                20 * 20
        );

    }


    public boolean checkExtraStartConditions(ServerLevel level, Villager villager){

        if(villager.isTrading()){
            return false;
        }

        List<ItemStack> decorations = villager.getAttached(AttachmentTypes.DECORATION_LIST);

        if(decorations == null || decorations.isEmpty()){
            return false;
        }

        Optional<GlobalPos> homeOpt = villager.getBrain().getMemory(MemoryModuleType.HOME);
        if(homeOpt.isPresent()){
            GlobalPos pos = homeOpt.get();

            return pos.dimension() == level.dimension();

        }


        return false;

    }

    public boolean canStillUse(final ServerLevel level, final Villager villager, final long timestamp){

        return this.state != State.FINISHED;

    }

    public void start(ServerLevel level, Villager villager, long timestamp){


        this.state = State.MOVING_TO_HOME;
        this.decorationFloor = null;
        this.decorationSpot = null;
        this.direction = null;
        this.lootSpot = null;
        this.lookSpotVec = null;
        this.homeVec = null;

        this.home = villager.getBrain().getMemory(MemoryModuleType.HOME).orElse(null);
        if(this.home == null){
            this.state = State.FINISHED;
            return;
        }

        this.homeVec = Vec3.atBottomCenterOf(this.home.pos());

        villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(this.home.pos(), 0.5f, 2));

    }

    public void tick(final ServerLevel level, final Villager villager, final long timestamp){


        switch(this.state){

            case MOVING_TO_HOME -> {

                double distanceSq = villager.distanceToSqr(this.homeVec);
                if(distanceSq < 49 && distanceSq > 40){
                    this.lookSpotVec = villager.position();
                    return;

                }else if (villager.getBrain().getMemory(MemoryModuleType.WALK_TARGET).isEmpty()) {
                    this.setTarget(villager, this.home.pos(), 2);
                    return;
                }

                if(distanceSq < 5){
                    if(this.lookSpotVec == null){ // catching the case where the villager begins the task already near its bed
                        this.state = State.FINDING_SPOT;
                        return;
                    }
                    villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(this.lookSpotVec, 0.5f, 1));
                    this.state = State.MOVING_TO_LOOK_SPOT;
                } else if (villager.getBrain().getMemory(MemoryModuleType.WALK_TARGET).isEmpty()) {
                    this.setTarget(villager, this.home.pos(), 2);
                }
            }

            case MOVING_TO_LOOK_SPOT -> {

                if(villager.distanceToSqr(this.lookSpotVec) < 1.2){
                    this.state = State.FINDING_SPOT;
                }else if(villager.getBrain().getMemory(MemoryModuleType.WALK_TARGET).isEmpty()){
                    villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(this.lookSpotVec, 0.5f, 1));
                }


            }

            case FINDING_SPOT -> {
                DecorationResult result = Social_contract.getDecorationSpot(villager.getEyePosition(), villager.getRandom(), level);
                if(result == null || result.decorationSpot() == null || result.decorationFloor() == null || result.direction() == null){
                    this.state = State.FINISHED;
                    return;
                }
                this.decorationSpot = result.decorationSpot();
                this.decorationFloor = result.decorationFloor();
                this.direction = result.direction();
                this.base = result.base();

                this.setTarget(villager, this.decorationFloor, 1);
                this.state = State.MOVING_TO_DECORATION_SPOT;
            }

            case MOVING_TO_DECORATION_SPOT -> {

                if (villager.distanceToSqr(Vec3.atBottomCenterOf( this.decorationFloor)) < 2.0) {
                    this.state = State.DECORATING;
                } else if (villager.getBrain().getMemory(MemoryModuleType.WALK_TARGET).isEmpty()) {
                    this.setTarget(villager, this.decorationFloor, 1);
                }
            }

            case DECORATING -> {

                ItemStack decoration = getNextDecoration(villager);

                if(decoration == null || decoration.is(Items.AIR)){
                    this.state = State.FINISHED;
                    return;
                }

                if(decoration.is(Items.PAINTING)){
                    Optional<Holder.Reference<PaintingVariant>> paintingOpt = level.registryAccess().lookupOrThrow(Registries.PAINTING_VARIANT).getRandom(villager.getRandom());
                    if(paintingOpt.isPresent()){
                        Optional<Painting> painting = Painting.create(level, this.decorationSpot, this.direction);
                        if(painting.isPresent()) {
                            level.addFreshEntity(painting.get());
                        }
                    }
                }else{
                    level.setBlock(decorationFloor, Block.byItem(decoration.getItem()).defaultBlockState(), 3);
                }

                villager.playSound(SoundEvents.VILLAGER_CELEBRATE);

                this.state = State.FINISHED;

            }

        }


    }

    public void stop(final ServerLevel level, final Villager villager, final long timestamp){

        Social_contract.LOGGER.info("stopping task");
        villager.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);

    }

    public static ItemStack getNextDecoration(Villager villager){

        List<ItemStack> decorationList = villager.getAttached(AttachmentTypes.DECORATION_LIST);

        if(decorationList != null ){

            if(!decorationList.isEmpty()){
                try {
                    ItemStack itemStack = decorationList.removeFirst();


                    if (itemStack != null) {

                        ItemStack newItemStack = itemStack.split(1);

                        if (!itemStack.is(Items.AIR)) {
                            decorationList.addLast(itemStack);
                            villager.setAttached(AttachmentTypes.DECORATION_LIST, decorationList);
                        }

                        if (newItemStack.is(Items.AIR)) {
                            return null;
                        } else {
                            return newItemStack;
                        }

                    }

                } catch (Exception e) { // weird things can happen when the game closes in the middle of this task running
                    // where the list gets treated as immutable, this catches it
                    return null;
                }

            }


        }

        return null;

    }

    private void setTarget(Villager villager, BlockPos pos, int radius) {
        // Keep the speed a bit higher (0.5f is standard, can be pushed to 0.6f if they are sluggish)
        villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(pos, 0.5f, radius));
    }


}
