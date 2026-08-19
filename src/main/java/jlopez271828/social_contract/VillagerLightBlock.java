package jlopez271828.social_contract;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class VillagerLightBlock extends Block implements SimpleWaterloggedBlock {

    public static final MapCodec<VillagerLightBlock> CODEC = simpleCodec(VillagerLightBlock::new);
    public static final IntegerProperty LIGHT_POWER = CustomBlockStateProperties.LIGHT_POWER;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_4;
    public static final BooleanProperty HANGING = BlockStateProperties.HANGING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<Direction.Axis> AXIS = EnumProperty.create(
            "axis_xz",
            Direction.Axis.class,
            Direction.Axis.X,
            Direction.Axis.Z
    );
//    private static final VoxelShape SHAPE_STANDING = Shapes.or(Block.column(4.0, 8.0, 10.0), Block.column(6.0, 1.0, 8.0));
//    private static final VoxelShape SHAPE_HANGING = SHAPE_STANDING.move(0.0, 0.0625, 0.0).optimize();
    public static final VoxelShape SHAPE = Shapes.or(Block.column(4.0, 8.0, 10.0), Block.column(6.0, 1.0, 8.0));

    public VillagerLightBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState()
                .setValue(LIGHT_POWER, 0)
                .setValue(AGE, 0)
                .setValue(HANGING, false)
                .setValue(WATERLOGGED, false)
                .setValue(AXIS, Direction.Axis.X)
        );
    }

    @Override
    protected void onPlace(final BlockState state, final Level level, final BlockPos pos, final BlockState oldState, final boolean movedByPiston) {

//        level.scheduleTick(pos, this, 10);

    }

    @Override
    protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random){

        if (level.isClientSide()) return;

        int lightPower = state.getValue(LIGHT_POWER);

        if(lightPower > 0) {


            AABB box = new AABB(pos).inflate(lightPower * 3);

            List<Zombie> zombieList = level.getEntitiesOfClass(Zombie.class, box, zombie -> zombie.isAlive() && !zombie.isOnFire());

            for (Zombie zombie : zombieList) {

                zombie.igniteForSeconds(3);


                zombie.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 300, lightPower));


            }

            level.scheduleTick(pos, this, 10);

        }





    }


    @Override
    protected void randomTick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {

        int lightPower = state.getValue(LIGHT_POWER);

        if(lightPower > 0) {

            int age = state.getValue(AGE);

//            level.setBlock(pos, state.setValue(LIGHT_POWER, lightPower - 1), Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS | Block.UPDATE_IMMEDIATE | Block.UPDATE_SUPPRESS_DROPS);

            if (age == 4) {

                level.setBlock(pos, state.setValue(LIGHT_POWER, lightPower - 1), Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS | Block.UPDATE_IMMEDIATE | Block.UPDATE_SUPPRESS_DROPS);

                level.setBlock(pos, state.setValue(AGE, 0), 260);
            } else {
                level.setBlock(pos, state.setValue(AGE, age + 1), 260);
            }

        }

    }

    @Override
    public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
        FluidState replacedFluidState = context.getLevel().getFluidState(context.getClickedPos());

        boolean upDownFlag = false;
        boolean cardinalFlag = false;

        BlockState state = this.defaultBlockState();


        for (Direction direction : context.getNearestLookingDirections()) {

            if(upDownFlag && cardinalFlag){
                return state.setValue(WATERLOGGED, replacedFluidState.is(Fluids.WATER));
            }

            switch (direction.getAxis()) {

                case Direction.Axis.Y -> {

                    state = state.setValue(HANGING, direction == Direction.UP);
                    if (state.canSurvive(context.getLevel(), context.getClickedPos())) {
                        upDownFlag = true;
                    }

                }

                case Direction.Axis.Z, Direction.Axis.X -> {

                    state = state.setValue(AXIS, direction.getAxis());
                    if (state.canSurvive(context.getLevel(), context.getClickedPos())) {
                        cardinalFlag = true;
                    }

                }


            }


        }

        return null;
    }

    @Override
    protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
        return SHAPE;
    }


    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIGHT_POWER, AGE, HANGING, WATERLOGGED, AXIS);
    }

    @Override
    protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
        Direction direction = getConnectedDirection(state).getOpposite();
        return Block.canSupportCenter(level, pos.relative(direction), direction.getOpposite());
    }

    protected static Direction getConnectedDirection(final BlockState state) {
        return state.getValue(HANGING) ? Direction.DOWN : Direction.UP;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        int lightPower = state.getValue(LIGHT_POWER);

        level.setBlock(pos, state.setValue(LIGHT_POWER, (lightPower + 1) % 4), Block.UPDATE_CLIENTS | Block.UPDATE_NEIGHBORS | Block.UPDATE_IMMEDIATE | Block.UPDATE_SUPPRESS_DROPS);

        if(lightPower == 0){ // we only begin ticking if the light is on
            level.scheduleTick(pos, this, 10);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return useWithoutItem(state, level, pos, player, hitResult);
    }
}
