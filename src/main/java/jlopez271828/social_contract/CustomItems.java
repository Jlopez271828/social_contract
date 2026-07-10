package jlopez271828.social_contract;

import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class CustomItems {

    public static final Item BOUQUET = register("bouquet", Item::new, new Item.Properties());

//    public static final Item FORTESTING = register("fortesting", Item::new, new Item.Properties());

    public static <T extends Item> T register(String name, Function<Item.Properties, T> itemFactory, Item.Properties settings){

        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, name));

        T item = itemFactory.apply(settings.setId(itemKey));

        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }

    public static void initialize(){
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.NATURAL_BLOCKS).register((creativeTab) -> {creativeTab.accept(CustomItems.BOUQUET);});

    }



}
