package jlopez271828.social_contract.mixin;

import jlopez271828.social_contract.Happiness;
import jlopez271828.social_contract.Social_contract;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantResultSlot.class)
public class MerchantResultSlotMixin {


    @Shadow @Final private Merchant merchant;



    @Inject(
            method = "onTake",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/resources/Identifier;)V"
            )
    )
    private void onTrade(Player player, ItemStack carried, CallbackInfo ci){
        //this gets ran twice, once on client, and once on server
        if(this.merchant instanceof Villager villager){
            Happiness.increaseHappiness(Social_contract.HAPPINESS_FOR_TRADE, villager, Happiness.HappinessType.TRADE);
        }
    }


}
