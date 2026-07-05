package jlopez271828.social_contract.types;

import jlopez271828.social_contract.Social_contract;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;
import org.lwjgl.system.ffm.mapping.Mapping;

import java.util.UUID;

public class AttachmentTypes {

    public static final AttachmentType<UUID> BED_OWNER_ATTACHMENT = AttachmentRegistry.create(Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "bed_owner_attachment"));
    public static final AttachmentType<Integer> EXTRA_VILLAGER_MENU_DATA_ATTACHMENT = AttachmentRegistry.create(Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "extra_villager_menu_data"));

}
