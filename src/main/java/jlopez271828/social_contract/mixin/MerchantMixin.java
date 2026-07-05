package jlopez271828.social_contract.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import jlopez271828.social_contract.Social_contract;
import jlopez271828.social_contract.networking.ClientBoundMerchantInfoPayload;
import jlopez271828.social_contract.old.VillagerMenu;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.OptionalInt;

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

    @Inject(method = "openTradingScreen",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/item/trading/Merchant;getOffers()Lnet/minecraft/world/item/trading/MerchantOffers;"
            )
    )
    default void sendMerchantInfo(Player player, Component title, int level, CallbackInfo ci, @Local(name = "containerId") OptionalInt containerId){
        if(this instanceof LivingEntity entity && player instanceof ServerPlayer serverPlayer){
            Social_contract.LOGGER.info("SENDMERCHANTINFO: this is a living entity");
            ServerPlayNetworking.send(serverPlayer, new ClientBoundMerchantInfoPayload(entity.getId(), containerId.getAsInt()));
        }
    }




}
