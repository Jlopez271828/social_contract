package jlopez271828.social_contract.behavior;

import com.google.common.collect.ImmutableMap;
import jlopez271828.social_contract.Social_contract;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

public class HealSelfTask extends Behavior<Villager> {

    private static Logger logger = Social_contract.LOGGER;

    public HealSelfTask(){
        super(ImmutableMap.of());

    }

    public static boolean shouldHeal(final Villager villager){
        return villager.getHealth() < villager.getMaxHealth() && villager.getInventory().hasAnyOf(Villager.FOOD_POINTS.keySet()) && !villager.isPanicking();
    }


    public boolean checkExtraStartConditions(final ServerLevel level, final Villager villager){

        return shouldHeal(villager);
    }

    protected boolean canStillUse(final ServerLevel level, final Villager villager, final long timestamp) {
        return this.checkExtraStartConditions(level, villager);
    }

    protected void tick(final ServerLevel level, final Villager villager, final long timestamp){



        SimpleContainer inventory = villager.getInventory();

        for(int slot = 0; slot < inventory.getContainerSize(); slot++){
            ItemStack itemStack = inventory.getItem(slot);
            logger.info("item stack: {}", itemStack);
            if(!itemStack.isEmpty()){
                Integer value = Villager.FOOD_POINTS.get(itemStack.getItem());
                if(value != null){
                    int count = itemStack.count();

                    villager.playSound(SoundEvents.GENERIC_EAT.value());

                    for(int i = count; i > 0; i--){

                        villager.heal(value);
                        inventory.removeItem(slot, 1);

                        if(!shouldHeal(villager)){
                            return;
                        }


                    }

                }
            }
        }

    }


}
