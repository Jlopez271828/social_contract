package jlopez271828.social_contract;

import jlopez271828.social_contract.mixin.ScreenAccessor;
import jlopez271828.social_contract.networking.ClientBoundVillagerInfoPayload;
import jlopez271828.social_contract.networking.ServerBoundFollowRequestPayload;
import jlopez271828.social_contract.networking.ServerBoundFollowStopPayload;
import jlopez271828.social_contract.networking.ServerBoundGiveGiftPayload;
import jlopez271828.social_contract.types.AttachmentTypes;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExtraVillagerScreenWidgets {

    private static final Identifier SINGLE_SLOT = Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "single_slot.png");
    private static final Identifier STAGE_1 = Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "villager_stage_1.png");
    private static final Identifier STAGE_2 = Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "villager_stage_2.png");
    private static final Identifier STAGE_3 = Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "villager_stage_3.png");
    private static final Identifier STAGE_4 = Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "villager_stage_4.png");
    private static final Identifier STAGE_5 = Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "villager_stage_5.png");
    private static final Identifier STAGE_6 = Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "villager_stage_6.png");
    private static final Identifier STAGE_7 = Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "villager_stage_7.png");
    private static final Identifier STAGE_BAD_1 = Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "villager_stage_bad_1.png");
    private static final Identifier STAGE_BAD_2 = Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "villager_stage_bad_2.png");
    private static final Identifier STAGE_BAD_3 = Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "villager_stage_bad_3.png");
    private static final Identifier STAGE_BAD_4 = Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "villager_stage_bad_4.png");

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

    private static final float RATIO = 102.0f / SocialContractConfig.MAX_USED_HAPPINESS;

    public static final String BASE_KEY = "text.social_contract.";

    public static final String GIFT_BUTTON_TOOLTIP_KEY = BASE_KEY + "gift_button_tooltip";
    public static final String FOLLOW_BUTTON_STOP_TOOLTIP_KEY = BASE_KEY + "follow_button_stop_tooltip";
    public static final String FOLLOW_BUTTON_START_TOOLTIP_KEY = BASE_KEY + "follow_button_start_tooltip";
    public static final String PAIN_VALUE_KEY = BASE_KEY + "pain_value";
    public static final String ROOM_VALUE_KEY = BASE_KEY + "room_value";
    public static final String GIFT_VALUE_KEY = BASE_KEY + "gift_value";
    public static final String TRADE_VALUE_KEY = BASE_KEY + "trade_value";
    public static final String TOTAL_HAPPINESS_KEY = BASE_KEY + "total_happiness";


    public static final int HAPPINESS_STAGE_2 = SocialContractConfig.MIN_HAPPINESS_LEVEL_2;
    public static final int HAPPINESS_STAGE_3 = SocialContractConfig.MIN_HAPPINESS_LEVEL_3;
    public static final int HAPPINESS_STAGE_4 = SocialContractConfig.MIN_HAPPINESS_LEVEL_4;
    public static final int HAPPINESS_STAGE_5 = SocialContractConfig.MIN_HAPPINESS_LEVEL_5;
    public static final int HAPPINESS_STAGE_6 = SocialContractConfig.MIN_HAPPINESS_DISCOUNT;
    public static final int HAPPINESS_STAGE_7 = SocialContractConfig.MIN_HAPPINESS_REQUEST;

    public static final int HAPPINESS_BAD_STAGE_1 = -20;
    public static final int HAPPINESS_BAD_STAGE_2 = -100;
    public static final int HAPPINESS_BAD_STAGE_3 = -220;
    public static final int HAPPINESS_BAD_STAGE_4 = -420;



    public static void initialize() {

        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof MerchantScreen) {

                if(client.player != null && client.player.hasAttached(AttachmentTypes.EXTRA_VILLAGER_MENU_DATA_ATTACHMENT)){

                    ClientBoundVillagerInfoPayload payload = client.player.getAttached(AttachmentTypes.EXTRA_VILLAGER_MENU_DATA_ATTACHMENT);

                    if(payload == null){
                        logger.warn("null payload");
                        return;
                    }

                    int xo = (screen.width - 276) / 2;
                    int yo = (screen.height - 166) / 2;
                    int happiness = payload.happiness();
                    int roomHappiness = payload.roomHappiness();
                    int giftHappiness = payload.giftHappiness();
                    int tradeHappiness = payload.tradeHappiness();
                    int pain = payload.pain();

                    int leftBoundX = xo + 245;
                    int rightBoundX = leftBoundX + 24;

                    int topBoundY = yo + 30;
                    int bottomBoundY = topBoundY + 30;


                    List<FormattedCharSequence> strings = new ArrayList<>();
                    strings.add(FormattedCharSequence.forward("Total Happiness: " + happiness, Style.EMPTY));
                    strings.add(FormattedCharSequence.forward("GIFT: " + giftHappiness, Style.EMPTY));
                    strings.add(FormattedCharSequence.forward("TRADE: " + tradeHappiness, Style.EMPTY));
                    strings.add(FormattedCharSequence.forward("ROOM: " + roomHappiness, Style.EMPTY));
                    strings.add(FormattedCharSequence.forward("PAIN: " + pain, Style.EMPTY.withColor(TextColor.RED)));

                    List<Component> texts = new ArrayList<>();
                    texts.add(Component.translatable(TOTAL_HAPPINESS_KEY).append(": " + happiness));
                    texts.add(Component.translatable(GIFT_VALUE_KEY).append(": " + giftHappiness));
                    texts.add(Component.translatable(ROOM_VALUE_KEY).append(": " + roomHappiness));
                    texts.add(Component.translatable(TRADE_VALUE_KEY).append(": " + tradeHappiness));
                    texts.add(Component.translatable(PAIN_VALUE_KEY).append(": " + pain).withColor(TextColor.RED));



                    if(client.player.hasAttached(AttachmentTypes.HAS_VILLAGER_FOLLOWING)){
                        ((ScreenAccessor) screen).social_contract$addRenderableWidget(//this is the stop button
                                Button.builder(
                                                Component.empty(),
                                                button -> {


                                                    if(client.player != null && client.player.hasAttached(AttachmentTypes.HAS_VILLAGER_FOLLOWING)) {
                                                        Integer entityId = client.player.getAttached(AttachmentTypes.HAS_VILLAGER_FOLLOWING);

                                                        if (entityId != null) {
                                                            ClientPlayNetworking.send(new ServerBoundFollowStopPayload(entityId));

                                                        }

                                                    }



                                                })
                                        .pos(xo + FOLLOW_BUTTON_X, yo + FOLLOW_BUTTON_Y)
                                        .size(FOLLOW_BUTTON_WIDTH, FOLLOW_BUTTON_HEIGHT)
                                        .tooltip(Tooltip.create(Component.translatable(FOLLOW_BUTTON_STOP_TOOLTIP_KEY)))
                                        .build()
                        );

                    }else {
                        ((ScreenAccessor) screen).social_contract$addRenderableWidget(//this is the start following button
                                Button
                                        .builder(Component.empty(),
                                                button -> {
                                                    if(client.player == null){
                                                        return;
                                                    }

                                                    int entityId = payload.entityId();
                                                    ClientPlayNetworking.send(new ServerBoundFollowRequestPayload(entityId));

                                                }
                                        )
                                        .pos(xo + FOLLOW_BUTTON_X, yo + FOLLOW_BUTTON_Y)
                                        .size(FOLLOW_BUTTON_WIDTH, FOLLOW_BUTTON_HEIGHT)
                                        .tooltip(Tooltip.create(Component.translatable(FOLLOW_BUTTON_START_TOOLTIP_KEY)))
                                        .build()
                        );
                    }

                    ((ScreenAccessor) screen).social_contract$addRenderableWidget(
                            Button
                                    .builder(
                                            Component.empty(),
                                            button -> {
                                                if(client.player == null){
                                                    return;
                                                }

                                                int entityId = payload.entityId();
                                                ClientPlayNetworking.send(new ServerBoundGiveGiftPayload(entityId, ((MerchantScreen) screen).getMenu().containerId));

                                            })
                                    .pos(xo + GIVE_BUTTON_X, yo + GIVE_BUTTON_Y)
                                    .size(GIVE_BUTTON_WIDTH, GIVE_BUTTON_HEIGHT)
                                    .tooltip(Tooltip.create(Component.translatable(GIFT_BUTTON_TOOLTIP_KEY)))
                                    .build()
                    );

                    ScreenEvents.afterBackground(screen).register((afterScreen, drawContext, mouseX, mouseY, tickDelta) -> {

                        Identifier texture = STAGE_1;

                        if(client.player == null){
                            return;
                        }

                        if(happiness >= 0){//good happiness

                            if(happiness > HAPPINESS_STAGE_7){
                                texture = STAGE_7;
                            }else if(happiness > HAPPINESS_STAGE_6){
                                texture = STAGE_6;
                            }else if(happiness > HAPPINESS_STAGE_5){
                                texture = STAGE_5;
                            }else if(happiness > HAPPINESS_STAGE_4){
                                texture = STAGE_4;
                            }else if(happiness > HAPPINESS_STAGE_3){
                                texture = STAGE_3;
                            }else if(happiness > HAPPINESS_STAGE_2){
                                texture = STAGE_2;
                            }

                        }else{//bad happiness

                            if(happiness < HAPPINESS_BAD_STAGE_4){
                                texture = STAGE_BAD_4;
                            }else if(happiness < HAPPINESS_BAD_STAGE_3){
                                texture = STAGE_BAD_3;
                            }else if(happiness < HAPPINESS_BAD_STAGE_2){
                                texture = STAGE_BAD_2;
                            }else{
                                texture = STAGE_BAD_1;
                            }


                        }


                        if(mouseX > leftBoundX && mouseX < rightBoundX && mouseY > topBoundY && mouseY < bottomBoundY){
//                            drawContext.tooltip(screen.getFont(), components, xo, yo, );
                            drawContext.setTooltipForNextFrame(screen.getFont(), texts, Optional.empty(), mouseX, mouseY);
                        }




                        drawContext.blit(RenderPipelines.GUI_TEXTURED, SINGLE_SLOT, xo + GIFT_SLOT_X, yo + GIFT_SLOT_Y, 0.0f, 0.0f, 18, 18, 64, 64);

                        drawContext.blit(RenderPipelines.GUI_TEXTURED, texture, xo + 245, yo + 30, 0.0f, 0.0f, 24, 30, 28, 52);

                    });
                }


            }
        });

    }


}
