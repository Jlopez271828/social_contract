package jlopez271828.social_contract;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;

import java.util.UUID;

public class AttachmentTypes {

    public static final AttachmentType<UUID> BED_OWNER_ATTACHMENT = AttachmentRegistry.create(Identifier.fromNamespaceAndPath(Social_contract.MOD_ID, "bed_owner_attachment"));

}
