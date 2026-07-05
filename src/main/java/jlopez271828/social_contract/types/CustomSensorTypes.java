package jlopez271828.social_contract.types;

import jlopez271828.social_contract.RoomScoreSensor;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class CustomSensorTypes {

    public static final SensorType<RoomScoreSensor> ROOM_SCORE_SENSOR = register("room_score_sensor", RoomScoreSensor::new);
    private static final Logger logger = LoggerFactory.getLogger("social_contract");

    private static <U extends Sensor<?>> SensorType<U> register(final String name, final Supplier<U> factory) {
        return Registry.register(BuiltInRegistries.SENSOR_TYPE, Identifier.withDefaultNamespace(name), new SensorType<>(factory));
    }

    public static void initialize(){
        // static initialization
        logger.info("initializing CustomSensorTypes");
    }

}
