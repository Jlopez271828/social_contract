package jlopez271828.social_contract;

import jlopez271828.social_contract.mixin.VillagerExtendedData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.villager.Villager;

public class FollowFriendGoal {

    public static OneShot<PathfinderMob> create(float speedModifier){

        return BehaviorBuilder.create(i -> i.group(i.registered(MemoryModuleType.WALK_TARGET)).apply(i, walkTarget -> (level, body, timestamp) -> {
            if(!(body instanceof Villager)){
                return true;
            }
            Entity player = level.getEntity(((VillagerExtendedData) body).social_contract2$getPlayerToFollow());

            if(player == null){
                return true;
            }


            walkTarget.set(new WalkTarget(player.blockPosition(), speedModifier, 1));

            return true;

        }));

    }
}
