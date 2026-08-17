package jlopez271828.social_contract.datagen;

import jlopez271828.social_contract.criteria.*;
import jlopez271828.social_contract.Social_contract;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.*;
import net.minecraft.advancements.triggers.TradeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.references.BlockItemIds;
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
                        Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "villager_face"),
                        AdvancementType.TASK,
                        false,
                        false,
                        false
                )
                .addCriterion("traded", TradeTrigger.TriggerInstance.tradedWithVillager())
                .addCriterion("gave_gift", CustomCriteria.GIVE_GIFT_CRITERION.createCriterion(new GiveGiftCriterion.Conditions(Optional.empty())))
                .requirements(AdvancementRequirements.Strategy.OR)
                .save(consumer, Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "root"));

        AdvancementHolder giveGiftAdvancement = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.POPPY,
                        Component.literal("The Whole Thing's Wrapped With A Bow"),
                        Component.literal("Give a Villager a Gift"),
                        null,
                        AdvancementType.TASK,
                        true,
                        true,
                        false
                )
                .addCriterion("gave_gift", CustomCriteria.GIVE_GIFT_CRITERION.createCriterion(new GiveGiftCriterion.Conditions(Optional.empty())))
                .save(consumer, Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "give_gift_advancement"));

        AdvancementHolder successfullRequestAdvancement = Advancement.Builder.advancement()
                .parent(giveGiftAdvancement)
                .display(
                        Items.WRITABLE_BOOK,
                        Component.literal("I'll Consider It"),
                        Component.literal("Successfully make a Librarian trade a requested Enchantment"),
                        null,
                        AdvancementType.GOAL,
                        true,
                        true,
                        false
                )
                .addCriterion("successfully_requested", CustomCriteria.SUCCESSFULL_REQUEST_CRITERION.createCriterion(new SuccessfullRequestCriterion.Conditions(Optional.empty())))
                .save(consumer, Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "successfull_request_advancement"));

        AdvancementHolder convertIllager = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.ARROW,
                        Component.literal("Don't Come To The Village Tomorrow"),
                        Component.literal("Push a Villager too far"),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        true
                )
                .addCriterion("converted_illager", CustomCriteria.CONVERT_ILLAGER.createCriterion(new ConvertIllagerCriterion.Conditions(Optional.empty())))
                .save(consumer, Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "convert_illager_advancement"));

        AdvancementHolder maxHappiness = Advancement.Builder.advancement()
                .parent(giveGiftAdvancement)
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

        AdvancementHolder nicePlace = Advancement.Builder.advancement()
                .parent(root)
                .display(
                        Items.BED.red(),
                        Component.literal("Nice Place"),
                        Component.literal("Trade With a Villager with a room score of 500 or above"),
                        null,
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        false
                )
                .addCriterion("nice_place", CustomCriteria.NICE_PLACE_CRITERION.createCriterion(new NicePlaceCriterion.Conditions(Optional.empty())))
                .save(consumer, Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "nice_place_advancement"));

        //TODO: add advancement for giving a gift to a villager




    }
}
