package jlopez271828.social_contract;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;

public class VillagerFollowButton extends Button.Plain {

    public VillagerFollowButton(final int x, final int y, final Button.OnPress onPress){
        super(x, y, 18, 18, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
        this.visible = true;
    }




}
