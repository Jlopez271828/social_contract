package jlopez271828.social_contract.mixin;


import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import jlopez271828.social_contract.*;
import jlopez271828.social_contract.behavior.CustomGoalPackages;
import jlopez271828.social_contract.criteria.CustomCriteria;
import jlopez271828.social_contract.networking.ClientBoundVillagerInfoPayload;
import jlopez271828.social_contract.types.AttachmentTypes;
import jlopez271828.social_contract.types.CustomActivities;
import jlopez271828.social_contract.types.CustomReputationEventTypes;
import jlopez271828.social_contract.types.CustomSensorTypes;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.ActivityData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.*;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager  {

    //Dummy constructor
    VillagerMixin(final EntityType<? extends Villager> type, final Level level){
        super(type, level);
    }

    @Shadow
    public abstract @NonNull Brain<?> getBrain();

    @Shadow
    @Final
    private GossipContainer gossips;




    @ModifyArgs(method = "<clinit>",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/ai/Brain;provider(Ljava/util/Collection;Lnet/minecraft/world/entity/ai/Brain$ActivitySupplier;)Lnet/minecraft/world/entity/ai/Brain$Provider;"
            )
    )
    private static void addCustomVillagerSensors(Args args){
        Collection<SensorType<?>> altered = new ArrayList<SensorType<?>>(args.get(0));
        altered.add(CustomSensorTypes.VILLAGER_LIGHT_SENSOR);
        args.set(0, altered);


    }

    @Shadow public abstract VillagerData getVillagerData();

    @ModifyReturnValue(method = "lambda$static$0",
            at = @At("RETURN")
    )
    private static List<ActivityData<?>> registerCustomActivity(List<ActivityData<?>> original){
        original.add(ActivityData.create(CustomActivities.FOLLOW_FRIEND, CustomGoalPackages.getFollowPackage()));


        return original;
    }


    @Inject(method = "releaseAllPois", at = @At(value = "HEAD"))
    private void clearBedAttachments(CallbackInfo ci){

        Brain<?> brain = this.getBrain();

        GlobalPos gpos = brain.getMemory(MemoryModuleType.HOME).orElse(null);

        if(gpos != null){
            //TODO: verify if I even should check dimensions
            if(this.level().dimension() == gpos.dimension()){
                if(this.hasAttached(AttachmentTypes.BED_OWNER_ATTACHMENT)){
                    this.removeAttached(AttachmentTypes.BED_OWNER_ATTACHMENT);
                }
            }
        }


    }

    @Inject(method = "onReputationEventFrom", at = @At("HEAD"))
    private void extraEntityEvents(ReputationEventType type, Entity source, CallbackInfo ci){
        if(type == CustomReputationEventTypes.PLACED_BED_CLAIMED){
            this.gossips.add(source.getUUID(), GossipType.MAJOR_POSITIVE, 10);
        }

    }

    @Inject(method = "startTrading",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/npc/villager/Villager;openTradingScreen(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/network/chat/Component;I)V")
    )
    private void sendExtraPacket(Player player, CallbackInfo ci){

        if(player instanceof ServerPlayer sp) {
            Social_contract.scoreRoomWrapper((Villager) (Object) this);
            Happiness happiness = Happiness.getOrAttach((Villager) (Object) this);
            Map<Happiness.HappinessType, Integer> map = happiness.getMap();

            Level level = player.level();
            if(happiness.getMap().get(Happiness.HappinessType.ROOM) > 500 && player instanceof ServerPlayer) {
                CustomCriteria.NICE_PLACE_CRITERION.trigger((ServerPlayer) player);
            }


            MerchantOffers offers = this.getOffers();
            int numAvailableOffers = Social_contract.constrainOffers((Villager) (Object) this, offers);

            if(numAvailableOffers > 0){
                Social_contract.constrainOffers(offers, numAvailableOffers);
            }

            if(happiness.totalHappiness >= SocialContractConfig.MIN_HAPPINESS_DISCOUNT && this.getVillagerData().level() > 3){
                for(MerchantOffer offer : offers){
                    offer.setSpecialPriceDiff(-1 * Mth.ceil(0.20 * offer.getCostA().count()));
                }
            }

            ServerPlayNetworking.send(sp, new ClientBoundVillagerInfoPayload(
                    this.getId(),
                    0,
                    happiness.totalHappiness,
                    map.get(Happiness.HappinessType.ROOM),
                    map.get(Happiness.HappinessType.GIFT),
                    map.get(Happiness.HappinessType.TRADE),
                    map.get(Happiness.HappinessType.PAIN))
            );

        }
    }

    //this runs each time a villager levels up
    @Inject(method = "updateTrades", at = @At("TAIL"))
    private void addCustomTrades(ServerLevel level, CallbackInfo ci, @Local(name = "data") VillagerData data){


        if(data.profession().is(VillagerProfession.LIBRARIAN)) {

            MerchantOffers offers = this.getOffers();

            int profession_level = data.level();
            if (profession_level >= 3) {

                //if we couldn't use the last traded book (or if there is no last traded book)
                if (!Social_contract.trySetSavedBookTrade(offers, (Villager) (Object) this)) {

                    //noinspection DataFlowIssue
                    Social_contract.setRandomEnchantedBookTrade(offers, (Villager) (Object) this, EnchantmentTags.TRADEABLE, level);


                }


            }

        }



            int professionLevel = data.level();

        if(professionLevel > 4){

            if(this.offers == null){
                return;
            }

            this.offers.add(
                    new MerchantOffer(
                            new ItemCost(Items.EMERALD, 10),
                            Optional.of(new ItemCost(Items.GOLD_INGOT, 10)),
                            new ItemStack(CustomItems.VILLAGER_TOTEM),
                            1,
                            SocialContractConfig.xpPerLevel[4],
                            SocialContractConfig.ENCHANTED_BOOK_MULTIPLIER
                    )
            );
        }






    }

    @ModifyReturnValue(method = "shouldIncreaseLevel", at = @At("RETURN"))
    private boolean constrainUpdate(boolean original, @Local(name = "currentLevel") int currentLevel){

        if(!original){
            return false;
        }

        //currentLevel should already be bounds-checked from the original function
        // However, not bounds checking scares me
        // If you think this is unnecessary, by all means, make a GitHub issue about it.
        if(currentLevel < 5 && currentLevel > 0) {

            return Happiness.check((Villager) (Object) this, SocialContractConfig.MIN_HAPPINESS_LEVELS[currentLevel]);


        }

        return false;

    }


    @Inject(
            method = "die",
            at = @At("TAIL")
    )
    private void tellNearbyVillagersThatIDied(DamageSource source, CallbackInfo ci){

        Level level = this.level();

        if(!(level instanceof ServerLevel) ){
            return;
        }

        Vec3 position = this.position();
        double radius = ((ServerLevel) level).getGameRules().get(SocialContractGamerules.VILLAGER_DEATH_REPORT_RADIUS);

        List<Villager> villagers = level().getEntitiesOfClass(Villager.class, AABB.ofSize(position, radius * 2, 5, radius * 2));

        villagers.remove((Villager) (Object) this);



        for(Villager villager : villagers){

            Happiness.decreaseHappiness(SocialContractConfig.HAPPINESS_LOSS_NEARBY_DEATH, villager);
            villager.playSound(SoundEvents.VILLAGER_HURT);
            ((AbstractVillagerAccessor) villager).social_contract$addParticlesAroundSelf(ParticleTypes.SMOKE);


        }

    }

    @Inject(
            method = "setLastHurtByMob",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;onReputationEvent(Lnet/minecraft/world/entity/ai/village/ReputationEventType;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/entity/ReputationEventHandler;)V"
            )
    )
    private void onHurt(LivingEntity hurtBy, CallbackInfo ci){

        Happiness.decreaseHappiness(SocialContractConfig.HAPPINESS_LOSS_DMG, (Villager) (Object) this);

    }


}
