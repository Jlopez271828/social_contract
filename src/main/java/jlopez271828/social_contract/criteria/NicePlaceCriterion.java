package jlopez271828.social_contract.criteria;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.criterion.ContextAwarePredicate;
import net.minecraft.advancements.criterion.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class NicePlaceCriterion extends SimpleCriterionTrigger<NicePlaceCriterion.Conditions> {

    public void trigger(ServerPlayer player) {
        trigger(player, NicePlaceCriterion.Conditions::requirementsMet);
    }


    @Override
    public Codec<Conditions> codec() {
        return NicePlaceCriterion.Conditions.CODEC;
    }


    public record Conditions(Optional<ContextAwarePredicate> playerPredicate) implements SimpleCriterionTrigger.SimpleInstance{

        public static Codec<NicePlaceCriterion.Conditions> CODEC = ContextAwarePredicate.CODEC.optionalFieldOf("player")
                .xmap(NicePlaceCriterion.Conditions::new, NicePlaceCriterion.Conditions::player).codec();


        @Override
        public Optional<ContextAwarePredicate> player() {
            return this.playerPredicate;
        }


        public boolean requirementsMet() {
            return true;
        }


    }


}