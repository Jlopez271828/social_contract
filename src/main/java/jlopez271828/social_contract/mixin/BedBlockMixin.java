package jlopez271828.social_contract.mixin;

import jlopez271828.social_contract.types.AttachmentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BedBlock.class)
abstract class BedBlockMixin {

    Logger logger = LoggerFactory.getLogger("social_contract");

    @Inject(method = "setPlacedBy", at = @At(value = "HEAD"))
    private void onBedPlacedMixin(Level level, BlockPos pos, BlockState state, LivingEntity by, ItemStack itemStack, CallbackInfo ci){

        logger.info("bed placed at {} by {}", pos, by);
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity != null && !level.isClientSide() && by instanceof ServerPlayer sp){
            logger.info("saved the placer of this block into the blockentity");
            entity.setAttached(AttachmentTypes.BED_OWNER_ATTACHMENT, sp.getUUID());
        }

    }



}
