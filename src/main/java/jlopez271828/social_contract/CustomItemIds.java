package jlopez271828.social_contract;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class CustomItemIds {

    public static ResourceKey<Item> VILLAGER_TOTEM_KEY = create("villager_totem");


    public static ResourceKey<Item> create(String name) {
        // Create the item key.
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, name));
    }


}
