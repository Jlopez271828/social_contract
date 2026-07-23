package jlopez271828.social_contract;

import com.mojang.datafixers.optics.Wander;
import jlopez271828.social_contract.networking.ClientBoundMerchantInfoPayload;
import jlopez271828.social_contract.networking.PacketHandlers;
import jlopez271828.social_contract.networking.ServerBoundFollowRequestPayload;
import jlopez271828.social_contract.networking.ServerBoundGiveGiftPayload;
import jlopez271828.social_contract.types.*;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.stream.Stream;

public class Social_contract implements ModInitializer {
	public static final String MOD_ID = "social_contract";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final int MIN_FOLLOW_REPUTATION = 10;
    public static final int MIN_FOLLOW_HAPPINESS = 10;

    //Minimum happiness values for a villager to level up
    public static final int MIN_HAPPINESS_LEVEL_2 = 20;
    public static final int MIN_HAPPINESS_LEVEL_3 = 50;
    public static final int MIN_HAPPINESS_LEVEL_4 = 90;
    public static final int MIN_HAPPINESS_LEVEL_5 = 150;

    public static final int[] MIN_HAPPINESS_LEVELS = {0, 20, 50, 90, 150};

    //Happiness values for various events
    public static final int HAPPINESS_FOR_BED = 5;


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
        CustomItems.initialize();
        PacketHandlers.initialize();



	}


    public static boolean trySetSavedBookTrade(MerchantOffers offers, Villager villager){

        if(villager.hasAttached(AttachmentTypes.LAST_GIFTED_BOOK)){
            LOGGER.info("this villager has been given an enchanted book");
            //TODO: check book for the enchantments it has and do some processing dependant on villager happiness
            ItemStack gift = villager.getAttached(AttachmentTypes.LAST_GIFTED_BOOK);
            if(gift != null) {
                offers.add(new MerchantOffer(new ItemCost(Items.EMERALD), gift, 99, 1, 1));
                return true;
            }

        }

        LOGGER.info("this villager has not been given an enchanted book");
        return false;

    }

    public static void setRandomEnchantedBookTrade(MerchantOffers offers, Villager villager, TagKey<Enchantment> tagKey, ServerLevel level){


        LOGGER.info("trying to set random enchanted book trade");
        Optional<HolderSet.Named<Enchantment>> optionalSet = level.registryAccess().get(tagKey);

        if(optionalSet.isPresent()){

            HolderSet<Enchantment> set = optionalSet.get();

            Holder<Enchantment> holder = set.get(villager.getRandom().nextInt(0, set.size() - 1));

            LOGGER.info("enchantment found is {}", holder.value());

            ItemStack base = new ItemStack(Items.ENCHANTED_BOOK);

            //TODO: make the system for determining the level of a randomly given enchantment
            base.enchant(holder, 1);

            offers.add(new MerchantOffer(new ItemCost(Items.EMERALD), base, 99, 1, 1));

        }


    }
}