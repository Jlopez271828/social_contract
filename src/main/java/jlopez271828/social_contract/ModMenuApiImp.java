package jlopez271828.social_contract;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuApiImp implements ModMenuApi {


    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigScreen::new;
    }
}
