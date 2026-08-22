package jlopez271828.social_contract.behavior;

import com.google.common.collect.ImmutableMap;
import jlopez271828.social_contract.CustomBlockStateProperties;
import jlopez271828.social_contract.CustomBlocks;
import jlopez271828.social_contract.Social_contract;
import jlopez271828.social_contract.types.AttachmentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class LightVillagerLight extends Behavior<Villager> {

    private BlockPos target;
    private Vec3 floorTarget;
    private boolean hasLit = false;

    public LightVillagerLight(){

        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED), 20 * 10);

    }




    public boolean checkExtraStartConditions(ServerLevel level, Villager villager){

        List<GlobalPos> known_lights = villager.getAttached(AttachmentTypes.KNOWN_VILLAGER_LIGHTS);

        return known_lights != null && !known_lights.isEmpty();

    }

    public boolean canStillUse(ServerLevel level, Villager villager, long timestamp){
        return this.target != null && !this.hasLit;
    }


    public void start(ServerLevel level, Villager villager, long timestamp){

        try {

            List<GlobalPos> known_lights = villager.getAttached(AttachmentTypes.KNOWN_VILLAGER_LIGHTS);
            if (known_lights == null || known_lights.isEmpty()) {
                this.hasLit = true;
                return;
            }

            this.hasLit = false;

            this.target = null;

            for (int i = 0; i < known_lights.size(); i++) {

                GlobalPos temp = known_lights.get(i);

                if (temp.dimension() != level.dimension()) {
                    continue;
                }

                BlockState state = level.getBlockState(temp.pos());

                if (state.is(CustomBlocks.VILLAGER_LIGHT_BLOCKITEM) && state.getValue(CustomBlockStateProperties.LIGHT_POWER) < 3) {
                    this.target = temp.pos();
                    this.floorTarget = Vec3.atBottomCenterOf(Social_contract.getFloorBelow(temp.pos(), level));
                    villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(this.floorTarget, 0.5f, 1));
                    return;
                }

            }

            if (this.target == null) {
                this.hasLit = true;
                return;
            }

        }catch (Exception e){
            Social_contract.LOGGER.info("somemething weirdge happened");
            this.hasLit = true;
            return;
        }



    }

    public void tick(ServerLevel level, Villager villager, long timestamp){

        if(villager.distanceToSqr(this.floorTarget) < 2 * 2 - 1){
            BlockState state = level.getBlockState(this.target);
            int lightPower = state.getValue(CustomBlockStateProperties.LIGHT_POWER);
            level.setBlock(this.target, state.setValue(CustomBlockStateProperties.LIGHT_POWER, 3), 2);
            if(lightPower == 0){
                level.scheduleTick(this.target, CustomBlocks.VILLAGER_LIGHT_BLOCKITEM, 10);
            }
            villager.playSound(SoundEvents.FLINTANDSTEEL_USE);
            this.hasLit = true;
        }


    }









}
