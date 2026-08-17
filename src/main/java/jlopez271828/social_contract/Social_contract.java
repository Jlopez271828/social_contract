package jlopez271828.social_contract;

import jlopez271828.SocialContractGamerules;
import jlopez271828.social_contract.criteria.CustomCriteria;
import jlopez271828.social_contract.mixin.VillagerAccessor;
import jlopez271828.social_contract.networking.PacketHandlers;
import jlopez271828.social_contract.types.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Social_contract implements ModInitializer {
	public static final String MOD_ID = "social_contract";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    @Override
	public void onInitialize() {


		LOGGER.info("Social Contract Initializing");


        ExtraVillagerScreenWidgets.initialize();
        CustomReputationEventTypes.initialize();
        CustomActivities.initialize();
        CustomItems.initialize();
        PacketHandlers.initialize();
        AttachmentTypes.initialize();
        CustomCriteria.initialize();
        SocialContractGamerules.initialize();
        CustomBlocks.initialize();


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

                offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, cost), Optional.of(new ItemCost(Items.BOOK)), toGive, SocialContractConfig.ENCHANTED_BOOK_MAX_USES, getXpForTradeLevel(villager.getVillagerData().level()), SocialContractConfig.ENCHANTED_BOOK_MULTIPLIER));

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

            offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, cost), Optional.of(new ItemCost(Items.BOOK)), toGive, SocialContractConfig.ENCHANTED_BOOK_MAX_USES, getXpForTradeLevel(villager.getVillagerData().level()), SocialContractConfig.ENCHANTED_BOOK_MULTIPLIER));

        }


    }

    /**
     * Constrains a given set of offers depending upon the assosciated Villager's happiness.
     * @param villager the villager in question
     * @param offers a set of MerchantOffers
     * @return the constrained set of offers
     */
    public static int constrainOffers(Villager villager, MerchantOffers offers){

        int happiness = Happiness.getHappiness(villager);
        int homeScore = Happiness.getHappiness(villager, Happiness.HappinessType.ROOM);
        int level = villager.getVillagerData().level();

        int toReturn = 0;

        if(happiness >= SocialContractConfig.MIN_HAPPINESS_LEVEL_5){
            return offers.size();
        }

        //catching weird edge case
        if(offers.size() < 2){
            return 0;
        }


        toReturn += SocialContractConfig.NUM_TRADES_LEVEL_1;

        // Many calls to offers.size(), I wonder if it would be quicker (but redundant) to simply keep our own size variable
        for(int i = 1; i < Math.min(SocialContractConfig.MIN_HAPPINESS_LEVELS.length, level); i++){


            if(
                    happiness >= SocialContractConfig.MIN_HAPPINESS_LEVELS[i]
                            && offers.size() >= SocialContractConfig.NUM_LEVEL_TRADES[i]
                            && homeScore >= SocialContractConfig.MIN_ROOM_SCORES[i]
            )
            {

                toReturn = SocialContractConfig.NUM_LEVEL_TRADES[i];

            }else{

                return toReturn;
            }

        }



        return toReturn;

    }

    public static void constrainOffers(MerchantOffers offers, int numAvailable){

        if(numAvailable <= 0){
            return;
        }

        for(int i = numAvailable; i < offers.size(); i++){

            offers.get(i).setToOutOfStock();

        }

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

        if(! (level instanceof ServerLevel)){
            return;
        }

        long currentTime = level.getGameTime();

        Long last_score_time = villager.getAttached(AttachmentTypes.LAST_ROOM_SCORE_TIME);

        if(last_score_time == null){
            last_score_time = 0L;
        }

        if(override || currentTime - last_score_time > ((ServerLevel) level).getGameRules().get(SocialContractGamerules.ROOM_RESCORE_COOLDOWN) * 20L) {



            Brain<?> brain = villager.getBrain();
            GlobalPos memory = brain.getMemory(MemoryModuleType.HOME).orElse(null);
            if (memory != null && memory.dimension() == level.dimension() ) {

                ScoreResult scoreResult = Scoring.scoreRoom(memory.pos(), (ServerLevel) level);
                if(scoreResult == null){
                    Happiness.setHappiness(0, villager, Happiness.HappinessType.ROOM);
                    villager.removeAttached(AttachmentTypes.ROOM_DOORS);
                    villager.setAttached(AttachmentTypes.LAST_ROOM_SCORE_TIME, level.getGameTime());
                    return;
                }

                int score = scoreResult.score();

                Happiness.setHappiness(score, villager, Happiness.HappinessType.ROOM);

                villager.setAttached(AttachmentTypes.LAST_ROOM_SCORE_TIME, level.getGameTime());
                villager.setAttached(AttachmentTypes.ROOM_DOORS, scoreResult.doorList());

                VillagerAccessor accessor = (VillagerAccessor) villager;

                if(accessor.social_contract$shouldIncreaseLevel()){
                    accessor.social_contract$setUpdateMerchantTimer(40);
                    accessor.social_contract$increaseProfessionLevelOnUpdate(true);

                }


                if (score > SocialContractConfig.MIN_GOOD_SCORE) {
                    level.broadcastEntityEvent(villager, (byte) 14);

                } else {
                    level.broadcastEntityEvent(villager, (byte) 13);
                }

            } else {
                Happiness.setHappiness(0, villager, Happiness.HappinessType.ROOM);
                level.broadcastEntityEvent(villager, (byte) 13);
            }

        }
//        else{
//            LOGGER.info("A sufficient time has not passed since last room score for this Villager");
//        }

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

        if(level >= SocialContractConfig.xpPerLevel.length){
            return SocialContractConfig.xpPerLevel[SocialContractConfig.xpPerLevel.length - 1];
        }

        if(level < 0){
            return SocialContractConfig.xpPerLevel[0];
        }

        return SocialContractConfig.xpPerLevel[level];

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

        if(happiness >= SocialContractConfig.MIN_HAPPINESS_LEVELS[SocialContractConfig.MIN_HAPPINESS_LEVELS.length - 1] && level >= SocialContractConfig.MIN_HAPPINESS_LEVELS.length){
            return enchantment.getMaxLevel();
        }

        for(int i = SocialContractConfig.MIN_HAPPINESS_LEVELS.length - 2; i >= 0 && i >= level - 1; i--){

            if(happiness >= SocialContractConfig.MIN_HAPPINESS_LEVELS[i] && level >= i + 1){

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

        LightningBolt bolt = EntityTypes.LIGHTNING_BOLT.create(level, EntitySpawnReason.CONVERSION);
        if(bolt == null){
            return;
        }
        bolt.setVisualOnly(true);
        bolt.setPos(pos);
        level.addFreshEntity(bolt);

    }

    public static void tryConvertVillager(Villager villager, ServerLevel level, int happiness){



        if(happiness < SocialContractConfig.HAPPINESS_FOR_ILLAGER && villager.isAlive()){

            Holder<VillagerProfession> professionHolder = villager.getVillagerData().profession();

            EntityType<? extends AbstractIllager> entityType;


            if(professionHolder.is(VillagerProfession.LIBRARIAN) || professionHolder.is(VillagerProfession.CLERIC)){
                entityType = EntityTypes.EVOKER;
            }else if (professionHolder.is(VillagerProfession.FLETCHER)){
                entityType = EntityTypes.PILLAGER;
            }else{
                entityType = EntityTypes.VINDICATOR;
            }

            ((VillagerAccessor) villager).social_contract$releaseAllPois();


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
            }


        }


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


    /**
     * Casts a virtual 'ray', gets the blocks along its path, returns once one of those blocks are solid.
     * @param start the block to start the ray from
     * @param ang the angle of the ray
     * @param level the level the ray exists in
     * @return A DecorationResult record containing the air block before the solid block, the solid block, the floor beneath the air block.
     */
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