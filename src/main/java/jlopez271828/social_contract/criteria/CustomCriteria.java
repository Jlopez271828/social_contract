package jlopez271828.social_contract.criteria;

import jlopez271828.social_contract.Social_contract;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class CustomCriteria {

    public static final ConvertIllagerCriterion CONVERT_ILLAGER = register("convert_illager_criterion", new ConvertIllagerCriterion());

    public static final AltruistCriterion ALTRUIST_CRITERION = register("altruist_criterion", new AltruistCriterion());

    public static final GiveGiftCriterion GIVE_GIFT_CRITERION = register("give_gift_criterion", new GiveGiftCriterion());

    public static final SuccessfullRequestCriterion SUCCESSFULL_REQUEST_CRITERION = register("successfull_request_criterion", new SuccessfullRequestCriterion());

    public static final NicePlaceCriterion NICE_PLACE_CRITERION = register("nice_place_criterion", new NicePlaceCriterion());

    private static <T extends CriterionTrigger<?>> T register(final String name, final T criterion) {
        return Registry.register(BuiltInRegistries.TRIGGER_TYPES, Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, name), criterion);
    }

    public static void initialize(){

    }


}
