package jlopez271828.social_contract;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends OptionsSubScreen {


    public ConfigScreen(Screen previous){
        super(previous, Minecraft.getInstance().options, Component.literal("config screen"));
    }



    @Override
    protected void addOptions(){
        if (this.list != null) {
            this.list.addSmall(SocialContractOptions.asOptions());
        }
    }




}
