package jlopez271828.social_contract.criteria;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class AltruistCriterion extends SimpleCriterionTrigger<AltruistCriterion.Conditions> {

    public void trigger(ServerPlayer player) {
        trigger(player, AltruistCriterion.Conditions::requirementsMet);
    }


    @Override
    public Codec<AltruistCriterion.Conditions> codec() {
        return AltruistCriterion.Conditions.CODEC;
    }


    public record Conditions(Optional<ContextAwarePredicate> playerPredicate) implements SimpleCriterionTrigger.SimpleInstance{

        public static Codec<AltruistCriterion.Conditions> CODEC = ContextAwarePredicate.CODEC.optionalFieldOf("player")
                .xmap(AltruistCriterion.Conditions::new, AltruistCriterion.Conditions::player).codec();


        @Override
        public Optional<ContextAwarePredicate> player() {
            return this.playerPredicate;
        }


        public boolean requirementsMet() {
            return true;
        }


    }


}
