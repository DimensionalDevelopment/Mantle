package slimeknights.mantle.registration.deferred;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.types.Type;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Deferred register to register tile entity instances
 */
public class TileEntityTypeDeferredRegister extends DeferredRegisterWrapper {
  public TileEntityTypeDeferredRegister(String modID) {
    super(modID);
  }

  /**
   * Gets the data fixer type for the tile entity instance
   * @param name  Tile entity name
   * @return  Data fixer type
   */
  @Nullable
  private Type<?> getType(String name) {
    return Util.getChoiceType(TypeReferences.BLOCK_ENTITY, resourceName(name));
  }

  /**
   * Registers a tile entity type for a single block
   * @param name     Tile entity name
   * @param factory  Tile entity factory
   * @param block    Single block to add
   * @param <T>      Tile entity type
   * @return  Registry object instance
   */
  public <T extends BlockEntity> BlockEntityType<T> register(String name, Supplier<? extends T> factory, Supplier<? extends Block> block) {
    return (BlockEntityType<T>) Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(modID, name), BlockEntityType.Builder.create(factory, block.get()).build(null));
  }

  /**
   * Registers a new tile entity type using a tile entity factory and a block supplier
   * @param name             Tile entity name
   * @param factory          Tile entity factory
   * @param blockCollector   Function to get block list
   * @param <T>              Tile entity type
   * @return  Tile entity type registry object
   */
  @SuppressWarnings("ConstantConditions")
  public <T extends BlockEntity> BlockEntityType<T> register(String name, Supplier<? extends T> factory, Consumer<ImmutableSet.Builder<Block>> blockCollector) {
    return Registry.register(Registry.BLOCK_ENTITY_TYPE,
            new Identifier(modID, name), doConcernMagic(factory, blockCollector));
  }

  private <T extends BlockEntity> BlockEntityType<T> doConcernMagic(Supplier<? extends T> factory, Consumer<ImmutableSet.Builder<Block>> blockCollector) {
    ImmutableSet.Builder<Block> blocks = new ImmutableSet.Builder<>();
    blockCollector.accept(blocks);
    ImmutableList<Block> builtBlocks = blocks.build().asList();
    Block[] realBlocks = new Block[builtBlocks.size()];
    for (int i = 0; i < builtBlocks.size(); i++) {
      realBlocks[i] = builtBlocks.asList().get(i);
    }

    return (BlockEntityType<T>) BlockEntityType.Builder.create(factory, realBlocks).build(null);
  }
}
