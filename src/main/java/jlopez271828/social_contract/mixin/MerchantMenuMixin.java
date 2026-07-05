package jlopez271828.social_contract.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import jlopez271828.social_contract.MerchantMenuExtra;
import jlopez271828.social_contract.Social_contract;
import jlopez271828.social_contract.VillagerGiftSlot;
import jlopez271828.social_contract.networking.ServerBoundFollowRequestPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.ClientSideMerchant;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.trading.Merchant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(MerchantMenu.class)
public abstract class MerchantMenuMixin extends AbstractContainerMenu implements MerchantMenuExtra {

    private static Logger logger = LoggerFactory.getLogger(Social_contract.MOD_ID);

    @Shadow @Final private MerchantContainer tradeContainer;

    protected MerchantMenuMixin(MenuType<?> type, int containerID){
        super(type, containerID);
    }

    @Unique
    UUID traderUUID = null;

    @Override
    public UUID social_contract$getTraderUUID(){
        return this.traderUUID;
    }

    @Override
    public void social_contract$setTraderUUID(UUID id){
        this.traderUUID = id;
    }

    @Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/item/trading/Merchant;)V",
        at = @At("TAIL")
    )
    protected void injectSlots(int containerId, Inventory inventory, Merchant merchant, CallbackInfo ci){

//        if(merchant instanceof Villager){
//            logger.info("this merchant is a villager");
//
//        }else{
//            logger.info("this merchant is not a villager");
//        }

        //this constructor should run twice, once for the server and once for the client, the only difference between the two being
        // that the merchant on the client side is a ClientSideMerchant whereas the merchant on the Server side is the actual entity,
        // be it Villager or Wondering trader, and a ClientSideMerchant is not a LivingEntity
        if(merchant instanceof LivingEntity entity){
            logger.info("this code is running on the server");
            int id = entity.getId();

        }

        this.addSlot(new VillagerGiftSlot(this.tradeContainer, 3, 220, 64));



    }

//    @WrapMethod(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/item/trading/Merchant;)V")
//    protected void ConstructorMixin(int containerId, Inventory inventory, Merchant merchant, Operation<Void> original){
//
//
//    }



}
