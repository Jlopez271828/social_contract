package jlopez271828.social_contract.mixin;

import net.minecraft.world.entity.npc.villager.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Villager.class)
public interface VillagerAccessor {

    @Invoker("stopTrading")
    void social_contract$stopTrading();

    @Invoker("shouldIncreaseLevel")
    boolean social_contract$shouldIncreaseLevel();

    @Invoker("releaseAllPois")
    void social_contract$releaseAllPois();

    @Accessor("increaseProfessionLevelOnUpdate")
    void social_contract$increaseProfessionLevelOnUpdate(boolean state);

    @Accessor("updateMerchantTimer")
    void social_contract$setUpdateMerchantTimer(int time);





}
