package jlopez271828.social_contract.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import jlopez271828.social_contract.Happiness;
import jlopez271828.social_contract.Social_contract;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.npc.villager.Villager;
import org.spongepowered.asm.mixin.Mixin;


/**
 * Secondary mixin class because I need to override the damage method for a villager
 * I don't think built-in fabric method will work because I don't think I can get the
 * value of damage done.
 */
@Mixin(Villager.class)
public class VillagerSubMixin extends LivingEntityMixin {

    @Override
    protected void overrideForVillager(ServerLevel level, DamageSource source, float dmg, Operation<Void> original) {
        original.call(level, source, dmg);
        Happiness.decreaseHappiness((int) (Social_contract.HAPPINESS_LOSS_DMG * dmg), (Villager) (Object) this);

    }


}
