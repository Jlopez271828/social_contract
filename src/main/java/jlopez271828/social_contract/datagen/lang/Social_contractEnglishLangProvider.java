package jlopez271828.social_contract.datagen.lang;

import jlopez271828.social_contract.CustomItems;
import jlopez271828.social_contract.ExtraVillagerScreenWidgets;
import jlopez271828.social_contract.types.CustomEnchantmentTags;
import jlopez271828.social_contract.types.CustomItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class Social_contractEnglishLangProvider extends FabricLanguageProvider {




    public Social_contractEnglishLangProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        // Specifying en_us is optional, as it's the default language code
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider holderLookup, TranslationBuilder translationBuilder) {

        translationBuilder.add(CustomItems.BOUQUET, "Bouquet");
        translationBuilder.add(ExtraVillagerScreenWidgets.FOLLOW_BUTTON_START_TOOLTIP_KEY, "Request Villager Follow You");
        translationBuilder.add(ExtraVillagerScreenWidgets.FOLLOW_BUTTON_STOP_TOOLTIP_KEY, "Make Villager Stop Following");
        translationBuilder.add(ExtraVillagerScreenWidgets.GIFT_BUTTON_TOOLTIP_KEY, "Give This Villager a Gift :)");
        translationBuilder.add(CustomItemTags.VILLAGER_GIFTABLE, "villager giftable");
        translationBuilder.add(CustomEnchantmentTags.ENCHANTMENT_GROUP_A, "Enchantment Group A");
        translationBuilder.add(CustomEnchantmentTags.ENCHANTMENT_GROUP_B, "Enchantment Group B");
        translationBuilder.add(CustomItemTags.VILLAGER_DECORATION, "villager decoration");
    }

}
