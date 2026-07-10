package jlopez271828.social_contract.types;

import jlopez271828.social_contract.Social_contract;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class CustomItemTags {

    public static TagKey<Item> VILLAGER_GIFTABLE = create("villager_giftable");

    private static TagKey<Item> create(String name){
        return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, name));
    }
}
