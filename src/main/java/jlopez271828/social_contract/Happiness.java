package jlopez271828.social_contract;

import com.mojang.serialization.Codec;
import jlopez271828.social_contract.criteria.CustomCriteria;
import jlopez271828.social_contract.mixin.VillagerAccessor;
import jlopez271828.social_contract.types.AttachmentTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.NonNull;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;


/**
 * Class that manages happiness. This class is meant to resemble the Villager gossips system and is currently using
 * data attachments to function. I'm a bit worried with storing such a large class as a data attachment, but there
 * aren't many good alternatives.
 *
 * <p>
 *     This class works by using a map of Enums {@link HappinessType} to integers, the value of that particular happiness.
 *     The total happiness is the sum of all types. There exists a {@link #totalHappiness} field that serves
 *     to facilitate quick lookups, it is updated whenever happiness is changed.
 * </p>
 */
public class Happiness {

    private final Map<HappinessType, Integer> store;
    //Tracker for quick access for comparisons
    public int totalHappiness;

    public static final Codec<Happiness> CODEC = Codec.unboundedMap(HappinessType.CODEC, Codec.INT)
            .xmap(Happiness::new, happiness -> happiness.store);


    public Happiness() {
        this.store = new EnumMap<>(HappinessType.class);
        for (HappinessType type : HappinessType.values()) {
            this.store.put(type, 0);
        }
        this.totalHappiness = 0;
    }

    public Happiness(Map<HappinessType, Integer> store) {

        this.store = new EnumMap<>(HappinessType.class);
        this.store.putAll(store);

        for (HappinessType type : HappinessType.values()) {
            this.store.putIfAbsent(type, 0);
        }
        recalculateTotal();

    }

    /**
     * Recalculates the {@link #totalHappiness} field of this Happiness class.
     */
    public void recalculateTotal() {

        int total = 0;
        for (int value : store.values()) {
            total += value;
        }
        this.totalHappiness = total;

    }


    /**
     * This is a static helper method used to increase happiness of a specific type. This will get a Villager's Happiness
     * Attaching one if it does not have it, and then increase the given type's value, bounded by that type's maximum.
     * @param toAdd the amount to add
     * @param villager the Villager in question
     * @param type The happiness to increase
     */
    public static void increaseHappiness(int toAdd, Villager villager, HappinessType type) {

        Happiness happiness = getOrAttach(villager);


        happiness.increaseHappiness(toAdd, type);


        villager.setAttached(AttachmentTypes.VILLAGER_HAPPINESS, happiness);

        VillagerAccessor accessor = (VillagerAccessor) villager;

        if(accessor.social_contract$shouldIncreaseLevel()){
            accessor.social_contract$setUpdateMerchantTimer(40);
            accessor.social_contract$increaseProfessionLevelOnUpdate(true);

        }

        if(villager.level() instanceof ServerLevel serverLevel) {
            if(happiness.totalHappiness >= SocialContractConfig.MIN_HAPPINESS_REQUEST) {
                List<Player> players = serverLevel.getNearbyPlayers(TargetingConditions.forNonCombat(), villager, AABB.ofSize(villager.position(), 10, 5, 10));
                if (!players.isEmpty()) {
                    for (Player player : players) {
                        if (player instanceof ServerPlayer sp) {
                            CustomCriteria.ALTRUIST_CRITERION.trigger(sp);
                        }
                    }
                }
            }
        }


    }

    /**
     * This method will increase the value of a Happiness type, bounded by that type's maximum. This method will first
     * try to increase the PAIN value towards its maximum of 0, then it will increase the given type's value.
     * @param toAdd the amount to add
     * @param type the type of happiness to increase.
     */
    public void increaseHappiness(int toAdd, HappinessType type) {

        //pain is negative
        int oldPainValue = this.store.get(HappinessType.PAIN);
        if(oldPainValue < HappinessType.PAIN.maxValue){
            int newPainValue = oldPainValue + toAdd;
            if(newPainValue > HappinessType.PAIN.maxValue){
                toAdd = newPainValue;
                this.totalHappiness += newPainValue - oldPainValue;
                this.store.put(HappinessType.PAIN, 0);
            }else{
                this.store.put(HappinessType.PAIN, newPainValue);
                this.totalHappiness += toAdd;
                return;
            }



        }

        int oldValue = this.store.get(type);
        int newValue = Math.min(type.maxValue, oldValue + toAdd);

        this.store.put(type, Math.min(type.maxValue, newValue));

        this.totalHappiness += newValue - oldValue;

    }

