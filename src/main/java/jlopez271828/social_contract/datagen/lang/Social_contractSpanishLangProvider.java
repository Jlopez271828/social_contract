package jlopez271828.social_contract.datagen.lang;

import jlopez271828.social_contract.ExtraVillagerScreenWidgets;
import jlopez271828.social_contract.types.CustomItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class Social_contractSpanishLangProvider extends FabricLanguageProvider {

    public Social_contractSpanishLangProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup){
        super(dataOutput,  "es-es", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider holderLookup, TranslationBuilder translationBuilder) {


        translationBuilder.add(ExtraVillagerScreenWidgets.FOLLOW_BUTTON_START_TOOLTIP_KEY, "Pedir a un aldeano que te siga");
        translationBuilder.add(ExtraVillagerScreenWidgets.FOLLOW_BUTTON_STOP_TOOLTIP_KEY, "Haz que este aldeano deje de seguirte.");
        translationBuilder.add(ExtraVillagerScreenWidgets.GIFT_BUTTON_TOOLTIP_KEY, "Dale un regalo a este aldeano. :)");
        translationBuilder.add(CustomItemTags.VILLAGER_GIFTABLE, "villager giftable");
    }

}