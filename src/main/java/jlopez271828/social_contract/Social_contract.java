package jlopez271828.social_contract;

import jlopez271828.social_contract.networking.PacketHandlers;
import jlopez271828.social_contract.types.*;
import net.fabricmc.api.ModInitializer;

import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Social_contract implements ModInitializer {
	public static final String MOD_ID = "social_contract";

    public static final float ENCHANTED_BOOK_MULTIPLIER = 0.05f;
    public static final int ENCHANTED_BOOK_MAX_USES = 12;

    public static final int[] xpPerLevel = {1, 5, 10, 15, 30};

    public static final int MAX_ROOM_SIZE = 500;
    private static final int MIN_GOOD_SCORE = 4;

    //this should be in ticks.
    private static final long ROOM_SCORE_COOLDOWN = 40 * 20;

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final int MIN_FOLLOW_REPUTATION = 10;
    public static final int MIN_FOLLOW_HAPPINESS = 10;
    public static final int MIN_BREED_HAPPINESS = 60;

    //Minimum happiness values for a villager to level up
    public static final int MIN_HAPPINESS_LEVEL_2 = 20;
    public static final int MIN_HAPPINESS_LEVEL_3 = 50;
    public static final int MIN_HAPPINESS_LEVEL_4 = 90;
    public static final int MIN_HAPPINESS_LEVEL_5 = 150;

    public static final int[] MIN_HAPPINESS_LEVELS = {
            0,
            MIN_HAPPINESS_LEVEL_2,
            MIN_HAPPINESS_LEVEL_3,
            MIN_HAPPINESS_LEVEL_4,
            MIN_HAPPINESS_LEVEL_5
    };

    public static final int MIN_HAPPINESS_DISCOUNT = 200;
    public static final int MIN_HAPPINESS_REQUEST = 280;

    //Happiness values for various events
    public static final int HAPPINESS_FOR_TRADE = 2;
    public static final int HAPPINESS_LOSS_NEARBY_DEATH = 5;
    public static final int HAPPINESS_LOSS_DMG = 5;

    public static final int MAX_GIFT_HAPPINESS = 25;
    public static final int MAX_ROOM_HAPPINESS = MAX_ROOM_SIZE;
    public static final int MAX_TRADE_HAPPINESS = 25;


    public static final int MIN_ROOM_SCORE_LEVEL_2 = 4;
    public static final int MIN_ROOM_SCORE_LEVEL_3 = 9;
    public static final int MIN_ROOM_SCORE_LEVEL_4 = 20;
    public static final int MIN_ROOM_SCORE_LEVEL_5 = 50;

    public static final int[] MIN_ROOM_SCORES = {
            0,
            MIN_ROOM_SCORE_LEVEL_2,
            MIN_ROOM_SCORE_LEVEL_3,
            MIN_ROOM_SCORE_LEVEL_4,
            MIN_ROOM_SCORE_LEVEL_5
    };

    public static final int DEATH_REPORT_RADIUS = 50;

    //Maybe I'll use these maybe not
    public static final int NUM_TRADES_LEVEL_1 = 2;
    public static final int NUM_TRADES_LEVEL_2 = 4;
    public static final int NUM_TRADES_LEVEL_3 = 6;
    public static final int NUM_TRADES_LEVEL_4 = 8;
    public static final int NUM_TRADES_LEVEL_5 = 10;

    public static final int[] NUM_LEVEL_TRADES = {
            NUM_TRADES_LEVEL_1,
            NUM_TRADES_LEVEL_2,
            NUM_TRADES_LEVEL_3,
            NUM_TRADES_LEVEL_4,
            NUM_TRADES_LEVEL_5
    };

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

        CustomMemoryModuleType.initialize();
        CustomSensorTypes.initialize();
        CustomMenuTypes.initialize();
        ExtraVillagerScreenWidgets.initialize();
        CustomReputationEventTypes.initialize();
        CustomActivities.initialize();
        CustomItems.initialize();
        PacketHandlers.initialize();
        AttachmentTypes.initialize();





	}


    public static boolean trySetSavedBookTrade(MerchantOffers offers, Villager villager){

        if(villager.hasAttached(AttachmentTypes.LAST_GIFTED_BOOK)){

            LOGGER.info("this villager has been given an enchanted book");
            ItemStack gift = villager.getAttached(AttachmentTypes.LAST_GIFTED_BOOK);

            if(gift != null) {

                EnchantmentInstance enchantmentInstance = getFirstEnchantment(gift);
                if(enchantmentInstance == null){
                    return false;
                }

                Holder<Enchantment> enchantmentHolder = enchantmentInstance.enchantment();

                int level = enchantmentInstance.level();

                ItemStack toGive = EnchantmentHelper.createBook(
                        new EnchantmentInstance(
                                enchantmentHolder,
                                Math.min(
                                        getMaxAllowedEnchantmentLevel(villager, enchantmentHolder.value()),
                                        level
                                )
                        )
                );

                int cost = decideCost(enchantmentHolder, level, villager.getRandom());

                villager.removeAttached(AttachmentTypes.LAST_GIFTED_BOOK);

                offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, cost), Optional.of(new ItemCost(Items.BOOK)), toGive, ENCHANTED_BOOK_MAX_USES, getXpForTradeLevel(villager.getVillagerData().level()), ENCHANTED_BOOK_MULTIPLIER));
                return true;


            }

        }

        LOGGER.info("this villager has not been given an enchanted book");
        return false;

    }

    public static void setRandomEnchantedBookTrade(MerchantOffers offers, Villager villager, TagKey<Enchantment> tagKey, ServerLevel level){


        LOGGER.info("trying to set random enchanted book trade");
        Optional<HolderSet.Named<Enchantment>> optionalSet = level.registryAccess().get(tagKey);

        if(optionalSet.isPresent()){

            HolderSet<Enchantment> set = optionalSet.get();

            Holder<Enchantment> holder = set.get(villager.getRandom().nextInt(0, set.size() - 1));

            LOGGER.info("enchantment found is {}", holder.value());


//            int maxLevel = holder.value().getMaxLevel();
//            int minLevel = holder.value().getMinLevel();
//            int enchantLevel = minLevel;
//            int currentHappiness = Happiness.getHappiness(villager);
//            for(int i = Math.min(maxLevel, MIN_HAPPINESS_LEVELS.length); i >= minLevel; i--){
//                if(currentHappiness >= MIN_HAPPINESS_LEVELS[i - 1]){
//                    enchantLevel = i;
//                    break;
//                }
//            }

            int enchantLevel = getMaxAllowedEnchantmentLevel(villager, holder.value());

            ItemStack toGive = EnchantmentHelper.createBook(new EnchantmentInstance(holder, enchantLevel));

            int cost = decideCost(holder, enchantLevel, villager.getRandom());

            offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, cost), Optional.of(new ItemCost(Items.BOOK)), toGive, ENCHANTED_BOOK_MAX_USES, getXpForTradeLevel(villager.getVillagerData().level()), ENCHANTED_BOOK_MULTIPLIER));

        }


    }

    /**
     * Constrains a given set of offers depending upon the assosciated Villager's happiness.
     * @param villager the villager in question
     * @param offers a set of MerchantOffers
     * @return the constrained set of offers
     */
    public static MerchantOffers constrainOffers(Villager villager, MerchantOffers offers){

        MerchantOffers newOffers = new MerchantOffers();


        int happiness = Happiness.getHappiness(villager);
        int homeScore = Happiness.getHappiness(villager, Happiness.HappinessType.ROOM);
        int level = villager.getVillagerData().level();


        if(happiness >= Social_contract.MIN_HAPPINESS_LEVEL_5){
            return offers;
        }

        //catching weird edge case
        if(offers.size() < 2){
            return newOffers;
        }

        for(int i = 0; i < NUM_TRADES_LEVEL_1; i++){
            newOffers.add(offers.get(newOffers.size()));
        }

        // Many calls to offers.size(), I wonder if it would be quicker (but redundant) to simply keep our own size variable
        for(int i = 1; i < Math.min(MIN_HAPPINESS_LEVELS.length, level); i++){

            LOGGER.info("checking requirements for level {}", i + 1);
            if(
                    happiness >= Social_contract.MIN_HAPPINESS_LEVELS[i]
                            && offers.size() >= Social_contract.NUM_LEVEL_TRADES[i]
                            && homeScore >= Social_contract.MIN_ROOM_SCORES[i]
            )
            {
                int tempSize = newOffers.size();
                for(int j = 0; j < Social_contract.NUM_LEVEL_TRADES[i] - tempSize; j++){
                    newOffers.add(offers.get(newOffers.size()));
                }

            }else{
                LOGGER.info("this villager does not meet requirements for level {}", i + 1);
                return newOffers;
            }

        }



        return newOffers;

    }


    public static Holder.Reference<Enchantment> getEnchantmentRequest(WrittenBookContent content, RegistryAccess access){

        if(content != null){

            Registry<Enchantment> enchantments = access.lookupOrThrow(Registries.ENCHANTMENT);

            List<String> searchStrings = new ArrayList<>();
            searchStrings.add(normalize(content.title().raw()));
            searchStrings.add(normalize(content.pages().getFirst().raw().getString()));

            for(String text : searchStrings){

                LOGGER.info(text);

                for(Identifier identifier : enchantments.keySet()){


                    if(text.contains(identifier.getPath())){

                        LOGGER.info("found match: {}", identifier);
                        return enchantments.get(identifier).orElse(null);

                    }


                }

            }

        }

        return null;

    }

    private static String normalize(String text) {
        return text.toLowerCase(Locale.ENGLISH)
                .replace(' ', '_');
    }

    /**
     * Will attempt to score a villagers room, but first it checks to see if a sufficient time has passed
     * Since the last score before re-scoring, unless the override is set.
     * 
     * <p>
     *     To skip the cooldown, use {@link #scoreRoomWrapper(Villager, boolean)}
     * </p>
     *      
     * @param villager the Villager in question
     */
    public static void scoreRoomWrapper(Villager villager){
        scoreRoomWrapper(villager, false);
    }

    /**
     * Will attempt to score a villagers room, but first it checks to see if a sufficient time has passed
     * Since the last score before re-scoring, unless the override is set.
     *
     * @param villager the villager in question
     *
     * @param override set this to true to skip the cooldown
     */
    public static void scoreRoomWrapper(Villager villager, boolean override){

        Level level = villager.level();

        long currentTime = level.getGameTime();

        Long last_score_time = villager.getAttached(AttachmentTypes.LAST_ROOM_SCORE_TIME);

        if(last_score_time == null){
            last_score_time = 0L;
        }

        if((override || currentTime - last_score_time > ROOM_SCORE_COOLDOWN) && level instanceof ServerLevel serverLevel) {


            LOGGER.info("begging score room task\n");
            Brain<?> brain = villager.getBrain();
            GlobalPos memory = brain.getMemory(MemoryModuleType.HOME).orElse(null);
            if (memory != null && memory.dimension() == level.dimension() ) {

                ScoreResult scoreResult = Scoring.scoreRoom(memory.pos(), serverLevel);
                if(scoreResult == null){
                    Happiness.setHappiness(0, villager, Happiness.HappinessType.ROOM);
                    villager.removeAttached(AttachmentTypes.ROOM_DOORS);
                    villager.setAttached(AttachmentTypes.LAST_ROOM_SCORE_TIME, level.getGameTime());
                    return;
                }

                int score = scoreResult.score();

                LOGGER.info("room scored with a score of {}", score);

                Happiness.setHappiness(score, villager, Happiness.HappinessType.ROOM);

                villager.setAttached(AttachmentTypes.LAST_ROOM_SCORE_TIME, level.getGameTime());
                villager.setAttached(AttachmentTypes.ROOM_DOORS, scoreResult.doorList());

                if (score > MIN_GOOD_SCORE) {
                    level.broadcastEntityEvent(villager, (byte) 14);

                } else {
                    level.broadcastEntityEvent(villager, (byte) 13);
                }

            } else {
                LOGGER.info("No home memory");
                Happiness.setHappiness(0, villager, Happiness.HappinessType.ROOM);
                level.broadcastEntityEvent(villager, (byte) 13);
            }

        }else{
            LOGGER.info("A sufficient time has not passed since last room score for this Villager");
        }

    }

    public static int decideCost(Holder<Enchantment> enchantmentHolder, int desiredLevel, RandomSource random){

        int cost = enchantmentHolder.is(CustomEnchantmentTags.ENCHANTMENT_GROUP_B) ? 25 : 32;

        Enchantment enchantment = enchantmentHolder.value();

        cost = cost - (enchantment.getMaxLevel() - desiredLevel) * 5;

        cost += -4 + random.nextInt(0, 6);

        return cost;

    }



    public static int getXpForTradeLevel(int level){

        if(level >= xpPerLevel.length){
            return xpPerLevel[xpPerLevel.length - 1];
        }

        if(level < 0){
            return xpPerLevel[0];
        }

        return xpPerLevel[level];

    }


    /**
     * Gets the max allowed enchantment level for an enchanted book sold by a Villager.
     * @param villager the villager in question
     * @param enchantment the enchantment in question
     * @return the max level allowed
     */
    public static int getMaxAllowedEnchantmentLevel(Villager villager, Enchantment enchantment){

        int happiness = Happiness.getHappiness(villager);
        int level = villager.getVillagerData().level();

        if(happiness >= MIN_HAPPINESS_LEVELS[MIN_HAPPINESS_LEVELS.length - 1] && level >= MIN_HAPPINESS_LEVELS.length){
            return enchantment.getMaxLevel();
        }

        for(int i = MIN_HAPPINESS_LEVELS.length - 2; i >= 0 && i >= level - 1; i--){

            if(happiness >= MIN_HAPPINESS_LEVELS[i] && level >= i + 1){

                return i + 1;

            }

        }

        return 1;

    }

    public static EnchantmentInstance getFirstEnchantment(ItemStack itemStack){

        ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting(itemStack);

        Set<Holder<Enchantment>> set =  enchantments.keySet();

        if(!set.isEmpty()){

            Holder<Enchantment> first = set.stream().toList().getFirst();

            return new EnchantmentInstance(first, enchantments.getLevel(first));

        }

        return null;


    }

    public static void changeOfferResult(MerchantOffers offers, int index, ItemStack newResult){

        MerchantOffer offer = offers.get(index);
        ItemStack result = offer.getResult();
        offers.remove(index);
        offers.add(
                index,
                new MerchantOffer(
                        offer.getItemCostA(),
                        offer.getItemCostB(),
                        newResult,
                        offer.getUses(),
                        offer.getXp(),
                        offer.getPriceMultiplier()
                )
        );


    }



}