package jlopez271828.social_contract.networking;

import jlopez271828.social_contract.Social_contract;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ClientBoundFollowConfirmPayload(int entityId) implements CustomPacketPayload{

    public static final Identifier FOLLOW_CONFIRM_PAYLOAD_ID = Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "follow_confirm");
    public static final CustomPacketPayload.Type<ClientBoundFollowConfirmPayload> TYPE = new CustomPacketPayload.Type<>(FOLLOW_CONFIRM_PAYLOAD_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientBoundFollowConfirmPayload> CODEC = StreamCodec.composite(ByteBufCodecs.INT, ClientBoundFollowConfirmPayload::entityId, ClientBoundFollowConfirmPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