    /**
     * This is a static helper method to decrease a Villager's happiness. It will get that Villager's happiness, attaching
     * one if that Village doesn't have it, and then decrease the given type's value, bounded by that type's minimum.
     * @param toSubtract the amount to subtract
     * @param villager the Villager to subtract from
     * @param type the type of happiness to subtract.
     */
    public static void decreaseHappiness(int toSubtract, Villager villager, HappinessType type) {

        Happiness happiness = getOrAttach(villager);

        happiness.decreaseHappiness(toSubtract, type);

        villager.setAttached(AttachmentTypes.VILLAGER_HAPPINESS, happiness);

    }

    /**
     * This is a static helper method to decrease a Villager's happiness. It will get the Villager's happiness, attaching
     * one if that Villager does not have it, then decrease its PAIN value.
     * @param toSubtract the amount to subtract.
     * @param villager the Villager to subtract from
     */
    public static void decreaseHappiness(int toSubtract, Villager villager){

        Happiness happiness = getOrAttach(villager);

        happiness.decreaseHappiness(toSubtract, HappinessType.PAIN);

        villager.setAttached(AttachmentTypes.VILLAGER_HAPPINESS, happiness);

        if(villager.level() instanceof ServerLevel serverLevel) {
            Social_contract.tryConvertVillager(villager, serverLevel, happiness.totalHappiness);
        }



    }

    /**
     * This will decrease the value of a specific type of happiness. Bounded by that type's minimum.
     * @param toSubtract the amount to subtract
     * @param type the type of happiness to subtract from
     */
    public void decreaseHappiness(int toSubtract, HappinessType type) {

        int oldValue = this.store.get(type);
        int newValue = Math.max(oldValue - toSubtract, type.minValue);

        this.store.put(type, newValue);
        this.totalHappiness -= oldValue - newValue;

    }


    /**
     * Static helper function which calls {@link #setHappiness(int, HappinessType)} on the happiness attached to
     * the Villager. Will attach a happiness to the Villager if it does not have one.
     * @param value the value to set
     * @param villager the Villager in question
     * @param type the specific {@link HappinessType} to use.
     */
    public static void setHappiness(int value, Villager villager, HappinessType type){

        Happiness happiness = getOrAttach(villager);

        happiness.setHappiness(value, type);

        villager.setAttached(AttachmentTypes.VILLAGER_HAPPINESS, happiness);


    }


    /**
     * Sets the value of a specific {@link HappinessType}
     * @param value the value to set
     * @param type the {@link HappinessType} to set
     */
    public void setHappiness(int value, HappinessType type){

        int oldValue = this.store.get(type);
        int delta = value - oldValue;
        this.store.put(type, value);
        this.totalHappiness += delta;

    }

    /**
     * gets the total happiness of a villager, rescoring its room in the process.
     * If there is no happiness attached to this Villager, returns 0
     * <p>
     * If you do not want to rescore the room, use {@link #getHappiness(Villager, boolean, boolean)}
     * @param villager the villager in question
     * @return the total happiness
     */
    public static int getHappiness(Villager villager){
        return getHappiness(villager, false, false);
    }

    /**
     * gets the total happiness of a Villager
     * @param villager the villager in question.
     * @param overrideCooldown set this to true to override the cooldown of the room score
     * @param overrideRescore set this to true to skip the rescore entirely
     * @return the total happiness
     */
    public static int getHappiness(Villager villager, boolean overrideCooldown, boolean overrideRescore){

        if(!overrideRescore) {
            Social_contract.scoreRoomWrapper(villager, overrideCooldown);
        }

        Happiness happiness = villager.getAttached(AttachmentTypes.VILLAGER_HAPPINESS);

        if(happiness == null){
            return 0;
        }else{
            return happiness.totalHappiness;
        }
    }

