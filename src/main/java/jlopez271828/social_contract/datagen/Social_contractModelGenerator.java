package jlopez271828.social_contract.datagen;


import jlopez271828.social_contract.CustomBlocks;
import jlopez271828.social_contract.CustomItems;
import jlopez271828.social_contract.VillagerLightBlock;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureSlot;

public class Social_contractModelGenerator extends FabricModelProvider {


    public Social_contractModelGenerator(FabricPackOutput output){
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {

        VillagerLightBlockStateModelGenerator.registerVillagerLight(blockStateModelGenerator, (VillagerLightBlock) CustomBlocks.VILLAGER_LIGHT_BLOCKITEM);


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
