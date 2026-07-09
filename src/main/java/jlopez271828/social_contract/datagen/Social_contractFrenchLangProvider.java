package jlopez271828.social_contract.datagen;

import jlopez271828.social_contract.CustomItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class Social_contractFrenchLangProvider extends FabricLanguageProvider {

    public Social_contractFrenchLangProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup){
        super(dataOutput,  "fr_fr", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider holderLookup, TranslationBuilder translationBuilder) {

        translationBuilder.add(CustomItems.BOUQUET, "Bouquet");
    }

}
