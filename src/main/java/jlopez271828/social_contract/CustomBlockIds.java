package jlopez271828.social_contract;

import net.minecraft.references.BlockItemId;
import net.minecraft.resources.Identifier;

public class CustomBlockIds {

    public static BlockItemId VILLAGER_LIGHT_BLOCKITEM_ID = create("villager_light");

    private static BlockItemId create(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, name);
        return BlockItemId.create(id, id);
    }

}
