package jlopez271828.social_contract.old;


import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.npc.villager.Villager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(VillagerGoalPackages.class)
abstract class VillagerGoalPackagesMixin {

    private static final Logger logger = LoggerFactory.getLogger("social_contract");

    @ModifyReturnValue(
            method = "getCorePackage",
            at = @At("RETURN")
    )
    private static ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> extendCoreGoals(
            ImmutableList<Pair<Integer, ? extends BehaviorControl<? super Villager>>> original
    ) {
        return ImmutableList.<Pair<Integer, ? extends BehaviorControl<? super Villager>>>builder()
                .addAll(original)
                .build();
    }


}
