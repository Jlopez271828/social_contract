package jlopez271828.social_contract.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.datafixers.util.Pair;
import jlopez271828.social_contract.Social_contract;
import jlopez271828.social_contract.behavior.CloseDoorsTask;
import jlopez271828.social_contract.behavior.DecorateRoomTask;
import jlopez271828.social_contract.behavior.HealSelfTask;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.npc.villager.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(VillagerGoalPackages.class)
public class VillagerGoalPackagesMixin {

    @ModifyReturnValue(method = "getCorePackage", at = @At("RETURN"))
    private static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> addCustomCoreGoals(ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> original){

        List<Pair<Integer, ? extends BehaviorControl<? super Villager>>> temp = new ArrayList<>(original);

        temp.addLast(Pair.of(1, new HealSelfTask()));


        return ImmutableList.copyOf(temp);
    }


    @ModifyReturnValue(method = "getIdlePackage", at = @At("RETURN"))
    private static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> addCustomIdleGoals(ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> original){

        List<Pair<Integer, ? extends BehaviorControl<? super Villager>>> toReturn = new ArrayList<>(original);

        toReturn.addLast(Pair.of(3, new DecorateRoomTask()));

        return ImmutableList.copyOf(toReturn);
    }

    @ModifyReturnValue(method = "getRestPackage", at = @At("RETURN"))
    private static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> addCustomRestGoals(ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> original){

        Social_contract.LOGGER.info("injecting close doors task");

        List<Pair<Integer, ? extends BehaviorControl<? super Villager>>> temp = new ArrayList<>(original);

        //this is done to make sure that Villagers go to their bed first before closing all the doors, prevent them from
        // essentially locking themselves out of their own room.
//        for(int i = 1; i < original.size(); i++){
//
//            Pair<Integer, ? extends  BehaviorControl<? super Villager>> pair = original.get(i);
//
//            temp.add(Pair.of(pair.getFirst() + 1, pair.getSecond()));
//
//        }

        temp.addLast(Pair.of(4, new CloseDoorsTask()));


        return ImmutableList.copyOf(temp);
    }


}
