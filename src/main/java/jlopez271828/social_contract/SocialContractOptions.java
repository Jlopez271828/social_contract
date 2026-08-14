package jlopez271828.social_contract;

import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import org.slf4j.Logger;


import java.util.ArrayList;
import java.util.List;


public class SocialContractOptions {

    private static Logger logger = Social_contract.LOGGER;
    private static final RandomSource randomSource = RandomSource.create();

    private static final OptionInstance<Boolean> useless = OptionInstance.createBoolean(
            "social_contract.options.useless",
            false,
            value -> {
                SoundManager sm = Minecraft.getInstance().getSoundManager();

                int rand = randomSource.nextInt(0, 3);
                SoundEvent event = switch (rand) {
                    case 0 -> SoundEvents.VILLAGER_AMBIENT;
                    case 1 -> SoundEvents.VILLAGER_CELEBRATE;
                    case 2 -> SoundEvents.VILLAGER_NO;
                    default -> SoundEvents.VILLAGER_YES;
                };

                sm.play(SimpleSoundInstance.forUI(event, 1));
            }
    );


    public static OptionInstance<?>[] asOptions(){
        List<OptionInstance<?>> list = new ArrayList<>();
        list.add(useless);
        list.add(useless);
        list.add(useless);
        list.add(useless);
        list.add(useless);
        list.add(useless);
        list.add(useless);

        return list.toArray(OptionInstance[]::new);
    }

}
