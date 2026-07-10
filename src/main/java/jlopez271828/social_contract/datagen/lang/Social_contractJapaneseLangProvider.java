package jlopez271828.social_contract.datagen.lang;

import jlopez271828.social_contract.CustomItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class Social_contractJapaneseLangProvider extends FabricLanguageProvider {

    public Social_contractJapaneseLangProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup){
        super(dataOutput,  "ja_jp", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider holderLookup, TranslationBuilder translationBuilder) {

        translationBuilder.add(CustomItems.BOUQUET, "花束");
    }

}
