package jlopez271828.social_contract.datagen;

import jlopez271828.social_contract.AltruistCriterion;
import jlopez271828.social_contract.ConvertIllagerCriterion;
import jlopez271828.social_contract.CustomCriteria;
import jlopez271828.social_contract.Social_contract;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.criterion.TradeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class Social_contractAdvancementProvider extends FabricAdvancementProvider {


    protected Social_contractAdvancementProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
        AdvancementHolder root = Advancement.Builder.advancement()
                .display(
                        Items.BELL,
                        Component.literal("Social Contract"),
                        Component.literal("Interact With Villagers"),
                        Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "villager_face.png"),
                        AdvancementType.TASK,
                        false,
                        false,
                        false
                )
                .addCriterion("traded", TradeTrigger.TriggerInstance.tradedWithVillager())
                .save(consumer, Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "root"));

        AdvancementHolder convertIllager = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.ARROW,
                        Component.literal("Don't come to the village tomorrow"),
                        Component.literal("Push A Villager too far"),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        true
                )
                .addCriterion("converted_illager", CustomCriteria.CONVERT_ILLAGER.createCriterion(new ConvertIllagerCriterion.Conditions(Optional.empty())))
                .save(consumer, Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "convert_illager_advancement"));

        AdvancementHolder maxHappiness = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.EMERALD,
                        Component.literal("Altruist"),
                        Component.literal("Get A Villager to Max Happiness"),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .addCriterion("altruist", CustomCriteria.ALTRUIST_CRITERION.createCriterion(new AltruistCriterion.Conditions(Optional.empty())))
                .save(consumer, Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "altruist_advancement"));

        //TODO: add advancement for giving a gift to a villager




    }
}
