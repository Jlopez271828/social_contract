package jlopez271828.social_contract.datagen;

import com.mojang.datafixers.kinds.IdF;
import jlopez271828.social_contract.CustomBlocks;
import jlopez271828.social_contract.Social_contract;
import jlopez271828.social_contract.VillagerLightBlock;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.color.item.ItemTintSources;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.Optional;

public class VillagerLightBlockStateModelGenerator {

    public static TextureSlot BODY = TextureSlot.create("body");
    public static TextureSlot HEAD = TextureSlot.create("head");
    public static TextureSlot HANDLE = TextureSlot.create("handle");
    public static TextureSlot NOSE = TextureSlot.create("nose");
    public static TextureSlot STAND = TextureSlot.create("stand");

    private static final ModelTemplate STANDING_TEMPLATE =
            new ModelTemplate(
                    Optional.of(Identifier.fromNamespaceAndPath(
                            Social_contract.MOD_ID,
                            "block/template_villager_light_standing"
                    )),
                    Optional.empty(),
                    BODY
            );

    private static final ModelTemplate HANGING_TEMPLATE_A =
            new ModelTemplate(
                    Optional.of(Identifier.fromNamespaceAndPath(
                            Social_contract.MOD_ID,
                            "block/template_villager_light_hanging_a"
                    )),
                    Optional.empty(),
                    BODY
            );

    private static final ModelTemplate HANGING_TEMPLATE_B =
            new ModelTemplate(
                    Optional.of(Identifier.fromNamespaceAndPath(
                            Social_contract.MOD_ID,
                            "block/template_villager_light_hanging_b"
                    )),
                    Optional.empty(),
                    BODY
            );

    private static BlockModelDefinitionGenerator createBlockStates(VillagerLightBlock block, Identifier[] standingModelIds, Identifier[] hangingModelIdsA, Identifier[] hangingModelIdsB) {

        MultiVariant[] hangingModelsA = new MultiVariant[4];
        MultiVariant[] hangingModelsB = new MultiVariant[4];
        MultiVariant[] standingModels = new MultiVariant[4];

        for(int i = 0; i < 4; i++){
            hangingModelsA[i] = BlockModelGenerators.plainVariant(hangingModelIdsA[i]);
            hangingModelsB[i] = BlockModelGenerators.plainVariant(hangingModelIdsB[i]);

            standingModels[i] = BlockModelGenerators.plainVariant(standingModelIds[i]);
        }

        PropertyDispatch.C3<MultiVariant, Direction.Axis, Boolean, Integer> dispatch = PropertyDispatch.initial(block.AXIS, block.HANGING, block.LIGHT_POWER);

        for(int i = 0; i < 2; i++){ // axis selector

            Direction.Axis thisAxis = i == 0 ? Direction.Axis.X : Direction.Axis.Z;

            for(int j = 0; j < 2; j++){

                Boolean isHanging = j == 0;

                MultiVariant[] thisList;

                if(isHanging){

                    if(thisAxis == Direction.Axis.X){

                        thisList = hangingModelsA;

                    }else{

                        thisList = hangingModelsB;
                    }

                }else{

                    thisList = standingModels;

                }


                for(int k = 0; k < 4; k++){


                    dispatch.select(thisAxis, isHanging, k, thisAxis == Direction.Axis.X ? thisList[k].with(BlockModelGenerators.Y_ROT_90) : thisList[k].with(BlockModelGenerators.UV_LOCK));


                }

            }



        }

        return MultiVariantGenerator.dispatch(block).with(dispatch);


    }

    public static void registerVillagerLight(BlockModelGenerators generators, VillagerLightBlock block){

        Identifier[] standingModels = new Identifier[4];
        Identifier[] hangingModelsA = new Identifier[4];
        Identifier[] hangingModelsB = new Identifier[4];

        for(int i = 0; i < 4; i++){

            TextureMapping mapping = new TextureMapping()
                    .put(BODY, new Material(Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "block/villager_light_body_" + i)))
                    .put(HEAD, new Material(Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "villager_light_head")))
                    .put(HANDLE, new Material(Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "villager_light_handle")))
                    .put(STAND, new Material(Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "villager_light_stand")))
                    .put(NOSE, new Material(Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "villager_light_noise")));

            standingModels[i] = STANDING_TEMPLATE.createWithSuffix(CustomBlocks.VILLAGER_LIGHT_BLOCKITEM, "_standing_" + i, mapping, generators.modelOutput);
            hangingModelsA[i] = HANGING_TEMPLATE_A.createWithSuffix(CustomBlocks.VILLAGER_LIGHT_BLOCKITEM, "_hanging_a_" + i, mapping, generators.modelOutput);
            hangingModelsB[i] = HANGING_TEMPLATE_B.createWithSuffix(CustomBlocks.VILLAGER_LIGHT_BLOCKITEM, "_hanging_b_" + i, mapping, generators.modelOutput);
        }



        generators.blockStateOutput.accept(createBlockStates(block, standingModels, hangingModelsA, hangingModelsB));


        generators.registerSimpleItemModel(block, standingModels[3]);



    }

}
