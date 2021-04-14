package slimeknights.mantle.registration.deferred;

import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import slimeknights.mantle.registration.object.*;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Deferred register to handle registering blocks with possible item forms
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class BlockDeferredRegister extends DeferredRegisterWrapper {
  protected final DefaultedRegistry<Item> itemRegister;
  public BlockDeferredRegister(String modID) {
    super(modID);
    this.itemRegister = Registry.ITEM;
  }

  /* Blocks with no items */

  /**
   * Registers a block with the block registry
   * @param name   Block ID
   * @param block  Block supplier
   * @param <B>    Block class
   * @return  Block registry object
   */
  public <B extends Block> Block registerNoItem(String name, Supplier<? extends B> block) {
    return Registry.register(Registry.BLOCK, name, block.get());
  }

  /**
   * Registers a block with the block registry
   * @param name   Block ID
   * @param props  Block properties
   * @return  Block
   */
  public Block registerNoItem(String name, AbstractBlock.Settings props) {
    return registerNoItem(name, () -> new Block(props));
  }


  /* Block item pairs */

  /**
   * Registers a block with the block registry, using the function for the BlockItem
   * @param name   Block ID
   * @param block  Block supplier
   * @param item   Function to create a BlockItem from a Block
   * @return  Block item registry object pair
   */
  public ItemObject<Block> register(String name, Supplier<Block> block, final Function<Block, ? extends BlockItem> item) {
    Block blockObj = registerNoItem(name, block);
    Registry.register(itemRegister, new Identifier(modID, name), item.apply(blockObj));
    return new ItemObject<>(blockObj);
  }

  /**
   * Registers a block with the block registry, using the function for the BlockItem
   * @param name   Block ID
   * @param block  Block
   * @param item   Function to create a BlockItem from a Block
   * @return  Block item registry object pair
   */
  public Block register(String name, Block block, final Function<Block, ? extends BlockItem> item) {
    return register(name, () -> block, item).get();
  }

  /**
   * Registers a block with the block registry, using the function for the BlockItem
   * @param name        Block ID
   * @param blockProps  Block supplier
   * @param item        Function to create a BlockItem from a Block
   * @return  Block item registry object pair
   */
  public ItemObject<Block> register(String name, AbstractBlock.Settings blockProps, Function<Block, ? extends BlockItem> item) {
    return register(name, () -> new Block(blockProps), item);
  }


  /* Specialty */

  /**
   * Registers a building block with slabs and stairs, using a custom block
   * @param name   Block name
   * @param block  Block supplier
   * @param item   Item block, used for all variants
   * @return  Building block object
   */
  public BuildingBlockObject registerBuilding(String name, Supplier<Block> block, Function<Block, BlockItem> item) {
    ItemObject<Block> blockObj = register(name, block, item);
    return new BuildingBlockObject(
        blockObj,
        this.register(name + "_slab", () -> new SlabBlock(AbstractBlock.Settings.copy(blockObj.get())), item),
        this.register(name + "_stairs", () -> new StairsBlock(blockObj.get().getDefaultState(), AbstractBlock.Settings.copy(blockObj.get())), item));
  }

  /**
   * Registers a block with slab, and stairs
   * @param name      Name of the block
   * @param props     Block properties
   * @param item      Function to get an item from the block
   * @return  BuildingBlockObject class that returns different block types
   */
  public BuildingBlockObject registerBuilding(String name, Block.Settings props, Function<Block, BlockItem> item) {
    ItemObject<Block> blockObj = register(name, props, item);
    return new BuildingBlockObject(blockObj,
      register(name + "_slab", () -> new SlabBlock(props), item),
      register(name + "_stairs", () -> new StairsBlock(blockObj.get().getDefaultState(), props), item)
    );
  }

  /**
   * Registers a building block with slabs, stairs and wall, using a custom block
   * @param name   Block name
   * @param block  Block supplier
   * @param item   Item block, used for all variants
   * @return  Building block object
   */
  public WallBuildingBlockObject registerWallBuilding(String name, Supplier<Block> block, Function<Block, BlockItem> item) {
    BuildingBlockObject obj = this.registerBuilding(name, block, item);
    return new WallBuildingBlockObject(obj, (WallBlock) this.register(name + "_wall", new WallBlock(AbstractBlock.Settings.copy(obj.get())), item));
  }

  /**
   * Registers a block with slab, stairs, and wall
   * @param name      Name of the block
   * @param props     Block properties
   * @param item      Function to get an item from the block
   * @return  StoneBuildingBlockObject class that returns different block types
   */
  public WallBuildingBlockObject registerWallBuilding(String name, AbstractBlock.Settings props, Function<Block, BlockItem> item) {
    return new WallBuildingBlockObject(
      registerBuilding(name, props, item),
            (WallBlock) register(name + "_wall", new WallBlock(props), item)
    );
  }

  /**
   * Registers a building block with slabs, stairs and wall, using a custom block
   * @param name   Block name
   * @param block  Block supplier
   * @param item   Item block, used for all variants
   * @return  Building block object
   */
  public FenceBuildingBlockObject registerFenceBuilding(String name, Supplier<Block> block, Function<Block, BlockItem> item) {
    BuildingBlockObject obj = this.registerBuilding(name, block, item);
    return new FenceBuildingBlockObject(obj, (FenceBlock) this.register(name + "_fence", () -> new FenceBlock(AbstractBlock.Settings.copy(obj.get())), item).get());
  }

  /**
   * Registers a block with slab, stairs, and fence
   * @param name      Name of the block
   * @param props     Block properties
   * @param item      Function to get an item from the block
   * @return  WoodBuildingBlockObject class that returns different block types
   */
  public FenceBuildingBlockObject registerFenceBuilding(String name, AbstractBlock.Settings props, Function<Block, BlockItem> item) {
    return new FenceBuildingBlockObject(
      registerBuilding(name, props, item),
            (FenceBlock) register(name + "_fence", () -> new FenceBlock(props), item).get()
    );
  }

  /**
   * Registers an item with multiple variants, prefixing the name with the value name
   * @param values    Enum values to use for this block
   * @param name      Name of the block
   * @param mapper    Function to get a block for the given enum value
   * @param item      Function to get an item from the block
   * @return  EnumObject mapping between different block types
   */
  public <T extends Enum<T> & StringIdentifiable, B extends Block> EnumObject<T,Block> registerEnum(
          T[] values, String name, Function<T,Block> mapper, Function<Block, ? extends BlockItem> item) {
    return registerEnum(values, name, (fullName, value) -> register(fullName, () -> mapper.apply(value), item));
  }

  /**
   * Registers a block with multiple variants, suffixing the name with the value name
   * @param name      Name of the block
   * @param values    Enum values to use for this block
   * @param mapper    Function to get a block for the given enum value
   * @param item      Function to get an item from the block
   * @return  EnumObject mapping between different block types
   */
  public <T extends Enum<T> & StringIdentifiable, B extends Block> EnumObject<T,B> registerEnum(
          String name, T[] values, Function<T,? extends Block> mapper, Function<B, ? extends BlockItem> item) {
    return (EnumObject<T, B>) registerEnum(name, values, (fullName, value) -> register(fullName, () -> mapper.apply(value), (Function<Block, ? extends BlockItem>) item));
  }
}
