package jlopez271828.social_contract.types;

import jlopez271828.social_contract.Social_contract;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

public class CustomEnchantmentTags {

    public static TagKey<Enchantment> ENCHANTMENT_GROUP_A = create("enchantment_group_a");
    public static TagKey<Enchantment> ENCHANTMENT_GROUP_B = create("enchantment_group_b");

    private static TagKey<Enchantment> create(String name){
        return TagKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, name));
    }

}
