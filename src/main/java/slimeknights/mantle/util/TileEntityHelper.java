package slimeknights.mantle.util;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.Mantle;

import java.util.Optional;

/**
 * Utilities to help in handling of tile entities
 */
@SuppressWarnings("WeakerAccess")
public class TileEntityHelper {
  private TileEntityHelper() {
  }

  /**
   * Gets a tile entity if present and the right type
   * @param clazz  Tile entity class
   * @param world  World instance
   * @param pos    Tile entity position
   * @param <T>    Tile entity type
   * @return  Optional of the tile entity, empty if missing or wrong class
   */
  public static <T> Optional<T> getTile(Class<T> clazz, @Nullable BlockView world, BlockPos pos) {
    return getTile(clazz, world, pos, false);
  }

  /**
   * Gets a tile entity if present and the right type
   * @param clazz         Tile entity class
   * @param world         World instance
   * @param pos           Tile entity position
   * @param logWrongType  If true, logs a warning if the type is wrong
   * @param <T>    Tile entity type
   * @return  Optional of the tile entity, empty if missing or wrong class
   */
  public static <T> Optional<T>  getTile(Class<T> clazz, @Nullable BlockView world, BlockPos pos, boolean logWrongType) {
    if (!isBlockLoaded(world, pos)) {
      return Optional.empty();
    }

    //TODO: This causes freezes if being called from onLoad
    BlockEntity tile = world.getBlockEntity(pos);
    if (tile == null) {
      return Optional.empty();
    }

    if (clazz.isInstance(tile)) {
      return Optional.of(clazz.cast(tile));
    } else if (logWrongType) {
      Mantle.logger.warn("Unexpected TileEntity class at {}, expected {}, but found: {}", pos, clazz, tile.getClass());
    }

    return Optional.empty();
  }

  /**
   * Checks if the given block is loaded
   * @param world  World instance
   * @param pos    Position to check
   * @return  True if its loaded
   */
  public static boolean isBlockLoaded(@Nullable BlockView world, BlockPos pos) {
    if (world == null) {
      return false;
    }
    if (world instanceof WorldView) {
      return ((WorldView) world).isChunkLoaded(pos);
    }
    return true;
  }
}
