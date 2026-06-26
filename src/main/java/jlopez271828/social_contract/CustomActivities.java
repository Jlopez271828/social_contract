package jlopez271828.social_contract;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.schedule.Activity;

public class CustomActivities {

    public static final Activity FOLLOW_FRIEND = register("follow_friend");

    private static Activity register(final String name) {
        return Registry.register(BuiltInRegistries.ACTIVITY, name, new Activity(name));
    }
}
