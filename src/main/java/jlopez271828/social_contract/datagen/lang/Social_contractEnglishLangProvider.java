package jlopez271828.social_contract.datagen.lang;

import jlopez271828.social_contract.CustomBlocks;
import jlopez271828.social_contract.ExtraVillagerScreenWidgets;
import jlopez271828.social_contract.Social_contract;
import jlopez271828.social_contract.types.CustomEnchantmentTags;
import jlopez271828.social_contract.types.CustomItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;

import java.util.concurrent.CompletableFuture;

public class Social_contractEnglishLangProvider extends FabricLanguageProvider {




    public Social_contractEnglishLangProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        // Specifying en_us is optional, as it's the default language code
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider holderLookup, TranslationBuilder translationBuilder) {

        translationBuilder.add("social_contract.options.useless", "Useless Button");
        translationBuilder.add(CustomBlocks.VILLAGER_LIGHT_BLOCKITEM, "Villager Light");

        //Villager GUI translations
        translationBuilder.add(ExtraVillagerScreenWidgets.FOLLOW_BUTTON_START_TOOLTIP_KEY, "Request Villager Follow You");
        translationBuilder.add(ExtraVillagerScreenWidgets.FOLLOW_BUTTON_STOP_TOOLTIP_KEY, "Make Villager Stop Following");
        translationBuilder.add(ExtraVillagerScreenWidgets.GIFT_BUTTON_TOOLTIP_KEY, "Give This Villager a Gift :)");
        translationBuilder.add(ExtraVillagerScreenWidgets.TOTAL_HAPPINESS_KEY, "Total Happiness");
        translationBuilder.add(ExtraVillagerScreenWidgets.GIFT_VALUE_KEY, "GIFT");
        translationBuilder.add(ExtraVillagerScreenWidgets.ROOM_VALUE_KEY, "ROOM");
        translationBuilder.add(ExtraVillagerScreenWidgets.TRADE_VALUE_KEY, "TRADE");
        translationBuilder.add(ExtraVillagerScreenWidgets.PAIN_VALUE_KEY, "PAIN");

        //Tag translations
        translationBuilder.add(CustomItemTags.VILLAGER_GIFTABLE, "villager giftable");
        translationBuilder.add(CustomEnchantmentTags.ENCHANTMENT_GROUP_A, "Enchantment Group A");
        translationBuilder.add(CustomEnchantmentTags.ENCHANTMENT_GROUP_B, "Enchantment Group B");
        translationBuilder.add(CustomItemTags.VILLAGER_DECORATION, "villager decoration");


        //Gamerule Translations
        translationBuilder.add(Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "max_room_volume"), "Max Villager Room Volume");
        translationBuilder.add(Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "max_villager_wonder_distance"), "Max Villager Wonder Distance");
        translationBuilder.add(Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "room_rescore_cooldown"), "Villager Room Rescore Cooldown");
        translationBuilder.add(Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "villager_death_report_radius"), "Villager Death Report Radius");
        translationBuilder.add(Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "do_villager_illager_conversions"), "Do Villager Illager Conversions");

        translationBuilder.add(
                Util.makeDescriptionId("gamerule", Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "max_room_volume")),
                "Determines how much volume the scoring algorithm will search before concluding that the room is not enclosed"
        );

        translationBuilder.add(
                Util.makeDescriptionId("gamerule", Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "max_villager_wonder_distance")),
                "The distance a Villager can wonder from its Bed, if it has one."
        );

        translationBuilder.add(
                Util.makeDescriptionId("gamerule", Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "room_rescore_cooldown")),
                "The Time interval before a room can be rescored"
        );

        translationBuilder.add(
                Util.makeDescriptionId("gamerule", Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "villager_death_report_radius")),
                "The radius in which a Villager's death will influence nearby Villagers' Happiness"
        );

        translationBuilder.add(
                Util.makeDescriptionId("gamerule", Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "do_villager_illager_conversions")),
                "Determines whether Villagers will convert to Illagers when they are low enough on Happiness"
        );
    }

}
