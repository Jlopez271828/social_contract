package jlopez271828.social_contract.datagen;

import jlopez271828.social_contract.CustomBlocks;
import jlopez271828.social_contract.CustomItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class Social_contractRecipeProvider extends FabricRecipeProvider {

    public Social_contractRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
        return new RecipeProvider(registryLookup, exporter) {
            @Override
            public void buildRecipes() {
                HolderLookup.RegistryLookup<Item> itemLookup = registries.lookupOrThrow(Registries.ITEM);

                shaped(RecipeCategory.MISC, CustomBlocks.VILLAGER_LIGHT_BLOCKITEM)
                        .pattern("iii")
                        .pattern("ivi")
                        .pattern("iii")
                        .define('i', Items.IRON_NUGGET)
                        .define('v', CustomItems.VILLAGER_TOTEM)
                        .unlockedBy(getHasName(CustomItems.VILLAGER_TOTEM), has(CustomItems.VILLAGER_TOTEM))
                        .save(output);

            }
        };
    }

    @Override
    public String getName() {
        return "Social_contractRecipeProvider";
    }


}
