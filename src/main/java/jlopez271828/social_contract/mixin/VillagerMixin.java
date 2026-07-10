package jlopez271828.social_contract.mixin;


import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import jlopez271828.social_contract.behavior.CustomGoalPackages;
import jlopez271828.social_contract.networking.ClientBoundMerchantInfoPayload;
import jlopez271828.social_contract.types.AttachmentTypes;
import jlopez271828.social_contract.types.CustomActivities;
import jlopez271828.social_contract.types.CustomReputationEventTypes;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.ActivityData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import jlopez271828.social_contract.types.CustomSensorTypes;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager  {

    VillagerMixin(final EntityType<? extends Villager> type, final Level level){
        super(type, level);
    }

    @Shadow
    public abstract Brain<?> getBrain();

    @Shadow
    @Final
    private GossipContainer gossips;

    @ModifyArgs(method = "<clinit>",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/Brain;provider(Ljava/util/Collection;Lnet/minecraft/world/entity/ai/Brain$ActivitySupplier;)Lnet/minecraft/world/entity/ai/Brain$Provider;"
            )
    )
    private static void addCustomVillagerSensors(Args args){
        Collection<SensorType<?>> altered = new ArrayList<SensorType<?>>(args.get(0));
        altered.add(CustomSensorTypes.ROOM_SCORE_SENSOR);
        args.set(0, altered);


    }

    @ModifyReturnValue(method = "lambda$static$0",
            at = @At("RETURN")
    )
    private static List registerCustomActivity(List original){
        original.add(ActivityData.create(CustomActivities.FOLLOW_FRIEND, CustomGoalPackages.getFollowPackage()));

        return original;
    }


    @Inject(method = "releaseAllPois", at = @At(value = "HEAD"))
    private void clearBedAttachments(CallbackInfo ci){

        Logger logger = LoggerFactory.getLogger("social_contract");

        Brain<?> brain = this.getBrain();

        GlobalPos gpos = brain.getMemory(MemoryModuleType.HOME).orElse(null);

        if(gpos != null){
            if(this.level().dimension() == gpos.dimension()){
                if(this.hasAttached(AttachmentTypes.BED_OWNER_ATTACHMENT)){
                    this.removeAttached(AttachmentTypes.BED_OWNER_ATTACHMENT);
                }
            }
        }


    }

    @Inject(method = "onReputationEventFrom", at = @At("HEAD"))
    private void extraEntityEvents(ReputationEventType type, Entity source, CallbackInfo ci){
        if(type == CustomReputationEventTypes.PLACED_BED_CLAIMED){
            this.gossips.add(source.getUUID(), GossipType.MAJOR_POSITIVE, 10);
        }

    }

    @Inject(method = "startTrading",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/npc/villager/Villager;openTradingScreen(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/network/chat/Component;I)V")
    )
    private void sendExtraPacket(Player player, CallbackInfo ci){

        if(player instanceof ServerPlayer sp) {
            ServerPlayNetworking.send(sp, new ClientBoundMerchantInfoPayload(this.getId(), 0));
        }
    }


}
