package jlopez271828.social_contract.datagen.lang;

import jlopez271828.social_contract.CustomItems;
import jlopez271828.social_contract.ExtraVillagerScreenWidgets;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class Social_contractRussianLangProvider extends FabricLanguageProvider {

    public Social_contractRussianLangProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup){
        super(dataOutput,  "ru_ru", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider holderLookup, TranslationBuilder translationBuilder) {

        // note: Mojang calls Villagers "крестья́не", so I will too.
        translationBuilder.add(CustomItems.BOUQUET, "Букет");
        translationBuilder.add(ExtraVillagerScreenWidgets.FOLLOW_BUTTON_START_TOOLTIP_KEY, "Запросить у крестья́нина следовать за вами");
        translationBuilder.add(ExtraVillagerScreenWidgets.FOLLOW_BUTTON_STOP_TOOLTIP_KEY, "перестать следить за вами.");
        translationBuilder.add(ExtraVillagerScreenWidgets.GIFT_BUTTON_TOOLTIP_KEY, "Подари подарок этому крестьянину");
    }

}
