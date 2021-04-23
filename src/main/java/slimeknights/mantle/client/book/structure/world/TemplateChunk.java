// Credit to Immersive Engineering and blusunrize for this class
// See: https://github.com/BluSunrize/ImmersiveEngineering/blob/1.16.5/src/main/java/blusunrize/immersiveengineering/common/util/fakeworld/TemplateChunk.java
package slimeknights.mantle.client.book.structure.world;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.structure.Structure;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.EmptyChunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class TemplateChunk extends EmptyChunk {

  private final Map<BlockPos, Structure.StructureBlockInfo> blocksInChunk;
  private final Map<BlockPos, BlockEntity> tiles;
  private final Predicate<BlockPos> shouldShow;

  public TemplateChunk(World worldIn, ChunkPos chunkPos, List<Structure.StructureBlockInfo> blocksInChunk, Predicate<BlockPos> shouldShow) {
    super(worldIn, chunkPos);
    this.shouldShow = shouldShow;
    this.blocksInChunk = new HashMap<>();
    this.tiles = new HashMap<>();

    for (Structure.StructureBlockInfo info : blocksInChunk) {
      this.blocksInChunk.put(info.pos, info);

      if (info.tag != null) {
        BlockEntity tile = BlockEntity.createFromTag(info.state, info.tag);

        if (tile != null) {
          tile.setLocation(worldIn, info.pos);
          this.tiles.put(info.pos, tile);
        }
      }
    }
  }

  @NotNull
  @Override
  public BlockState getBlockState(@NotNull BlockPos pos) {
    if (this.shouldShow.test(pos)) {
      Structure.StructureBlockInfo result = this.blocksInChunk.get(pos);

      if (result != null)
        return result.state;
    }

    return Blocks.VOID_AIR.getDefaultState();
  }

  @NotNull
  @Override
  public FluidState getFluidState(@NotNull BlockPos pos) {
    return getBlockState(pos).getFluidState();
  }


  @Nullable
  @Override
  public BlockEntity getBlockEntity(@NotNull BlockPos pos, @NotNull CreationType creationMode) {
    if (!this.shouldShow.test(pos))
      return null;

    return this.tiles.get(pos);
  }
}
