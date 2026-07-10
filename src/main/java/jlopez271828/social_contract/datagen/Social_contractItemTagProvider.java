package jlopez271828.social_contract.datagen;

import jlopez271828.social_contract.CustomItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

import static jlopez271828.social_contract.types.CustomItemTags.VILLAGER_GIFTABLE;

public class Social_contractItemTagProvider extends FabricTagsProvider.ItemTagsProvider {



    public Social_contractItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {

        //Maybe there should be multiple tags, one for general villager giftable, and more for each profession
        valueLookupBuilder(VILLAGER_GIFTABLE)
                .addOptionalTag(ItemTags.VILLAGER_PICKS_UP)
                .addOptionalTag(ItemTags.BOOKSHELF_BOOKS)
                .addOptionalTag(ItemTags.FLOWERS)
                .add(CustomItems.BOUQUET)
                .add(Items.APPLE)
                .add(Items.HONEY_BOTTLE)
                .add(Items.HONEYCOMB)
                .add(Items.COOKIE)
                .add(Items.CAKE)
                .add(Items.SUGAR) //there really should be a tag for all these cooked foods ...
                .add(Items.COOKED_BEEF)
                .add(Items.COOKED_CHICKEN)
                .add(Items.COOKED_COD)
                .add(Items.COOKED_MUTTON)
                .add(Items.COOKED_RABBIT)
                .add(Items.COOKED_PORKCHOP)
                .add(Items.COOKED_SALMON);
    }
}
