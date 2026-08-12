package jlopez271828.social_contract.types;

import com.mojang.serialization.Codec;
import jlopez271828.social_contract.Happiness;
import jlopez271828.social_contract.Social_contract;
import jlopez271828.social_contract.networking.ClientBoundVillagerInfoPayload;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.UUID;

public class AttachmentTypes {

    public static final AttachmentType<UUID> BED_OWNER_ATTACHMENT = AttachmentRegistry.create(Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "bed_owner_attachment"),
            builder -> builder.persistent(UUIDUtil.CODEC)
    );
    public static final AttachmentType<UUID> BED_PACED_BY = AttachmentRegistry.create(Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "bed_placed_by"),
            builder -> builder.persistent(UUIDUtil.CODEC)
    );
    public static final AttachmentType<ClientBoundVillagerInfoPayload> EXTRA_VILLAGER_MENU_DATA_ATTACHMENT = AttachmentRegistry.create(Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "extra_villager_menu_data"));

    public static final AttachmentType<ServerPlayer> PLAYER_TO_FOLLOW = AttachmentRegistry.create(Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "player_to_follow"));

    public static final AttachmentType<Integer> HAS_VILLAGER_FOLLOWING = AttachmentRegistry.create(Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "has_villager_following"));

    public static final AttachmentType<ItemStack> LAST_GIFTED_BOOK = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "last_gifted_book"),
            builder -> builder.persistent(ItemStack.CODEC)
    );

    public static final AttachmentType<Happiness> VILLAGER_HAPPINESS = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "villager_happiness"),
            builder -> builder.persistent(Happiness.CODEC)
    );

    public static final AttachmentType<Long> LAST_ROOM_SCORE_TIME = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "last_room_score_time"),
            builder -> builder.persistent(Codec.LONG)
    );

    public static final AttachmentType<List<GlobalPos>> ROOM_DOORS = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "room_doors"),
            builder -> builder.persistent(GlobalPos.CODEC.listOf())
    );

    public static final AttachmentType<List<ItemStack>> DECORATION_LIST = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "decoration_list"),
            builder -> builder.persistent(ItemStack.CODEC.listOf())
    );

    public static final AttachmentType<Boolean> SHOULD_DROP_LOOT = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "should_drop_loot"),
            builder -> builder.persistent(Codec.BOOL)
    );



    public static void initialize(){

    }
}
