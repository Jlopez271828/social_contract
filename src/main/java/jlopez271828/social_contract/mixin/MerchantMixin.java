package jlopez271828.social_contract.mixin;

import jlopez271828.social_contract.Social_contract;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Merchant.class)
public interface MerchantMixin {



    @Shadow
    MerchantOffers getOffers();

    @Shadow
    int getVillagerXp();

    @Shadow
    boolean showProgressBar();

    @Shadow
    boolean canRestock();


    @ModifyArg(
            method = "openTradingScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;sendMerchantOffers(ILnet/minecraft/world/item/trading/MerchantOffers;IIZZ)V"
            ),
            index = 1
    )
    default MerchantOffers constrainOffers(MerchantOffers offers){

        int numAvailableOffers = 0;

        if(this instanceof Villager villager){

            numAvailableOffers = Social_contract.constrainOffers(villager, offers);


        }

//        MerchantOffers newOffers = new MerchantOffers();
//        newOffers.addAll(offers.subList(0, numAvailableOffers - 1));

        return offers;
    }




}
