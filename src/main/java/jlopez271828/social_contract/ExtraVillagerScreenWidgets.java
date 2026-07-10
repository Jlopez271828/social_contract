package jlopez271828.social_contract;

import jlopez271828.social_contract.mixin.ScreenAccessor;
import jlopez271828.social_contract.networking.ServerBoundFollowRequestPayload;
import jlopez271828.social_contract.networking.ServerBoundGiveGiftPayload;
import jlopez271828.social_contract.types.AttachmentTypes;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.system.ffm.mapping.Mapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtraVillagerScreenWidgets {

    private static final Identifier SINGLE_SLOT = Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "single_slot.png");
    private static final Logger logger = LoggerFactory.getLogger(Social_contract.MOD_ID);

    private static final int FOLLOW_BUTTON_X = 179;
    private static final int FOLLOW_BUTTON_Y = 63;
    private static final int FOLLOW_BUTTON_WIDTH = 18;
    private static final int FOLLOW_BUTTON_HEIGHT = 18;
    private static final int GIFT_SLOT_X = 219;
    private static final int GIFT_SLOT_Y = 63;
    private static final int GIVE_BUTTON_X = 240;
    private static final int GIVE_BUTTON_Y = 63;
    private static final int GIVE_BUTTON_WIDTH = 18;
    private static final int GIVE_BUTTON_HEIGHT = 18;

    public static void initialize() {

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof MerchantScreen) {

                if(client.player != null && client.player.hasAttached(AttachmentTypes.EXTRA_VILLAGER_MENU_DATA_ATTACHMENT)){
                    ScreenEvents.afterBackground(screen).register((afterScreen, drawContext, mouseX, mouseY, tickDelta) -> {

                        int xo = (screen.width - 276) / 2;
                        int yo = (screen.height - 166) / 2;


                        drawContext.blit(RenderPipelines.GUI_TEXTURED, SINGLE_SLOT, xo + GIFT_SLOT_X, yo + GIFT_SLOT_Y, 0.0f, 0.0f, 18, 18, 64, 64);
//                        ((ScreenAccessor) screen).social_contract$addRenderableWidget(new VillagerFollowButton(xo + FOLLOW_BUTTON_X, yo + FOLLOW_BUTTON_Y, button -> {
//                            logger.info("the follow button has been pushed");
//
//                            Integer entityId = client.player.getAttached(AttachmentTypes.EXTRA_VILLAGER_MENU_DATA_ATTACHMENT);
//                            if(entityId != null) {
//                                ClientPlayNetworking.send(new ServerBoundFollowRequestPayload(entityId));
//                            }
//                        }));

                        ((ScreenAccessor) screen).social_contract$addRenderableWidget(
                                Button
                                        .builder(Component.empty(),
                                                button -> {
                                                    logger.info("the follow button has been pushed");

                                                    Integer entityId = client.player.getAttached(AttachmentTypes.EXTRA_VILLAGER_MENU_DATA_ATTACHMENT);
                                                    if(entityId != null) {
                                                        ClientPlayNetworking.send(new ServerBoundFollowRequestPayload(entityId));
                                                    }
                                                }
                                        )
                                        .pos(xo + FOLLOW_BUTTON_X, yo + FOLLOW_BUTTON_Y)
                                        .size(FOLLOW_BUTTON_WIDTH, FOLLOW_BUTTON_HEIGHT)
                                        .tooltip(Tooltip.create(Component.literal("Follow Button")))
                                        .build()
                        );

                        ((ScreenAccessor) screen).social_contract$addRenderableWidget(
                                Button
                                        .builder(
                                                Component.empty(),
                                                button -> {
                                                    logger.info("the give button was pushed");
                                                    Integer entityId = client.player.getAttached(AttachmentTypes.EXTRA_VILLAGER_MENU_DATA_ATTACHMENT);
                                                    if(entityId != null){

                                                        ClientPlayNetworking.send(new ServerBoundGiveGiftPayload(entityId, ((MerchantScreen) screen).getMenu().containerId));
                                                    }
                                                })
                                        .pos(xo + GIVE_BUTTON_X, yo + GIVE_BUTTON_Y)
                                        .size(GIVE_BUTTON_WIDTH, GIVE_BUTTON_HEIGHT)
                                        .tooltip(Tooltip.create(Component.literal("give button")))
                                        .build()
                        );



                    });
                }


            }
        });

    }


}
