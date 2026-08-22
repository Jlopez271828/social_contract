package jlopez271828.social_contract;

public class SocialContractConfig {


    public static final float ENCHANTED_BOOK_MULTIPLIER = 0.02f;
    public static final int ENCHANTED_BOOK_MAX_USES = 12;
    public static final int ENCHANTMENT_COST_A = 32;
    public static final int ENCHANTMENT_COST_B = 25;
    public static final int[] xpPerLevel = {1, 5, 10, 15, 30};

    public static final int MAX_ROOM_SIZE = 1000;
    public static final int DEATH_REPORT_RADIUS = 50;
    public static final int MAX_HOME_WANDER_DISTANCE = 45;
    //this should be in seconds.
    public static final int ROOM_SCORE_COOLDOWN = 40;


    public static final int MAX_ROOM_HAPPINESS = MAX_ROOM_SIZE;
    public static final int MIN_GOOD_SCORE = 4;

    public static final int MIN_FOLLOW_REPUTATION = 10;
    public static final int MIN_FOLLOW_HAPPINESS = 20;
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
    public static final int MIN_HAPPINESS_REQUEST = 300;
    public static final int MAX_USED_HAPPINESS = MIN_HAPPINESS_REQUEST;
    public static final int HAPPINESS_FOR_ILLAGER = -660 - 6;
    //Happiness values for various events
    public static final int HAPPINESS_FOR_TRADE = 2;
    public static final int HAPPINESS_LOSS_NEARBY_DEATH = 80;
    public static final int HAPPINESS_LOSS_DMG = 15; //per heart
    public static final int MAX_GIFT_HAPPINESS = 25;
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




}
