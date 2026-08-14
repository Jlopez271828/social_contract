package jlopez271828.social_contract;

import jlopez271828.social_contract.criteria.CustomCriteria;
import jlopez271828.social_contract.types.AttachmentTypes;
import jlopez271828.social_contract.types.CustomItemTags;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;


public class VillagerGiftSlot extends Slot {

    private final MerchantContainer container;
    private int removeCount;
    private final Logger logger = Social_contract.LOGGER;



    public VillagerGiftSlot(final MerchantContainer container, final int id, final int x, final int y){

        super(container, id, x, y);
        this.container = container;



    }

    @Override
    public boolean mayPlace(final ItemStack itemStack) {
        return itemStack.is(CustomItemTags.VILLAGER_GIFTABLE);
    }

    //this method does indeed run every time an item is placed inside the slot
    //should be called onPlace tbh
//    @Override
//    public void setByPlayer(final ItemStack itemStack){
//        logger.info("item {} was placed by player", itemStack);
//        super.setByPlayer(itemStack);
//    }

    /**
     * This method runs the logic when a villager accepts a gift. This method will be ran from the server.
     * It is here, in the gift slot, due to easy access
     * to the needed data.
     *
     * @param villager the Villager in question.
     */
    public void acceptGift(Villager villager){
        ItemStack gift = container.getItem(3);
        if(gift.is(Items.AIR)){
            return;
        }
        //can take the gift
        if(gift.is(CustomItemTags.VILLAGER_GIFTABLE)){


            if(gift.is(Items.ENCHANTED_BOOK)) {

                // TODO: implement a system to keep the specific giftable items for professions in their own classes.
                if(!villager.getVillagerData().profession().is(VillagerProfession.LIBRARIAN)){
                    villager.playSound(SoundEvents.VILLAGER_NO);
                    return;

                }


                EnchantmentInstance giftInfo  = Social_contract.getFirstEnchantment(gift);
                if(giftInfo == null){
                    return;
                }

                //For a gifted enchanted book, it will only accept it if the level of the book is at the max or lower than it's allowed to sell
                if(giftInfo.level() > Social_contract.getMaxAllowedEnchantmentLevel(villager, giftInfo.enchantment().value())){
                    villager.playSound(SoundEvents.VILLAGER_NO);
                    return;
                }

                // You can clone restricted enchantments with a librarian, however, that villager will need a very high happiness to do so.
                if(!giftInfo.enchantment().is(EnchantmentTags.TRADEABLE) && !Happiness.check(villager, SocialContractConfig.MIN_HAPPINESS_REQUEST)){
                    villager.playSound(SoundEvents.VILLAGER_NO);
                    return;
                }

                MerchantOffers offers = villager.getOffers();
                for (int i = 0; i < offers.size(); i++) {
                    MerchantOffer offer = offers.get(i);
                    ItemStack result = offer.getResult();
                    if (result.is(Items.ENCHANTED_BOOK)) {

                        EnchantmentInstance tempInfo = Social_contract.getFirstEnchantment(result);
                        if(tempInfo == null){
                            return;
                        }

                        Holder<Enchantment> tempEnchantment = tempInfo.enchantment();

                        if(tempEnchantment.equals(giftInfo.enchantment())){

                            if(giftInfo.level() > tempInfo.level()){

                                Social_contract.changeOfferResult(offers, i, EnchantmentHelper.createBook(giftInfo));
                                gift.shrink(1);

                            } else if(giftInfo.level() == tempInfo.level() && Social_contract.getMaxAllowedEnchantmentLevel(villager, tempEnchantment.value()) >= tempInfo.level() + 1){

                                Social_contract.changeOfferResult(
                                        offers,
                                        i,
                                        EnchantmentHelper.createBook(
                                                new EnchantmentInstance(
                                                        tempEnchantment,
                                                        Math.min(
                                                                tempInfo.level() + 1,
                                                                tempEnchantment.value().getMaxLevel()
                                                        )
                                                )
                                        )
                                );

                                container.removeItem(3, 1);


                            }

                            return;
                        }

                    }

                }



                ItemStack toGive = new ItemStack(Holder.direct(gift.getItem()), 1, gift.getComponentsPatch());

                villager.setAttached(AttachmentTypes.LAST_GIFTED_BOOK, toGive);

            }

            if(gift.is(Items.WRITTEN_BOOK)) {


                if(!villager.getVillagerData().profession().is(VillagerProfession.LIBRARIAN)){
                    villager.playSound(SoundEvents.VILLAGER_NO);
                    return;

                }

                if (!Happiness.check(villager, SocialContractConfig.MIN_HAPPINESS_REQUEST)) {
                    villager.playSound(SoundEvents.VILLAGER_NO);
                    return;
                }

                WrittenBookContent content = gift.get(DataComponents.WRITTEN_BOOK_CONTENT);
                Holder.Reference<Enchantment> enchantment = Social_contract.getEnchantmentRequest(content, villager.registryAccess());
                if(enchantment != null && enchantment.is(EnchantmentTags.TRADEABLE)){

                    villager.playSound(SoundEvents.VILLAGER_CELEBRATE);

                    Player player = villager.getTradingPlayer();

                    if(player instanceof ServerPlayer){
                        CustomCriteria.SUCCESSFULL_REQUEST_CRITERION.trigger((ServerPlayer) player);
                    }

                    ItemStack requested = EnchantmentHelper.createBook(new EnchantmentInstance(enchantment, enchantment.value().getMaxLevel()));
                    villager.setAttached(AttachmentTypes.LAST_GIFTED_BOOK, requested);
                    container.removeItem(3, 1);

                }else{
                    villager.playSound(SoundEvents.VILLAGER_NO);
                }

                return;
            }

            if(gift.is(CustomItemTags.VILLAGER_DECORATION)){
                List<ItemStack> decorationList = villager.getAttached(AttachmentTypes.DECORATION_LIST);

                if(decorationList == null){
                    decorationList = new ArrayList<>();
                }

                decorationList.add(gift.copy());
                villager.setAttached(AttachmentTypes.DECORATION_LIST, decorationList);
            }

            if(Villager.FOOD_POINTS.containsKey(gift.getItem())){
                villager.getInventory().addItem(gift);
            }

            int amount = gift.count();
            container.removeItem(3, amount);


            Happiness.increaseHappiness(amount, villager, Happiness.HappinessType.GIFT);


            if(amount > 0){
                villager.playSound(SoundEvents.VILLAGER_CELEBRATE);
                Player player = villager.getTradingPlayer();
                if(player instanceof ServerPlayer){
                    CustomCriteria.GIVE_GIFT_CRITERION.trigger((ServerPlayer) player);
                }

            }else{
                villager.playSound(SoundEvents.VILLAGER_NO);
            }

            return;

        }

        //could not take the gift

        villager.playSound(SoundEvents.VILLAGER_NO);

    }








}
