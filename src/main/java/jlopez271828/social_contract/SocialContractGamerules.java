package jlopez271828.social_contract;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;

public class SocialContractGamerules {

    public static final GameRule<Integer> MAX_ROOM_VOLUME_GAMERULE = create(SocialContractConfig.MAX_ROOM_SIZE, "max_room_volume");

    public static final GameRule<Integer> MAX_VILLAGER_WONDER_DISTANCE = create(SocialContractConfig.MAX_HOME_WANDER_DISTANCE, "max_villager_wonder_distance");

    public static final GameRule<Integer> ROOM_RESCORE_COOLDOWN = create(SocialContractConfig.ROOM_SCORE_COOLDOWN, "room_rescore_cooldown");

    public static final GameRule<Integer> VILLAGER_DEATH_REPORT_RADIUS = create(SocialContractConfig.DEATH_REPORT_RADIUS, "villager_death_report_radius");

    public static final GameRule<Boolean> VILLAGER_ILLAGER_CONVERSION = create(true, "do_villager_illager_conversions");

    private static GameRule<Integer> create(Integer value, String name){
        return GameRuleBuilder
                .forInteger(value)
                .category(GameRuleCategory.MISC)
                .buildAndRegister(Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, name));
    }

    private static GameRule<Boolean> create(Boolean value, String name){
        return GameRuleBuilder
                .forBoolean(value)
                .category(GameRuleCategory.MISC)
                .buildAndRegister(Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, name));
    }

    public static void initialize(){

    }


}
