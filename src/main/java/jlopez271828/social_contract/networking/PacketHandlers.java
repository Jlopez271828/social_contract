package jlopez271828.social_contract.networking;

import jlopez271828.social_contract.Happiness;
import jlopez271828.social_contract.Social_contract;
import jlopez271828.social_contract.VillagerGiftSlot;
import jlopez271828.social_contract.mixin.VillagerAccessor;
import jlopez271828.social_contract.types.AttachmentTypes;
import jlopez271828.social_contract.types.CustomActivities;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.Slot;
import org.slf4j.Logger;

import java.util.Optional;


public class PacketHandlers {

    private static final Logger logger = Social_contract.LOGGER;

    public static void initialize(){

        PayloadTypeRegistry.serverboundPlay().register(ServerBoundFollowRequestPayload.TYPE, ServerBoundFollowRequestPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ServerBoundGiveGiftPayload.TYPE, ServerBoundGiveGiftPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ServerBoundFollowStopPayload.TYPE, ServerBoundFollowStopPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ClientBoundVillagerInfoPayload.TYPE, ClientBoundVillagerInfoPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ClientBoundFollowConfirmPayload.TYPE, ClientBoundFollowConfirmPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ClientBoundFollowStopConfirmPayload.TYPE, ClientBoundFollowStopConfirmPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ServerBoundFollowRequestPayload.TYPE,
                (payload, context) -> {

                    logger.info("received entityId: {} from a server bound packet", payload.entityId());

                    Player player = context.player();

                    int entityId = payload.entityId();

                    //error state
                    if(entityId < 0){
                        logger.warn("recieved error code from server bound packet");
                        return;
                    }

                    Entity entity = player.level().getEntity(entityId);

                    if(entity instanceof Villager villager && player instanceof ServerPlayer serverPlayer){
//                        logger.info("found requested villager entity");
                        int reputation = villager.getPlayerReputation(player);
                        logger.info("reputation: {}", reputation);

                        // This button will also be a convenient way to force a room rescore.
                        if(reputation >= Social_contract.MIN_FOLLOW_REPUTATION && Happiness.check(villager, Social_contract.MIN_FOLLOW_HAPPINESS, true, false)){

//                            logger.info("this player meets the requirements");
                            villager.playSound(SoundEvents.VILLAGER_CELEBRATE);
                            Brain<?> brain = villager.getBrain();
                            villager.setAttached(AttachmentTypes.PLAYER_TO_FOLLOW, serverPlayer);
                            brain.setActiveActivityIfPossible(CustomActivities.FOLLOW_FRIEND);
//                            player.containerMenu.removed(player);
                            ((VillagerAccessor) villager).social_contract$stopTrading(); //I wonder which method is more elegant
                            if(brain.isActive(CustomActivities.FOLLOW_FRIEND)){
                                logger.info("succeeded in setting activity");
                                ServerPlayNetworking.send(serverPlayer,  new ClientBoundFollowConfirmPayload(villager.getId()));
                            }else{
                                logger.info("failed in setting activity");
                            }
                            //The reason the above function is 'IfPossible' is for two possible cases:
                            // 1. the activity is simply never registered
                            // 2. an activity has start conditions (memories that need to be present)
                        }else{
                            logger.info("this player does not meet the requirements");
                            entity.playSound(SoundEvents.VILLAGER_NO);
                        }

                    }else if(entity instanceof WanderingTrader){
                        entity.playSound(SoundEvents.VILLAGER_NO);
                    }

                }
        );


        ServerPlayNetworking.registerGlobalReceiver(ServerBoundGiveGiftPayload.TYPE,
                (payload, context) -> {

                    Player player = context.player();
                    Entity entity = player.level().getEntity(payload.entityId());
                    AbstractContainerMenu playerMenu = player.containerMenu;
                    ServerLevel serverLevel = context.player().level();

                    if(playerMenu.containerId != payload.containerId()){
                        logger.warn("attested containerId and found containerId do not match");
                        return;
                    }

                    if(entity instanceof Villager villager && playerMenu instanceof MerchantMenu menu && menu.slots.size() >= 40){
                        Slot slot = menu.getSlot(39);
                        if(slot instanceof VillagerGiftSlot giftSlot){
                            giftSlot.acceptGift(villager);
                        }

                    }else{
                        logger.warn("something went wrong, entity: {}, playerMenu: {}, slot size: {}", entity, playerMenu, playerMenu.slots.size());
                    }

                }
        );

        ServerPlayNetworking.registerGlobalReceiver(ServerBoundFollowStopPayload.TYPE, (payload, context) -> {

            Player player = context.player();
            Entity entity = player.level().getEntity(payload.entityId());

            if(player instanceof ServerPlayer serverPlayer) {
                if (entity instanceof Villager villager) {

                    Brain<?> brain = villager.getBrain();

                    Optional<Activity> optional = brain.getActiveNonCoreActivity();

                    if (optional.isPresent() && optional.get().equals(CustomActivities.FOLLOW_FRIEND)) {

                        brain.setActiveActivityIfPossible(Activity.IDLE);

                        Optional<Activity> newOptional = brain.getActiveNonCoreActivity();
                        if(newOptional.isPresent() && newOptional.get().equals(CustomActivities.FOLLOW_FRIEND)){
                            //we fucked up
                            logger.error("Activity was not reset to idle");
                            return;

                        }

                    }

                }

                ServerPlayNetworking.send(serverPlayer, new ClientBoundFollowStopConfirmPayload(false));

            }

        });

        ClientPlayNetworking.registerGlobalReceiver(ClientBoundVillagerInfoPayload.TYPE, (payload, context) -> {

            Player player = context.player();

            player.setAttached(AttachmentTypes.EXTRA_VILLAGER_MENU_DATA_ATTACHMENT, payload);

        });

        ClientPlayNetworking.registerGlobalReceiver(ClientBoundFollowConfirmPayload.TYPE, (payload, context) -> {

            Player player = context.player();
            int entityId = payload.entityId();

            player.setAttached(AttachmentTypes.HAS_VILLAGER_FOLLOWING, entityId);


        });

        ClientPlayNetworking.registerGlobalReceiver(ClientBoundFollowStopConfirmPayload.TYPE, (payload, context) -> {

            Player player = context.player();

            player.removeAttached(AttachmentTypes.HAS_VILLAGER_FOLLOWING);

        });

    }





}
