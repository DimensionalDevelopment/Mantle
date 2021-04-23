// Credit to Immersive Engineering and blusunrize for this class
// See: https://github.com/BluSunrize/ImmersiveEngineering/blob/1.16.5/src/main/java/blusunrize/immersiveengineering/common/util/fakeworld/TemplateWorld.java
package slimeknights.mantle.client.book.structure.world;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.DummyClientTickScheduler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.map.MapState;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.structure.Structure;
import net.minecraft.tag.TagManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.profiler.DummyProfiler;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.TickScheduler;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class TemplateWorld extends World {

  private final Map<String, MapState> maps = new HashMap<>();
  private final Scoreboard scoreboard = new Scoreboard();
  private final RecipeManager recipeManager = new RecipeManager();
  private final TemplateChunkProvider chunkProvider;
  private final DynamicRegistryManager registries = DynamicRegistryManager.create();

  public TemplateWorld(List<Structure.StructureBlockInfo> blocks, Predicate<BlockPos> shouldShow) {
    super(
      new FakeSpawnInfo(), World.OVERWORLD, DimensionType.OVERWORLD,
      () -> DummyProfiler.INSTANCE, true, false, 0
    );

    this.chunkProvider = new TemplateChunkProvider(blocks, this, shouldShow);
  }

  @Override
  public void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags) {

  }

  @Override
  public void playSound(@Nullable PlayerEntity player, double x, double y, double z, @NotNull SoundEvent soundIn, @NotNull SoundCategory category, float volume, float pitch) {
  }

  @Override
  public void playSoundFromEntity(@Nullable PlayerEntity player, Entity entity, SoundEvent sound, SoundCategory category, float volume, float pitch) {

  }

  @Nullable
  @Override
  public Entity getEntityById(int id) {
    return null;
  }

  @Nullable
  @Override
  public MapState getMapState(@NotNull String mapName) {
    return this.maps.get(mapName);
  }

  @Override
  public void putMapState(MapState mapState) {

  }

  @Override
  public int getNextMapId() {
    return this.maps.size();
  }

  @Override
  public void setBlockBreakingInfo(int breakerId, @NotNull BlockPos pos, int progress) {
  }

  @NotNull
  @Override
  public Scoreboard getScoreboard() {
    return this.scoreboard;
  }

  @NotNull
  @Override
  public RecipeManager getRecipeManager() {
    return this.recipeManager;
  }

  @NotNull
  @Override
  public TagManager getTagManager() {
    return TagManager.EMPTY;
  }

  @NotNull
  @Override
  public TickScheduler<Block> getBlockTickScheduler() {
    return DummyClientTickScheduler.get();
  }

  @NotNull
  @Override
  public TickScheduler<Fluid> getFluidTickScheduler() {
    return DummyClientTickScheduler.get();
  }

  @Override
  public ChunkManager getChunkManager() {
    return this.chunkProvider;
  }

  @Override
  public void syncWorldEvent(@Nullable PlayerEntity player, int eventId, BlockPos pos, int data) {

  }

  @NotNull
  @Override
  public List<? extends PlayerEntity> getPlayers() {
    return ImmutableList.of();
  }

  @Override
  public DynamicRegistryManager getRegistryManager() {
    return this.registries;
  }

  @Override
  public float getBrightness(Direction direction, boolean shaded) {
    return 0;
  }

  @Override
  public Biome getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
    return this.getRegistryManager().get(Registry.BIOME_KEY).getOrThrow(BiomeKeys.PLAINS);
  }
}
