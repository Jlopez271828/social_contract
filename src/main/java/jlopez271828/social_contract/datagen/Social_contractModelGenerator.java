package jlopez271828.social_contract.datagen;

import jlopez271828.social_contract.CustomItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;

public class Social_contractModelGenerator extends FabricModelProvider {

    public Social_contractModelGenerator(FabricPackOutput output){
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateFlatItem(CustomItems.BOUQUET, ModelTemplates.FLAT_ITEM);
    }

    @Override
    public String getName() {
        return "Social_contractModelProvider";
    }

}
