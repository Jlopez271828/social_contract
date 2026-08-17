package jlopez271828.social_contract;

import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class CustomBlockStateProperties {

    public static final IntegerProperty LIGHT_POWER = IntegerProperty.create("light_power", 0, 3);

    public static void initialize(){

    }
}
