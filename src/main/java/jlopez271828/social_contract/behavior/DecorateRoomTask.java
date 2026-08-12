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
import net.minecraft.world.level.pathfinder.Path;

import java.util.List;
import java.util.Optional;

public class DecorateRoomTask extends Behavior<Villager> {

    private boolean has_reached_home = false;
    private boolean has_reach_decoration_spot = false;
    private boolean can_stop = false;

    private State STATE;
    private GlobalPos home;
    private BlockPos decorationFloor;
    private BlockPos decorationSpot;
    private Direction direction;
    private BlockPos base;

    private enum State {
        MOVING_TO_HOME,
        FINDING_SPOT,
        MOVING_TO_SPOT,
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
            Path path = villager.getNavigation().createPath(pos.pos(), 2);

            return pos.dimension() == level.dimension() && path != null && path.canReach();

        }


        return false;

    }

    public boolean canStillUse(final ServerLevel level, final Villager villager, final long timestamp){

        return !can_stop;

    }

    public void start(ServerLevel level, Villager villager, long timestamp){

        Optional<GlobalPos> home = villager.getBrain().getMemory(MemoryModuleType.HOME);
        this.has_reached_home = false;
        this.has_reach_decoration_spot = false;
        this.can_stop = false;
        this.decorationFloor = null;
        this.decorationSpot = null;
        this.direction = null;

        if(home.isPresent()){
            this.home = home.get();
        }else{
            this.stop(level, villager, timestamp);
        }

        Social_contract.LOGGER.info("starting task");
        villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(this.home.pos(), 0.5f, 3));

    }

    public void tick(final ServerLevel level, final Villager villager, final long timestamp){


        if(this.has_reached_home){

            if(this.has_reach_decoration_spot){

                ItemStack decoration = getNextDecoration(villager);

                if(decoration == null || decoration.is(Items.AIR)){
                    this.stop(level, villager, timestamp);
                    return;
                }

                if(decoration.is(Items.PAINTING)){
                    Optional<Holder.Reference<PaintingVariant>> paintingOpt = level.registryAccess().lookupOrThrow(Registries.PAINTING_VARIANT).getRandom(villager.getRandom());
                    if(paintingOpt.isPresent()){
                        Holder<PaintingVariant> holder = paintingOpt.get();
//                        Painting painting = new Painting(level, this.decorationSpot, this.direction, holder);
                        Optional<Painting> painting = Painting.create(level, this.decorationSpot, this.direction);
                        if(painting.isPresent()) {
                            level.addFreshEntity(painting.get());
                        }
                    }
                }else{
                    level.setBlock(decorationFloor, Block.byItem(decoration.getItem()).defaultBlockState(), 3);
                }

                this.can_stop = true;


            }else {

                if(villager.distanceToSqr(this.decorationFloor.getBottomCenter()) < 3){
                    this.has_reach_decoration_spot = true;
                }

            }

        }else{
            if(villager.distanceToSqr(this.home.pos().getBottomCenter()) < 5){

                DecorationResult result = Social_contract.getDecorationSpot(villager.getEyePosition(), villager.getRandom(), level);
                if(result == null){
                    this.stop(level, villager, timestamp);
                    return;
                }
                this.decorationSpot = result.decorationSpot();
                this.decorationFloor = result.decorationFloor();
                this.direction = result.direction();
                this.base = result.base();
                if (this.decorationSpot == null) {
                    Social_contract.LOGGER.info("could not find decoration spot");
                    this.stop(level, villager, timestamp);
                    return;
                }

                Social_contract.LOGGER.info("found decoration spot at {}", decorationSpot);

                if (this.decorationFloor == null) {
                    Social_contract.LOGGER.info("could not find floor below");
                    this.stop(level, villager, timestamp);
                    return;
                }

                Social_contract.LOGGER.info("found decoration floor at {}", decorationFloor);

                Path path = villager.getNavigation().createPath(this.decorationFloor, 1);

                if (path == null) {
                    Social_contract.LOGGER.info("could not create path to decoration floor");
                    this.stop(level, villager, timestamp);
                    return;
                }

                villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(this.decorationFloor, 0.5f, 1));

                this.has_reached_home = true;
            }
        }

    }

    public void stop(final ServerLevel level, final Villager villager, final long timestamp){

        Social_contract.LOGGER.info("stopping task");
        this.has_reached_home = false;
        this.home = null;
        villager.removeAttached(AttachmentTypes.DECORATION_LIST);

    }

    public static ItemStack getNextDecoration(Villager villager){

        List<ItemStack> decorationList = villager.getAttached(AttachmentTypes.DECORATION_LIST);

        if(decorationList != null ){

            if(!decorationList.isEmpty()){
                ItemStack itemStack = decorationList.removeFirst();

                if(itemStack != null){

                    ItemStack newItemStack = itemStack.split(1);

                    if(!itemStack.is(Items.AIR)){
                        decorationList.addLast(itemStack);
                        villager.setAttached(AttachmentTypes.DECORATION_LIST, decorationList);
                    }

                    if(newItemStack.is(Items.AIR)){
                        return null;
                    }else{
                        return newItemStack;
                    }

                }

            }


        }

        return null;

    }


}
