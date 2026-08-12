package jlopez271828.social_contract;

import com.terraformersmc.modmenu.config.ModMenuConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends OptionsSubScreen {

    private static final int MAX_ROOM_FIELD_X = 20;
    private static final int MAX_ROOM_FIELD_Y = 20;

    public ConfigScreen(Screen previous){
        super(previous, Minecraft.getInstance().options, Component.literal("config screen"));
    }


    @Override
    protected void addOptions(){
        if (this.list != null) {
            this.list.addSmall(ModMenuConfig.asOptions());
        }
    }




}
