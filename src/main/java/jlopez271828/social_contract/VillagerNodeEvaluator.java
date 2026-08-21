package jlopez271828.social_contract;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public class VillagerNodeEvaluator extends WalkNodeEvaluator {




    public int getNeighbors(final Node[] neighbors, final Node pos) {

        int p = 0;
        int jumpSize = 0;
        PathType blockPathTypeAbove = this.getCachedPathType(pos.x, pos.y + 1, pos.z);
        PathType blockPathTypeCurrent = this.getCachedPathType(pos.x, pos.y, pos.z);
        if (this.mob.getPathfindingMalus(blockPathTypeAbove) >= 0.0F && blockPathTypeCurrent != PathType.STICKY_HONEY) {
            jumpSize = Mth.floor(Math.max(1.0F, this.mob.maxUpStep()));
        }

        double posHeight = this.getFloorLevel(new BlockPos(pos.x, pos.y, pos.z));

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            Node node = this.findAcceptedNode(pos.x + direction.getStepX(), pos.y, pos.z + direction.getStepZ(), jumpSize, posHeight, direction, blockPathTypeCurrent);
//            this.reusableNeighbors[direction.get2DDataValue()] = node;
            if (this.isNeighborValid(node, pos)) {
                neighbors[p++] = node;
            }
        }

//        for (Direction direction : Direction.Plane.HORIZONTAL) {
//            Direction secondDirection = direction.getClockWise();
//            if (this.isDiagonalValid(pos, this.reusableNeighbors[direction.get2DDataValue()], this.reusableNeighbors[secondDirection.get2DDataValue()])) {
//                Node diagonalNode = this.findAcceptedNode(
//                        pos.x + direction.getStepX() + secondDirection.getStepX(),
//                        pos.y,
//                        pos.z + direction.getStepZ() + secondDirection.getStepZ(),
//                        jumpSize,
//                        posHeight,
//                        direction,
//                        blockPathTypeCurrent
//                );
//                if (this.isDiagonalValid(diagonalNode)) {
//                    neighbors[p++] = diagonalNode;
//                }
//            }
//        }

        return p;

    }

}