    /**
     * Gets a specific {@link HappinessType} of a Villager's {@link Happiness}
     *
     * <p>
     *     does not rescore a Villager's room unless the HappinessType is ROOM
     * </p>
     *
     * @param villager The Villager in question
     * @param type the corresponding {@link HappinessType}
     * @return the specific value requested
     */
    public static int getHappiness(Villager villager, HappinessType type){
        Happiness happiness = villager.getAttached(AttachmentTypes.VILLAGER_HAPPINESS);
        if(happiness != null){
            if(type == HappinessType.ROOM){
                Social_contract.scoreRoomWrapper(villager);
            }
            return happiness.store.get(type);
        }else{
            return 0;
        }

    }

    /**
     * Gets a Villager's Attached {@link Happiness}, and attaches a new one if
     * an attachment does not exist
     * @param villager the Villager in question
     * @return the Villager's {@link  Happiness}
     */
    public static Happiness getOrAttach(Villager villager) {

        Happiness happiness = villager.getAttached(AttachmentTypes.VILLAGER_HAPPINESS);
        if (!villager.hasAttached(AttachmentTypes.VILLAGER_HAPPINESS) || happiness == null) {

            happiness = new Happiness();
            villager.setAttached(AttachmentTypes.VILLAGER_HAPPINESS, happiness);

        }

        return happiness;

    }

    /**
     * Compares the given value to the Villager's total happines
     * with value >= happiness
     * <p>
     *      If a Villager has no happiness, it returns false
     * </p>
     * <p>
     *     Will try to rescore a Villager's room. To control this process, use {@link #check(Villager, int, boolean, boolean)}
     * </p>
     * To only check a certain {@link HappinessType} use {@link #check(Villager, int, HappinessType)}
     *
     * @param villager the villager in question
     * @param value the value the happiness must be at least to return true, inclusive.
     *
     */
    public static boolean check(Villager villager, int value){
        return check(villager, value, false, false);
    }

    /**
     * Compares the given value to the Villager's total happines
     * with value >= happiness
     * <br>
     * If a Villager has no happiness, it returns false
     * <br>
     * To only check a certain {@link HappinessType} use {@link #check(Villager, int, HappinessType)}
     *
     * @param villager the villager in question
     * @param minValue the value the happiness must be at least to return true, inclusive.
     * @param overrideCooldown set this to true to override the rescore cooldown.
     * @param overrideRescore set this to true to skip the rescore.
     *
     */
    public static boolean check(Villager villager, int minValue, boolean overrideCooldown, boolean overrideRescore) {

        if(!overrideRescore){
            Social_contract.scoreRoomWrapper(villager, overrideCooldown);
        }

        Happiness happiness = villager.getAttached(AttachmentTypes.VILLAGER_HAPPINESS);

        if (happiness == null) {
            return false;
        }

        return happiness.totalHappiness >= minValue;

    }

    /**
     * Compares the value of a certain {@link HappinessType} of the Villager's {@link Happiness} to
     * the provided value
     * <p>
     *     Does not attempt to rescore the Villager's room unless the type is ROOM
     * </p>
     * @param villager the Villager in question
     * @param minValue the value the happiness must be at least to return true
     * @param type the specific HappinessType to check.
     * @return the resulting boolean
     */
    public static boolean check(Villager villager, int minValue, HappinessType type) {

        Happiness happiness = villager.getAttached(AttachmentTypes.VILLAGER_HAPPINESS);

        if (happiness == null) {
            return false;
        }

        if(type == HappinessType.ROOM){
            Social_contract.scoreRoomWrapper(villager);
        }

        return happiness.store.get(type) >= minValue;

    }

    @Override
    public String toString(){

        return "Total Happiness: " + this.totalHappiness + "\n" + this.store.toString();

    }


    public enum HappinessType implements StringRepresentable {

        PAIN(0, -999, "pain"),
        TRADE(SocialContractConfig.MAX_TRADE_HAPPINESS, 0, "trade"),
        GIFT(SocialContractConfig.MAX_GIFT_HAPPINESS, 0, "gift"),
        ROOM(SocialContractConfig.MAX_ROOM_HAPPINESS, 0, "room");


        public final int maxValue;
        public final int minValue;
        private final String name;

        public static final Codec<HappinessType> CODEC = StringRepresentable.fromEnum(HappinessType::values);


        HappinessType(int maxValue, int minValue, String name) {
            this.maxValue = maxValue;
            this.minValue = minValue;
            this.name = name;
        }

        @Override
        public @NonNull String getSerializedName() {
            return this.name;
        }
    }

    public Map<HappinessType, Integer> getMap(){
        return this.store;
    }


}

