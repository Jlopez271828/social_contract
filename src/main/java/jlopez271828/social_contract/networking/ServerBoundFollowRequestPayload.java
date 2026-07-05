package jlopez271828.social_contract.networking;

import jlopez271828.social_contract.Social_contract;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.UUID;

public record ServerBoundFollowRequestPayload(int entityId) implements CustomPacketPayload {

    public static final Identifier FOLLOW_REQUEST_PAYLOAD_ID = Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "follow_request");
    public static final CustomPacketPayload.Type<ServerBoundFollowRequestPayload> TYPE = new CustomPacketPayload.Type<>(FOLLOW_REQUEST_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerBoundFollowRequestPayload> CODEC = StreamCodec.composite(ByteBufCodecs.INT, ServerBoundFollowRequestPayload::entityId, ServerBoundFollowRequestPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
