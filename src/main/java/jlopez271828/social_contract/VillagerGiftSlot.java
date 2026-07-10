package jlopez271828.social_contract;

import jlopez271828.social_contract.types.CustomItemTags;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

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
        if(itemStack.is(CustomItemTags.VILLAGER_GIFTABLE)){
            return true;
        }else{
            return false;
        }
    }

    //this method does indeed run every time an item is placed inside the slot
    //should be called onPlace tbh
    @Override
    public void setByPlayer(final ItemStack itemStack){
        logger.info("item {} was placed by player", itemStack);
        super.setByPlayer(itemStack);
    }

    public int acceptGift(Villager villager){
        ItemStack gift = container.getItem(3);
        //can take the gift
        if(gift.is(CustomItemTags.VILLAGER_GIFTABLE)){

            int amount = gift.count();
            Item base = gift.getItem();
            gift.shrink(amount);

            return amount;

        }

        //could not take the gift
        return 0;

    }





}
