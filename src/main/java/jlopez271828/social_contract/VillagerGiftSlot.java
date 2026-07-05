package jlopez271828.social_contract;

import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;

public class VillagerGiftSlot extends Slot {

    private final MerchantContainer container;
    private int removeCount;


    public VillagerGiftSlot(final MerchantContainer container, final int id, final int x, final int y){

        super(container, id, x, y);
        this.container = container;



    }

    @Override
    public boolean mayPlace(final ItemStack itemStack) {
        return true; //will add better logic later
    }





}
