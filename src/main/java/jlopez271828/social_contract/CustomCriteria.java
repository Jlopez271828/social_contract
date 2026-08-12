package jlopez271828.social_contract;

import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class CustomCriteria {

    public static final ConvertIllagerCriterion CONVERT_ILLAGER = register("convert_illager_criterion", new ConvertIllagerCriterion());

    public static final AltruistCriterion ALTRUIST_CRITERION = register("altruist_criterion", new AltruistCriterion());

    private static <T extends CriterionTrigger<?>> T register(final String name, final T criterion) {
        return Registry.register(BuiltInRegistries.TRIGGER_TYPES, Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, name), criterion);
    }

    public static void initialize(){

    }


}
