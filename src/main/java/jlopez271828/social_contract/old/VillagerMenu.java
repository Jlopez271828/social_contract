package jlopez271828.social_contract.old;

import jlopez271828.social_contract.types.CustomMenuTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.ClientSideMerchant;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public class VillagerMenu extends AbstractContainerMenu {

//    private static final int PAYMENT_SLOT1 = 0;
//    private static final int PAYMENT_SLOT2 = 1;
//    private static final int RESULT_SLOT = 2;
//    private static final int GIFT_SLOT = 3;
//
//    private static final int INV_SLOT_START = 4;
//    private static final int INV_SLOT_END = 31;
//    private static final int HOTBAR_SLOT_START = 31;
//    private static final int HOTBAR_SLOT_END = 40;
//    private static final int SELLSLOT1_X = 136;
//    private static final int SELLSLOT2_X = 162;
//    private static final int BUYSLOT_X = 220;
//    private static final int ROW_Y = 37;
//
//    private static final int GIFTSLOT_X = 220;
//    private static final int GIFTSLOT_Y = 64;

    private final VillagerMenuContainer tradeContainer;
    private final Merchant trader;

    private int merchantLevel;
    private boolean showProgressBar;
    private boolean canRestock;

    public VillagerMenu(final int containerId, final Inventory inventory) {
        this(containerId, inventory, new ClientSideMerchant(inventory.player));
    }


    public VillagerMenu(final int containerId, final Inventory inventory, final Merchant villager){
        super(CustomMenuTypes.VILLAGER, containerId);
        this.trader = villager;
        this.tradeContainer = new VillagerMenuContainer(villager);
        this.addSlot(new Slot(this.tradeContainer, 0, 136, 37));
        this.addSlot(new Slot(this.tradeContainer, 1, 162, 37));
        this.addSlot(new VillagerResultSlot(inventory.player, villager, this.tradeContainer, 2, 220, 37));
//        this.addSlot(new VillagerGiftSlot(this.tradeContainer, 3, 220, 64));
        this.addStandardInventorySlots(inventory, 108, 84);
    }


    @Override
    public void slotsChanged(final Container container) {
        this.tradeContainer.updateSellItem();
        super.slotsChanged(container);
    }

    @Override
    public boolean stillValid(final Player player) {
        return this.trader.stillValid(player);
    }

    @Override
    public boolean canTakeItemForPickAll(final ItemStack carried, final Slot target) {
        return false;
    }

    @Override
    public ItemStack quickMoveStack(final Player player, final int slotIndex) {
        ItemStack clicked = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            clicked = stack.copy();
            if (slotIndex == 2) {
                if (!this.moveItemStackTo(stack, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(stack, clicked);
                this.playTradeSound();
            } else if (slotIndex != 0 && slotIndex != 1) {
                if (slotIndex >= 4 && slotIndex < 31) {
                    if (!this.moveItemStackTo(stack, 31, 40, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (slotIndex >= 31 && slotIndex < 40 && !this.moveItemStackTo(stack, 4, 31, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 4, 40, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == clicked.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }

        return clicked;
    }

    private void playTradeSound() {
        if (!this.trader.isClientSide()) {
            Entity entity = (Entity)this.trader;
            entity.level().playLocalSound(entity.getX(), entity.getY(), entity.getZ(), this.trader.getNotifyTradeSound(), SoundSource.NEUTRAL, 1.0F, 1.0F, false);
        }
    }

    @Override
    public void removed(final Player player) {
        super.removed(player);
        this.trader.setTradingPlayer(null);
        if (!this.trader.isClientSide()) {
            if (!player.isAlive() || player instanceof ServerPlayer && ((ServerPlayer)player).hasDisconnected()) {
                ItemStack itemStack = this.tradeContainer.removeItemNoUpdate(0);
                if (!itemStack.isEmpty()) {
                    player.drop(itemStack, false);
                }

                itemStack = this.tradeContainer.removeItemNoUpdate(1);
                if (!itemStack.isEmpty()) {
                    player.drop(itemStack, false);
                }
            } else if (player instanceof ServerPlayer) {
                player.getInventory().placeItemBackInInventory(this.tradeContainer.removeItemNoUpdate(0));
                player.getInventory().placeItemBackInInventory(this.tradeContainer.removeItemNoUpdate(1));
            }
        }
    }


    public void tryMoveItems(final int newTradeIndex) {
        if (newTradeIndex >= 0 && this.getOffers().size() > newTradeIndex) {
            ItemStack oldCostA = this.tradeContainer.getItem(0);
            if (!oldCostA.isEmpty()) {
                if (!this.moveItemStackTo(oldCostA, 4, 40, true)) {
                    return;
                }

                this.tradeContainer.setItem(0, oldCostA);
            }

            ItemStack oldCostB = this.tradeContainer.getItem(1);
            if (!oldCostB.isEmpty()) {
                if (!this.moveItemStackTo(oldCostB, 4, 40, true)) {
                    return;
                }

                this.tradeContainer.setItem(1, oldCostB);
            }

            if (this.tradeContainer.getItem(0).isEmpty() && this.tradeContainer.getItem(1).isEmpty()) {
                MerchantOffer merchantOffer = (MerchantOffer)this.getOffers().get(newTradeIndex);
                this.moveFromInventoryToPaymentSlot(0, merchantOffer.getItemCostA());
                merchantOffer.getItemCostB().ifPresent(costB -> this.moveFromInventoryToPaymentSlot(1, costB));
            }
        }
    }

    private void moveFromInventoryToPaymentSlot(final int paymentSlot, final ItemCost cost) {
        for (int i = 4; i < 40; i++) {
            ItemStack inventoryItem = this.slots.get(i).getItem();
            if (!inventoryItem.isEmpty() && cost.test(inventoryItem)) {
                ItemStack currentPaymentItem = this.tradeContainer.getItem(paymentSlot);
                if (currentPaymentItem.isEmpty() || ItemStack.isSameItemSameComponents(inventoryItem, currentPaymentItem)) {
                    int maxStackSize = inventoryItem.getMaxStackSize();
                    int moveCount = Math.min(maxStackSize - currentPaymentItem.getCount(), inventoryItem.getCount());
                    ItemStack newPaymentItem = inventoryItem.copyWithCount(currentPaymentItem.getCount() + moveCount);
                    inventoryItem.shrink(moveCount);
                    this.tradeContainer.setItem(paymentSlot, newPaymentItem);
                    if (newPaymentItem.getCount() >= maxStackSize) {
                        break;
                    }
                }
            }
        }
    }

    public void setOffers(final MerchantOffers offers) {
        this.trader.overrideOffers(offers);
    }

    public MerchantOffers getOffers() {
        return this.trader.getOffers();
    }

    public boolean showProgressBar() {
        return this.showProgressBar;
    }

    public int getTraderXp() {
        return this.trader.getVillagerXp();
    }

    public int getFutureTraderXp() {
        return this.tradeContainer.getFutureXp();
    }

    public void setXp(final int xp) {
        this.trader.overrideXp(xp);
    }

    public void setMerchantLevel(final int level) {
        this.merchantLevel = level;
    }

    public int getTraderLevel() {
        return this.merchantLevel;
    }

    public void setCanRestock(final boolean canRestock) {
        this.canRestock = canRestock;
    }

    public boolean canRestock() {
        return this.canRestock;
    }

    public void setSelectionHint(final int hint) {
        this.tradeContainer.setSelectionHint(hint);
    }

    public void setShowProgressBar(final boolean show) {
        this.showProgressBar = show;
    }

    public Merchant getTrader(){
        return this.trader;
    }

}
