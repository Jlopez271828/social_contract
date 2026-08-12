package jlopez271828.social_contract.networking;

import jlopez271828.social_contract.Social_contract;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ClientBoundVillagerInfoPayload(int entityId, int containerId, int happiness, int roomHappiness, int giftHappiness, int tradeHappiness, int pain) implements CustomPacketPayload {

    public static final Identifier MERCHANT_INFO_PAYLOAD_ID = Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "merchant_info");
    public static final CustomPacketPayload.Type<ClientBoundVillagerInfoPayload> TYPE = new CustomPacketPayload.Type<>(MERCHANT_INFO_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientBoundVillagerInfoPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClientBoundVillagerInfoPayload::entityId,
            ByteBufCodecs.INT, ClientBoundVillagerInfoPayload::containerId,
            ByteBufCodecs.INT, ClientBoundVillagerInfoPayload::happiness,
            ByteBufCodecs.INT, ClientBoundVillagerInfoPayload::roomHappiness,
            ByteBufCodecs.INT, ClientBoundVillagerInfoPayload::giftHappiness,
            ByteBufCodecs.INT, ClientBoundVillagerInfoPayload::tradeHappiness,
            ByteBufCodecs.INT, ClientBoundVillagerInfoPayload::pain,
            ClientBoundVillagerInfoPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }



}
