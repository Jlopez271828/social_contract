package jlopez271828.social_contract.datagen;

import jlopez271828.social_contract.types.CustomItemTags;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.references.BlockItemIds;
import net.minecraft.references.ItemIds;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.ItemTags;

import java.util.concurrent.CompletableFuture;

import static jlopez271828.social_contract.types.CustomItemTags.VILLAGER_GIFTABLE;

public class Social_contractItemTagProvider extends FabricTagsProvider.ItemTagsProvider {



    public Social_contractItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {

        //Maybe there should be multiple tags, one for general villager giftable, and more for each profession
        builder(VILLAGER_GIFTABLE)
                .addOptionalTag(CustomItemTags.VILLAGER_DECORATION)
                .addOptionalTag(ItemTags.VILLAGER_PICKS_UP)
                .addOptionalTag(ItemTags.BOOKSHELF_BOOKS)
                .add(ItemIds.WRITTEN_BOOK)
                .addOptionalTag(BlockItemTags.FLOWERS.item())
                .add(ItemIds.APPLE)
                .add(ItemIds.HONEY_BOTTLE)
                .add(ItemIds.HONEYCOMB)
                .add(ItemIds.COOKIE)
                .add(BlockItemIds.CAKE)
                .add(ItemIds.SUGAR) //there really should be a tag for all these cooked foods ...
                .add(ItemIds.COOKED_BEEF)
                .add(ItemIds.COOKED_CHICKEN)
                .add(ItemIds.COOKED_COD)
                .add(ItemIds.COOKED_MUTTON)
                .add(ItemIds.COOKED_RABBIT)
                .add(ItemIds.COOKED_PORKCHOP)
                .add(ItemIds.COOKED_SALMON);

        builder(CustomItemTags.VILLAGER_DECORATION)
                .add(BlockItemIds.DECORATED_POT)
                .add(BlockItemIds.JUKEBOX)
                .add(ItemIds.PAINTING)
                .add(BlockItemIds.FLOWER_POT);
    }
}
