package jlopez271828.social_contract;

import com.mojang.serialization.Codec;
import jlopez271828.social_contract.mixin.VillagerAccessor;
import jlopez271828.social_contract.types.AttachmentTypes;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.npc.villager.Villager;

import java.util.*;


/**
 * Class that manages happiness. This class is meant to resemble the Villager gossips system and is currently using
 * data attachments to function. I'm a bit worried with storing such a large class as a data attachment, but there
 * aren't many good alternatives.
 */
public class Happiness {

    private final Map<HappinessType, Integer> store;
    //Tracker for quick access for comparisons
    private int totalHappiness;

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

    public void recalculateTotal() {

        int total = 0;
        for (int value : store.values()) {
            total += value;
        }
        this.totalHappiness = total;

    }


    //helper function to be called statically
    public static void increaseHappiness(int toAdd, Villager villager, HappinessType type) {

        Happiness happiness = getOrAttach(villager);


        happiness.increaseHappiness(toAdd, type);


        villager.setAttached(AttachmentTypes.VILLAGER_HAPPINESS, happiness);

        VillagerAccessor accessor = (VillagerAccessor) villager;

        if(accessor.social_contract$shouldIncreaseLevel()){
            accessor.social_contract$setUpdateMerchantTimer(40);
            accessor.social_contract$increaseProfessionLevelOnUpdate(true);

        }


    }

    //Pain is the negative storage for happiness, and all positive additions will first go to making pain 0.
    public void increaseHappiness(int toAdd, HappinessType type) {

        int painValue = this.store.get(HappinessType.PAIN);
        if(painValue > 0){
            painValue += toAdd;
            if(painValue > 0){
                toAdd = painValue;
                this.store.put(HappinessType.PAIN, 0);
            }else{
                return;
            }
        }

        this.store.put(type, Math.min(type.maxValue, toAdd));

    }

    //helper function to be called statically
    public static void decreaseHappiness(int toSubtract, Villager villager, HappinessType type) {

        Happiness happiness = getOrAttach(villager);

        happiness.decreaseHappiness(toSubtract, type);

        villager.setAttached(AttachmentTypes.VILLAGER_HAPPINESS, happiness);

    }

    public static void decreaseHappiness(int toSubtract, Villager villager){

        Happiness happiness = getOrAttach(villager);

        happiness.decreaseHappiness(toSubtract, HappinessType.PAIN);

        villager.setAttached(AttachmentTypes.VILLAGER_HAPPINESS, happiness);

    }

    public void decreaseHappiness(int toSubtract, HappinessType type) {

        int oldValue = this.store.get(type);
        int newValue = Math.max(oldValue - toSubtract, type.minValue);

        this.store.put(type, newValue);
        this.totalHappiness -= oldValue - newValue;

    }


    /**
     * Static helper function which calls {@link #setHappiness(int, HappinessType)}
     * @param value the value to set
     * @param villager the Villager in question
     * @param type the specific {@link HappinessType} to use.
     */
    public static void setHappiness(int value, Villager villager, HappinessType type){

        Happiness happiness = getOrAttach(villager);

        happiness.setHappiness(value, type);

        villager.setAttached(AttachmentTypes.VILLAGER_HAPPINESS, happiness);



        VillagerAccessor accessor = (VillagerAccessor) villager;

        if(accessor.social_contract$shouldIncreaseLevel()){
            accessor.social_contract$setUpdateMerchantTimer(40);
            accessor.social_contract$increaseProfessionLevelOnUpdate(true);

        }



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
        TRADE(Social_contract.MAX_TRADE_HAPPINESS, 0, "trade"),
        GIFT(Social_contract.MAX_GIFT_HAPPINESS, 0, "gift"),
        ROOM(Social_contract.MAX_ROOM_HAPPINESS, 0, "room");


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
        public String getSerializedName() {
            return this.name;
        }
    }


}

