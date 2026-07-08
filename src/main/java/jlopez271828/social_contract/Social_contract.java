package jlopez271828.social_contract;

import com.mojang.datafixers.optics.Wander;
import jlopez271828.social_contract.networking.ClientBoundMerchantInfoPayload;
import jlopez271828.social_contract.networking.ServerBoundFollowRequestPayload;
import jlopez271828.social_contract.types.*;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Social_contract implements ModInitializer {
	public static final String MOD_ID = "social_contract";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final int MIN_FOLLOW_REPUTATION = 10;


	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

        CustomMemoryModuleType.initialize();
        CustomSensorTypes.initialize();
        CustomMenuTypes.initialize();
        ExtraVillagerScreenWidgets.initialize();
        CustomReputationEventTypes.initialize();
        CustomActivities.initialize();

        PayloadTypeRegistry.serverboundPlay().register(ServerBoundFollowRequestPayload.TYPE, ServerBoundFollowRequestPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ClientBoundMerchantInfoPayload.TYPE, ClientBoundMerchantInfoPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ServerBoundFollowRequestPayload.TYPE,
                (payload, context) -> {

            LOGGER.info("received entityId: {} from a server bound packet", payload.entityId());

            Player player = context.player();

            Entity entity = player.level().getEntity(payload.entityId());

            if(entity instanceof Villager villager){
                LOGGER.info("found requested villager entity");
                int reputation = villager.getPlayerReputation(player);
                LOGGER.info("reputation: {}", reputation);
                if(reputation >= MIN_FOLLOW_REPUTATION){
                    LOGGER.info("this player meets the requirements");
                    villager.playSound(SoundEvents.VILLAGER_CELEBRATE);
                    Brain<?> brain = villager.getBrain();
                    brain.setMemory(CustomMemoryModuleType.PLAYER_TO_FOLLOW, player);
                    brain.setActiveActivityIfPossible(CustomActivities.FOLLOW_FRIEND);
                    if(brain.isActive(CustomActivities.FOLLOW_FRIEND)){
                        LOGGER.info("succeeded in setting activity");
                    }else{
                        LOGGER.info("failed in setting activity");
                    }
                    //The reason the above function is 'IfPossible' is for two possible cases:
                    // 1. the activity is simply never registered
                    // 2. an activity has start conditions (memories that need to be present)
                }else{
                    LOGGER.info("this player does not meet the requirements");
                    entity.playSound(SoundEvents.VILLAGER_NO);
                }

            }else if(entity instanceof WanderingTrader){
                entity.playSound(SoundEvents.VILLAGER_NO);
            }

        });

        ClientPlayNetworking.registerGlobalReceiver(ClientBoundMerchantInfoPayload.TYPE, (payload, context) -> {

            LOGGER.info("received entityId: {} and containerId: {} from a client bound packet", payload.entityId(), payload.containerId());

            Player player = context.player();

            player.setAttached(AttachmentTypes.EXTRA_VILLAGER_MENU_DATA_ATTACHMENT, payload.entityId());

        });

	}
}