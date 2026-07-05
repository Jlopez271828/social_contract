package jlopez271828.social_contract.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MerchantContainer.class)
public interface MerchantContainerAccessor {

    @Accessor("itemStacks")
    NonNullList<ItemStack> social_contract$getItemStacks();

    @Accessor("merchant")
    Merchant getMerchant();

    @Accessor("activeOffer")
    MerchantOffer getActiveOffer();

    @Accessor("futureXp")
    int getFutureXp();

    @Accessor("selectionHint")
    int getSelectionHint();



}
