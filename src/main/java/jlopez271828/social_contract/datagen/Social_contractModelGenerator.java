package jlopez271828.social_contract.datagen;

import com.google.common.base.Optional;
import jlopez271828.social_contract.CustomItems;
import jlopez271828.social_contract.Social_contract;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

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
