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


//    @WrapMethod(method = "openTradingScreen")
//    default void overrideOpenTradingScreen(Player player, Component title, int level, Operation<Void> original){
//        if((Object) this instanceof Villager){
//            OptionalInt containerId = player.openMenu(new SimpleMenuProvider((id, inventory, p) -> new VillagerMenu(id, inventory, (Villager) (Object) this), title));
//            if (containerId.isPresent()) {
//                MerchantOffers offers = this.getOffers();
//                if (!offers.isEmpty()) {
//                    player.sendMerchantOffers(containerId.getAsInt(), offers, level, this.getVillagerXp(), this.showProgressBar(), this.canRestock());
//                }
//            }
//        }else{
//            original.call(player, title, level);
//        }
//    }

//    @Inject(method = "openTradingScreen",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/world/entity/player/Player;sendMerchantOffers(ILnet/minecraft/world/item/trading/MerchantOffers;IIZZ)V"
//            )
//    )
//    default void constrainOffers(Player player, Component title, int level, CallbackInfo ci, @Local(name = "offers") MerchantOffers offers){
//
//        if(this instanceof Villager villager){
//
//            offers = Social_contract.constrainOffers(villager, offers);
//
//        }
//
//    }

    @ModifyArg(
            method = "openTradingScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;sendMerchantOffers(ILnet/minecraft/world/item/trading/MerchantOffers;IIZZ)V"
            ),
            index = 1
    )
    default MerchantOffers constrainOffers(MerchantOffers offers){

        if(this instanceof Villager villager){

            Social_contract.LOGGER.info("constraining offers packet");
            Social_contract.LOGGER.info("offers before constraint: {}", offers);
            //I wonder If I can make this more efficient
            // currently it creates a new MerchantOffers with the constraints applied upon the original
            // However, I wonder if this method count instead return an integer corresponding to how many offers need to
            // be removed from the tail. Someone should implement this and test it, and make a GitHub issue about it if
            // it is truly faster & more memory efficient.
            offers = Social_contract.constrainOffers(villager, offers);
            Social_contract.LOGGER.info("offers after constraint: {}", offers);

        }

        return offers;
    }




}
