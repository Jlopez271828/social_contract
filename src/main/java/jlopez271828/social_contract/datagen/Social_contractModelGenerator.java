package jlopez271828.social_contract.datagen;


import jlopez271828.social_contract.*;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

public class Social_contractModelGenerator extends FabricModelProvider {

    private static final TextureSlot BODY = TextureSlot.create("body");
    private static final TextureSlot HEAD = TextureSlot.create("head");
    private static final TextureSlot STAND = TextureSlot.create("stand");
    private static final TextureSlot NOSE = TextureSlot.create("nose");
    private static final TextureSlot HANDLE = TextureSlot.create("handle");

//    private static final ModelTemplate STANDING_TEMPLATE =
//            new ModelTemplate(
//                    Optional.of(Identifier.fromNamespaceAndPath(
//                            Social_contract.MOD_ID,
//                            "block/villager_light_standing"
//                    )),
//                    Optional.empty(),
//                    BODY
//            );
//
//    private static final ModelTemplate HANGING_TEMPLATE =
//            new ModelTemplate(
//                    Optional.of(Identifier.fromNamespaceAndPath(
//                            Social_contract.MOD_ID,
//                            "block/villager_light_hanging"
//                    )),
//                    Optional.empty(),
//                    BODY
//            );

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


//    private Identifier createPowerModel(
//            BlockModelGenerators generator,
//            boolean hanging,
//            int power
//    ) {
//        ModelTemplate template = hanging ? HANGING_TEMPLATE : STANDING_TEMPLATE;
//
//        Identifier bodyTexture = Identifier.fromNamespaceAndPath(
//                Social_contract.MOD_ID,
//                "block/villager_light_" + power
//        );
//
//        TextureMapping textures = new TextureMapping()
//                .put(
//                        BODY,
//                        new Material(bodyTexture)
//                );
//
//        return template.createWithSuffix(
//                CustomBlocks.VILLAGER_LIGHT_BLOCKITEM,
//                hanging ? "_hanging_" + power : "_standing_" + power,
//                textures,
//                generator.modelOutput
//        );
//    }

//    private static MultiVariant model(Identifier id) {
//        return BlockModelGenerators.plainVariant(id);
//    }
//
//    private static Identifier powerModel(
//            BlockModelGenerators generator,
//            boolean hanging,
//            int power
//    ) {
//        // create child model using appropriate parent
//    }



}
