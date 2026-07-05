package jlopez271828.social_contract.types;

import jlopez271828.social_contract.Social_contract;
import jlopez271828.social_contract.old.VillagerMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomMenuTypes {

    public static final MenuType<VillagerMenu> VILLAGER = register("villager", VillagerMenu::new);

    public static Logger logger = LoggerFactory.getLogger(Social_contract.MOD_ID);

    private static <T extends AbstractContainerMenu> MenuType<T> register(final String name, final MenuType.MenuSupplier<T> constructor) {
        return Registry.register(BuiltInRegistries.MENU, name, new MenuType<>(constructor, FeatureFlags.VANILLA_SET));
    }

    public static void initialize(){
        logger.info("initializing custom menu types");
    }

}
