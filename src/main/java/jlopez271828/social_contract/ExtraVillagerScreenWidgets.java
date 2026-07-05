package jlopez271828.social_contract;

import jlopez271828.social_contract.mixin.ScreenAccessor;
import jlopez271828.social_contract.networking.ServerBoundFollowRequestPayload;
import jlopez271828.social_contract.types.AttachmentTypes;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtraVillagerScreenWidgets {

    private static final Identifier SINGLE_SLOT = Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "single_slot.png");
    private static final Logger logger = LoggerFactory.getLogger(Social_contract.MOD_ID);

    public static void initialize() {

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof MerchantScreen) {

                ScreenEvents.afterBackground(screen).register((afterScreen, drawContext, mouseX, mouseY, tickDelta) -> {

                    int xo = (screen.width - 276) / 2;
                    int yo = (screen.height - 166) / 2;



//                    drawContext.text(screen.getFont(), "hello", screen.width - 40, 166 - 94, -12566464, false);
                    drawContext.blit(RenderPipelines.GUI_TEXTURED, SINGLE_SLOT, xo + 219, yo + 63, 0.0f, 0.0f, 18, 18, 64, 64);
                    ((ScreenAccessor) screen).social_contract$addRenderableWidget(new VillagerFollowButton(xo + 240, yo + 63, button -> {
                        logger.info("the button has been pushed");
                        if(client.player.hasAttached(AttachmentTypes.EXTRA_VILLAGER_MENU_DATA_ATTACHMENT)){
                            int entityId = client.player.getAttached(AttachmentTypes.EXTRA_VILLAGER_MENU_DATA_ATTACHMENT);
                            ClientPlayNetworking.send(new ServerBoundFollowRequestPayload(entityId));
                        }else {
                            ClientPlayNetworking.send(new ServerBoundFollowRequestPayload(-1));
                        }
                    }));

                });

            }
        });

    }


}
