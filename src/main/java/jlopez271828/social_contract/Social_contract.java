package jlopez271828.social_contract;

import jlopez271828.social_contract.networking.ClientBoundMerchantInfoPayload;
import jlopez271828.social_contract.networking.ServerBoundFollowRequestPayload;
import jlopez271828.social_contract.types.AttachmentTypes;
import jlopez271828.social_contract.types.CustomMemoryModuleType;
import jlopez271828.social_contract.types.CustomMenuTypes;
import jlopez271828.social_contract.types.CustomSensorTypes;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Social_contract implements ModInitializer {
	public static final String MOD_ID = "social_contract";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

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

        PayloadTypeRegistry.serverboundPlay().register(ServerBoundFollowRequestPayload.TYPE, ServerBoundFollowRequestPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ClientBoundMerchantInfoPayload.TYPE, ClientBoundMerchantInfoPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(ServerBoundFollowRequestPayload.TYPE, (payload, context) -> {

            LOGGER.info("recieved entityId: {} from a server bound packet", payload.entityId());

            Entity entity = context.player().level().getEntity(payload.entityId());

            if(entity instanceof Villager){
                LOGGER.info("found requested villager entity");
            }

        });

        ClientPlayNetworking.registerGlobalReceiver(ClientBoundMerchantInfoPayload.TYPE, (payload, context) -> {

            LOGGER.info("recieved entityId: {} and containerId: {} from a client bound packet", payload.entityId(), payload.containerId());

            Player player = context.player();

            player.setAttached(AttachmentTypes.EXTRA_VILLAGER_MENU_DATA_ATTACHMENT, payload.entityId());

        });

	}
}