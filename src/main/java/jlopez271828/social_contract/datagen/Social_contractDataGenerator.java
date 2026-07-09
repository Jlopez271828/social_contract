package jlopez271828.social_contract.datagen;

import jlopez271828.social_contract.Social_contract;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class Social_contractDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(Social_contractModelGenerator::new);
        pack.addProvider(Social_contractRecipeProvider::new);
        //Languages
        pack.addProvider(Social_contractEnglishLangProvider::new);
        pack.addProvider(Social_contractRussianLangProvider::new);
        pack.addProvider(Social_contractJapaneseLangProvider::new);
        pack.addProvider(Social_contractSpanishLangProvider::new);
        pack.addProvider(Social_contractFrenchLangProvider::new);
	}
}
