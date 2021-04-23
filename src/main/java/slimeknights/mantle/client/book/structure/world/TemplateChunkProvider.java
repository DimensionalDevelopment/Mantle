// Credit to Immersive Engineering and blusunrize for this class
// See: https://github.com/BluSunrize/ImmersiveEngineering/blob/1.16.5/src/main/java/blusunrize/immersiveengineering/common/util/fakeworld/TemplateChunkProvider.java
package slimeknights.mantle.client.book.structure.world;

import com.mojang.datafixers.util.Pair;
import net.minecraft.structure.Structure;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.light.LightingProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TemplateChunkProvider extends ChunkManager {

  private final Map<ChunkPos, Chunk> chunks;
  private final World world;
  private final LightingProvider lightManager;

  public TemplateChunkProvider(List<Structure.StructureBlockInfo> blocks, World world, Predicate<BlockPos> shouldShow) {
    this.world = world;
    this.lightManager = new LightingProvider(this, true, true);
    Map<ChunkPos, List<Structure.StructureBlockInfo>> byChunk = new HashMap<>();

    for (Structure.StructureBlockInfo info : blocks) {
      byChunk.computeIfAbsent(new ChunkPos(info.pos), $ -> new ArrayList<>()).add(info);
    }

    this.chunks = byChunk.entrySet().stream()
      .map(e -> Pair.of(e.getKey(), new TemplateChunk(world, e.getKey(), e.getValue(), shouldShow)))
      .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
  }

  @Nullable
  @Override
  public Chunk getChunk(int chunkX, int chunkZ, @NotNull ChunkStatus requiredStatus, boolean load) {
    return this.chunks.computeIfAbsent(new ChunkPos(chunkX, chunkZ), p -> new EmptyChunk(world, p));
  }

  @Override
  public String getDebugString() {
    return "?";
  }

  @Override
  public LightingProvider getLightingProvider() {
    return this.lightManager;
  }

  @NotNull
  @Override
  public BlockView getWorld() {
    return this.world;
  }
}
