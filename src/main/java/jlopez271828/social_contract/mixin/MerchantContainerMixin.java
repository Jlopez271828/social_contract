package jlopez271828.social_contract.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MerchantContainer.class)
public class MerchantContainerMixin {

    @Shadow
    @Final
    @Mutable
    private NonNullList<ItemStack> itemStacks;

    @ModifyArg(method = "<init>",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/core/NonNullList;withSize(ILjava/lang/Object;)Lnet/minecraft/core/NonNullList;"),
            index = 0)
    int changeItemStacks(int size){

        return 4;
    }

}
