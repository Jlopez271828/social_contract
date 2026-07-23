package jlopez271828.social_contract.types;

import com.mojang.serialization.Codec;
import jlopez271828.social_contract.Social_contract;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.system.ffm.mapping.Mapping;

import java.util.UUID;

public class AttachmentTypes {

    public static final AttachmentType<UUID> BED_OWNER_ATTACHMENT = AttachmentRegistry.create(Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "bed_owner_attachment"),
            builder -> {
        builder.persistent(UUIDUtil.CODEC);
    });
    public static final AttachmentType<UUID> BED_PACED_BY = AttachmentRegistry.create(Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "bed_placed_by"),
            builder -> {
        builder.persistent(UUIDUtil.CODEC);
    });
    public static final AttachmentType<Integer> EXTRA_VILLAGER_MENU_DATA_ATTACHMENT = AttachmentRegistry.create(Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "extra_villager_menu_data"));

    public static final AttachmentType<Boolean> HAS_VILLAGER_FOLLOWING = AttachmentRegistry.create(Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "has_villager_following"));

    public static final AttachmentType<ItemStack> LAST_GIFTED_BOOK = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "last_gifted_book"),
            builder -> builder.persistent(ItemStack.CODEC));

    public static final AttachmentType<Integer> VILLAGER_HAPPINESS = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "villager_happiness"),
            builder -> {
                builder.persistent(Codec.INT);
            }
    );
}
