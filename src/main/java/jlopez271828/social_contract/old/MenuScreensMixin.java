package jlopez271828.social_contract.old;

import jlopez271828.social_contract.types.CustomMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MenuScreens.class)
abstract class MenuScreensMixin {

    @Inject(method = "<clinit>",
            at = @At("TAIL"))
    private static void addScreens(CallbackInfo ci){

        MenuScreens.register(CustomMenuTypes.VILLAGER, VillagerScreen::new);
    }


}
