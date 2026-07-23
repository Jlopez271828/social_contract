package jlopez271828.social_contract;

import jlopez271828.social_contract.types.AttachmentTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.slf4j.Logger;

import java.util.Optional;

abstract public class Happiness {

    private static final Logger logger = Social_contract.LOGGER;

    public static void increaseHappiness(int toAdd, Villager villager){

        Integer happiness = villager.getAttached(AttachmentTypes.VILLAGER_HAPPINESS);

        if(happiness == null){
            villager.setAttached(AttachmentTypes.VILLAGER_HAPPINESS, toAdd);
        }else{
            villager.setAttached(AttachmentTypes.VILLAGER_HAPPINESS, toAdd + happiness);
        }

    }



}
