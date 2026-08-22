package jlopez271828.social_contract.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import jlopez271828.social_contract.types.AttachmentTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractIllager.class)
abstract class AbstractIllagerMixin extends MonsterMixin {

    @Override
    protected boolean overrideLootDrop(ServerLevel level, Operation<Boolean> original){
        Boolean shouldDropLoot = ( (AbstractIllager) (Object) this).getAttached(AttachmentTypes.SHOULD_DROP_LOOT);
        if(shouldDropLoot != null && shouldDropLoot == false){
            return false;
        }else{
            return original.call(level);
        }
    }


}
