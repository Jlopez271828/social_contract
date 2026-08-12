package jlopez271828.social_contract;

import jlopez271828.social_contract.mixin.VillagerAccessor;
import jlopez271828.social_contract.networking.PacketHandlers;
import jlopez271828.social_contract.old.CustomMemoryModuleType;
import jlopez271828.social_contract.old.CustomMenuTypes;
import jlopez271828.social_contract.types.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import net.minecraft.world.entity.monster.illager.Evoker;
import net.minecraft.world.entity.monster.illager.Pillager;
import net.minecraft.world.entity.monster.illager.Vindicator;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Social_contract implements ModInitializer {
	public static final String MOD_ID = "social_contract";

    public static final float ENCHANTED_BOOK_MULTIPLIER = 0.05f;
    public static final int ENCHANTED_BOOK_MAX_USES = 12;

    public static final int ENCHANTMENT_COST_A = 32;
    public static final int ENCHANTMENT_COST_B = 25;

    public static final float DISCOUNT = 0.80f;

    public static final int[] xpPerLevel = {1, 5, 10, 15, 30};

    public static final int MAX_ROOM_SIZE = 500;
    public static final int MIN_GOOD_SCORE = 4;



    //this should be in ticks.
    public static final long ROOM_SCORE_COOLDOWN = 40 * 20;

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final int MIN_FOLLOW_REPUTATION = 10;
    public static final int MIN_FOLLOW_HAPPINESS = 20;
    public static final int MIN_BREED_HAPPINESS = 60;


    // TODO : rebalance these
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
    public static final int MIN_HAPPINESS_REQUEST = 300;

    public static final int HAPPINESS_FOR_ILLAGER = -660 - 6;

    //Happiness values for various events
    public static final int HAPPINESS_FOR_TRADE = 2;
    public static final int HAPPINESS_LOSS_NEARBY_DEATH = 80;
    public static final int HAPPINESS_LOSS_DMG = 15; //per heart

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


    public static final int MAX_USED_HAPPINESS = MIN_HAPPINESS_REQUEST;

	@Override
	public void onInitialize() {


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
        CustomCriteria.initialize();





	}


    /**
     * This will try to set an Enchanted Book trade from an Enchanted Book or Request gifted to the villager.
     *
     * @param offers The Villager's offers
     * @param villager The Villager
     * @return Whether it could find a suitable enchanted book.
     */
    public static boolean trySetSavedBookTrade(MerchantOffers offers, Villager villager){

        if(villager.hasAttached(AttachmentTypes.LAST_GIFTED_BOOK)){

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

        return false;

    }

    /**
     * Will add a new Enchanted Book trade into the given offers, selecting the enchantment randomly from the given Enchantment Tag.
     * Will decide its cost in emeralds by calling {@link #decideCost(Holder, int, RandomSource)}
     * @param offers the offers in question
     * @param villager the villager in question
     * @param tagKey The enchantment tag to select from
     * @param level The server level
     */
    public static void setRandomEnchantedBookTrade(MerchantOffers offers, Villager villager, TagKey<Enchantment> tagKey, ServerLevel level){


        Optional<HolderSet.Named<Enchantment>> optionalSet = level.registryAccess().get(tagKey);

        if(optionalSet.isPresent()){

            HolderSet<Enchantment> set = optionalSet.get();

            Holder<Enchantment> holder = set.get(villager.getRandom().nextInt(0, set.size() - 1));

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

                return newOffers;
            }

        }



        return newOffers;

    }

    /**
     * Scans a WrittenBookContent's first page and title, if it contains the name of an enchantment, return that enchantment.
     *
     */
    public static Holder.Reference<Enchantment> getEnchantmentRequest(WrittenBookContent content, RegistryAccess access){

        if(content != null){

            Registry<Enchantment> enchantments = access.lookupOrThrow(Registries.ENCHANTMENT);

            List<String> searchStrings = new ArrayList<>();
            searchStrings.add(normalize(content.title().raw()));
            searchStrings.add(normalize(content.pages().getFirst().raw().getString()));

            for(String text : searchStrings){


                for(Identifier identifier : enchantments.keySet()){


                    if(text.contains(identifier.getPath())){

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

                // TODO: I ran into looping issues because I tried to put the logic of setting the update trades flag inside of
                // setHappiness before setting the LAST_ROOM_SCORE_TIME attachment.
                villager.setAttached(AttachmentTypes.LAST_ROOM_SCORE_TIME, level.getGameTime());
                villager.setAttached(AttachmentTypes.ROOM_DOORS, scoreResult.doorList());

                VillagerAccessor accessor = (VillagerAccessor) villager;

                if(accessor.social_contract$shouldIncreaseLevel()){
                    accessor.social_contract$setUpdateMerchantTimer(40);
                    accessor.social_contract$increaseProfessionLevelOnUpdate(true);

                }


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

    /**
     * Will decide the cost of an Enchantment should have when placed on a book and traded.
     * <p>
     * Currently, Enchantments are grouped into 2 groups, A for expensive Enchantments, and B for cheap enchantments.
     * Each group of enchantments has a base cost for a max level enchantment. If an enchantment is of lesser level, it
     * will receive a discount of 5 emeralds per deviation from the max. All items will randomly change their cost following
     *{baseCost  - 4 <= cost <= baseCost + 2}.
     * </p>
     * <p>
     *     This system is designed so that each piece of max enchanted gear will cost about the same, with some deviation
     *     Accounting for gear that has naturally fewer enchantments (Leggings).
     * </p>
     * @param enchantmentHolder The enchantment on the book
     * @param desiredLevel The level that the enchantment will have applied once its on the book.
     * @param random a random source.
     * @return the cost
     */
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

    /**
     * Will change swap out the result of an offer at a given index.
     * @param offers The Offers to change
     * @param index the index of the offer to change
     * @param newResult the ItemStack to replace the old result
     */
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


    public static void summonLighting(Vec3 pos, ServerLevel level){

        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.CONVERSION);
        if(bolt == null){
            return;
        }
        bolt.setVisualOnly(true);
        bolt.setPos(pos);
        level.addFreshEntity(bolt);

    }

    public static AbstractIllager tryConvertVillager(Villager villager, ServerLevel level, int happiness){



        if(happiness < Social_contract.HAPPINESS_FOR_ILLAGER && villager.isAlive()){

            Holder<VillagerProfession> professionHolder = villager.getVillagerData().profession();

            EntityType<? extends AbstractIllager> entityType;


            if(professionHolder.is(VillagerProfession.LIBRARIAN) || professionHolder.is(VillagerProfession.CLERIC)){
                entityType = EntityType.EVOKER;
//                illager2 = EntityType.EVOKER.create(level, EntitySpawnReason.CONVERSION);
            }else if (professionHolder.is(VillagerProfession.FLETCHER)){
                entityType = EntityType.PILLAGER;
//                illager2 = EntityType.PILLAGER.create(level, EntitySpawnReason.CONVERSION);
            }else{
                entityType = EntityType.VINDICATOR;
//                illager2 = EntityType.VINDICATOR.create(level, EntitySpawnReason.CONVERSION);
            }

            ((VillagerAccessor) villager).social_contract$releaseAllPois();

//            Vec3 pos = villager.position();
//            Vec3 lookAngle = villager.getLookAngle();

            AbstractIllager illager = villager.convertTo(entityType, new ConversionParams(ConversionType.SINGLE, false, false, null), mob -> {});
            if(illager != null) {

                Social_contract.summonLighting(illager.position(), (ServerLevel) illager.level());
                illager.setAttached(AttachmentTypes.SHOULD_DROP_LOOT, false);
                List<Player> players =  level.getNearbyPlayers(TargetingConditions.forNonCombat(), illager, AABB.ofSize(illager.position(), 10, 5, 10));
                if(!players.isEmpty()){
                    for(Player player : players){
                        if(player instanceof ServerPlayer sp) {
                            CustomCriteria.CONVERT_ILLAGER.trigger(sp);
                        }
                    }
                }
                return illager;
            }

//            villager.remove(Entity.RemovalReason.DISCARDED);
//            if(illager2 != null){
//
//                illager2.setPos(pos);
//
//                ValueOutput output = new TagValueOutput
//
//                illager2.saveWithoutId(nbt);
//
//
//            }


        }

        return null;


    }


    public static DecorationResult getDecorationSpot(final Vec3 start, RandomSource random, ServerLevel level){

        DecorationResult toReturn;

        for(int i = 0; i < 3; i++){
            toReturn = getPaintingSpot(start, random.nextInt(0, 360), level);
            if(toReturn != null){
                return toReturn;
            }
        }

        return null;


    }



    public static DecorationResult getPaintingSpot(final Vec3 start, double ang, ServerLevel level){



        double dirX = -1 * Math.sin(ang);
        double dirZ = Math.cos(ang);

        int posX = Mth.floor(start.x());
        int posZ = Mth.floor(start.z());
        int posY = Mth.floor(start.y());

        int lastX = posX;
        int lastZ = posZ;

        int stepX;
        if(dirX > 0){
            stepX = 1;
        }else if(dirX < 0){
            stepX = -1;
        }else{
            stepX = 0;
        }

        int stepZ;
        if(dirZ > 0){
            stepZ = 1;
        }else if(dirZ < 0){
            stepZ = -1;
        }else{
            stepZ = 0;
        }

        double delX;
        if(dirX != 0){
            delX = Math.abs(1 / dirX);
        }else{
            delX = Double.POSITIVE_INFINITY;
        }

        double delZ;
        if(dirZ != 0){
            delZ = Math.abs(1 / dirZ);
        }else{
            delZ = Double.POSITIVE_INFINITY;
        }

        double maxX;
        if(dirX > 0){
            maxX = (posX + 1 - start.x()) * delX;
        }else if(dirX < 0){
            maxX = (start.x() - posX) * delX;
        }else{
            maxX = Double.POSITIVE_INFINITY;
        }

        double maxZ;
        if(dirZ > 0){
            maxZ = (posZ + 1 - start.z()) * delZ;
        }else if(dirZ < 0){
            maxZ = (start.z() - posZ) * delZ;
        }else{
            maxZ = Double.POSITIVE_INFINITY;
        }

        for(int i = 0; i < 40; i++){

            BlockPos blockPos = new BlockPos(posX, posY, posZ);
            BlockState state = level.getBlockState(blockPos);
            if(!state.getCollisionShape(level, blockPos, CollisionContext.empty()).isEmpty()){


                BlockPos decorationSpot = new BlockPos(lastX, posY, lastZ);

                if(!level.getBlockState(decorationSpot).isAir()){
                    return null;
                }

                Direction direction;

                if(posX - lastX > 0){ //we moved in the positive x direction: east
                    direction = Direction.WEST;
                }else if(posX - lastX < 0){// we moved in the negative x direction: west
                    direction = Direction.EAST;
                }else if(posZ - lastZ > 0){ //we moved in the positive z direction: south
                    direction = Direction.NORTH;
                }else {// we moved in the negative z direction: north
                    direction = Direction.SOUTH;
                }

                BlockPos decorationFloor = getFloorBelow(decorationSpot, level);

                return new DecorationResult(decorationSpot, decorationFloor, blockPos, direction);
            }
//            else{//for debugging
//                level.setBlock(blockPos, Blocks.RED_STAINED_GLASS.defaultBlockState(), 1);
//            }

            lastX = posX;
            lastZ = posZ;

            if(maxX < maxZ){
                maxX += delX;
                posX += stepX;
            }else{
                maxZ += delZ;
                posZ += stepZ;
            }
        }


        return null;


    }

    public static BlockPos getFloorBelow(final BlockPos start, final ServerLevel level){

        BlockPos current = start;
        BlockPos previous = start;

        for(int i = 0; i < 40; i++){
            BlockState state = level.getBlockState(current);
            if(!state.getCollisionShape(level, current, CollisionContext.empty()).isEmpty()){
                return previous;
            }else{
                previous = current;
                current = current.below();
            }
        }

        return null;

    }



}