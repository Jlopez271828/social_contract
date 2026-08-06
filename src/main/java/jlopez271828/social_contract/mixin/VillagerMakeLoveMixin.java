package jlopez271828.social_contract.mixin;

import jlopez271828.social_contract.Happiness;
import jlopez271828.social_contract.Social_contract;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.VillagerMakeLove;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.block.state.properties.Half;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//Best named class in the entire source
@Mixin(VillagerMakeLove.class)
public class VillagerMakeLoveMixin {

    //We are using a mixin here instead of the Villager's `canBreed()` method because this ensures we only preform a check when
    // Absolutley neccessary.
    @Inject(method = "tryToGiveBirth", at = @At("HEAD"), cancellable = true)
    private void checkBreedingRequirements(ServerLevel level, Villager body, Villager target, CallbackInfo ci){

        if(!Happiness.check(body, Social_contract.MIN_BREED_HAPPINESS) || !Happiness.check(target, Social_contract.MIN_BREED_HAPPINESS)){
            level.broadcastEntityEvent(target, (byte)13);
            level.broadcastEntityEvent(body, (byte)13);
            ci.cancel();
        }

    }

}
