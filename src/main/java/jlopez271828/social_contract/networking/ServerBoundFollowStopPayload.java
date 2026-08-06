package jlopez271828.social_contract.networking;

import jlopez271828.social_contract.Social_contract;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ServerBoundFollowStopPayload(int entityId) implements CustomPacketPayload {

    public static final Identifier FOLLOW_STOP_PAYLOAD_ID = Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "follow_stop");
    public static final CustomPacketPayload.Type<ServerBoundFollowStopPayload> TYPE = new CustomPacketPayload.Type<>(FOLLOW_STOP_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerBoundFollowStopPayload> CODEC = StreamCodec.composite(ByteBufCodecs.INT, ServerBoundFollowStopPayload::entityId, ServerBoundFollowStopPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
