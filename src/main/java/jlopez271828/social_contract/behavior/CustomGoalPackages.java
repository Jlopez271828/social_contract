package jlopez271828.social_contract.behavior;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.npc.villager.Villager;

public class CustomGoalPackages {

    public static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> getFollowPackage(){

        return ImmutableList.of(

                Pair.of(2, FollowFriendGoal.create(0.5f, 2, 4, 25, 1200))
        );

    }
}
