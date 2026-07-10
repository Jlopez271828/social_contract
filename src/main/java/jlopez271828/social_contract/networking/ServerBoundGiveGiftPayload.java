package jlopez271828.social_contract.networking;

import jlopez271828.social_contract.Social_contract;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ServerBoundGiveGiftPayload(int entityId, int containerId) implements CustomPacketPayload {

    public static final Identifier GIVE_GIFT_PAYLOAD_ID = Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "give_gift");

    public static final CustomPacketPayload.Type<ServerBoundGiveGiftPayload> TYPE = new CustomPacketPayload.Type<>(GIVE_GIFT_PAYLOAD_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, ServerBoundGiveGiftPayload> CODEC = StreamCodec.composite(ByteBufCodecs.INT, ServerBoundGiveGiftPayload::entityId, ByteBufCodecs.INT, ServerBoundGiveGiftPayload::containerId, ServerBoundGiveGiftPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
