package jlopez271828.social_contract.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import jlopez271828.social_contract.Social_contract;
import jlopez271828.social_contract.old.VillagerMenu;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundMerchantOffersPacket;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
abstract class ClientPacketListenerMixin {

    private static Logger logger = LoggerFactory.getLogger(Social_contract.MOD_ID);

    @Inject(method = "handleMerchantOffers",
            at = @At("TAIL")
    )
    private void sendMerchantOffersToVillager(ClientboundMerchantOffersPacket packet, CallbackInfo ci, @Local(name = "menu") AbstractContainerMenu menu){
        logger.info("trying to send offers to menu {}", menu);
        if (packet.getContainerId() == menu.containerId && menu instanceof VillagerMenu villagerMenu) {
            logger.info("sending offers to villagerMenu");
            villagerMenu.setOffers(packet.getOffers());
            villagerMenu.setXp(packet.getVillagerXp());
            villagerMenu.setMerchantLevel(packet.getVillagerLevel());
            villagerMenu.setShowProgressBar(packet.showProgress());
            villagerMenu.setCanRestock(packet.canRestock());
        }
    }

}
