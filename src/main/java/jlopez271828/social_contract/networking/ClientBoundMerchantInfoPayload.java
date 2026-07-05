package jlopez271828.social_contract.networking;

import jlopez271828.social_contract.Social_contract;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ClientBoundMerchantInfoPayload(int entityId, int containerId) implements CustomPacketPayload {

    public static final Identifier MERCHANT_INFO_PAYLOAD_ID = Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "merchant_info");
    public static final CustomPacketPayload.Type<ClientBoundMerchantInfoPayload> TYPE = new CustomPacketPayload.Type<>(MERCHANT_INFO_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientBoundMerchantInfoPayload> CODEC = StreamCodec.composite(ByteBufCodecs.INT,  ClientBoundMerchantInfoPayload::entityId, ByteBufCodecs.INT, ClientBoundMerchantInfoPayload::containerId, ClientBoundMerchantInfoPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }



}
