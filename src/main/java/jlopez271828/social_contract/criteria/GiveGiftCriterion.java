package jlopez271828.social_contract.criteria;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.predicates.ContextAwarePredicate;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public class GiveGiftCriterion extends SimpleCriterionTrigger<GiveGiftCriterion.Conditions> {

    public void trigger(ServerPlayer player) {
        trigger(player, GiveGiftCriterion.Conditions::requirementsMet);
    }


    @Override
    public Codec<Conditions> codec() {
        return GiveGiftCriterion.Conditions.CODEC;
    }


    public record Conditions(Optional<ContextAwarePredicate> playerPredicate) implements SimpleCriterionTrigger.SimpleInstance{

        public static Codec<GiveGiftCriterion.Conditions> CODEC = ContextAwarePredicate.CODEC.optionalFieldOf("player")
                .xmap(GiveGiftCriterion.Conditions::new, GiveGiftCriterion.Conditions::player).codec();


        @Override
        public Optional<ContextAwarePredicate> player() {
            return this.playerPredicate;
        }


        public boolean requirementsMet() {
            return true;
        }


    }


}
