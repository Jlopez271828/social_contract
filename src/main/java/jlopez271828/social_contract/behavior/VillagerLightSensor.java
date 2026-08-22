package jlopez271828.social_contract.behavior;

import jlopez271828.social_contract.CustomBlocks;
import jlopez271828.social_contract.Social_contract;
import jlopez271828.social_contract.types.AttachmentTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class VillagerLightSensor extends Sensor<Villager> {


    public VillagerLightSensor(){
        super(100);
    }

    @Override
    protected void doTick(ServerLevel level, Villager body) {

        BlockPos center = body.blockPosition();

        List<GlobalPos> results = new ArrayList<>();

        for (int x = -4; x <= 4; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -4; z <= 4; z++) {
                    BlockPos testPos = center.offset(x, y, z);
                    BlockState state = level.getBlockState(testPos);
                    if (state.is(CustomBlocks.VILLAGER_LIGHT_BLOCKITEM)) {
                        results.add(new GlobalPos(level.dimension(), testPos));
                        Social_contract.LOGGER.debug("found villager light");
                    }
                }
            }
        }

        List<GlobalPos> known_lights = body.getAttached(AttachmentTypes.KNOWN_VILLAGER_LIGHTS);

        if (known_lights != null) {

            results.addAll(0, known_lights);
        }
        limitSize(results);
        body.setAttached(AttachmentTypes.KNOWN_VILLAGER_LIGHTS, results);
        

    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of();
    }


    public static void limitSize(List<GlobalPos> known_lights){

        int toRemove = known_lights.size() - 10;

        for(int i = 0; i < toRemove; i++){
            known_lights.removeFirst();
        }

    }


}
