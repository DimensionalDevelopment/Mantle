package slimeknights.mantle.registration.object;

import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Object containing a block with slab and stairs variants
 */
@SuppressWarnings("WeakerAccess")
public class BuildingBlockObject extends ItemObject<Block> {
  private final SlabBlock slab;
  private final StairsBlock stairs;

  /**
   * Creates a new building block object from three blocks
   * @param block   Base block
   * @param slab    Slab block, should be an instance of SlabBlock
   * @param stairs  Stairs block, should be an instance of StairsBlock
   */
  public BuildingBlockObject(Block block, Block slab, Block stairs) {
    super(block);
    this.slab = (SlabBlock) slab;
    this.stairs = (StairsBlock) stairs;
  }

  /**
   * Creates a new object from a ItemObject and some suppliers.
   * @param block   Base block
   * @param slab    Slab block
   * @param stairs  Stairs block
   */
  public BuildingBlockObject(ItemObject<? extends Block> block, ItemObject<Block> slab, ItemObject<Block> stairs) {
    super(block);
    this.slab = (SlabBlock) slab.get();
    this.stairs = (StairsBlock) stairs.get();
  }

  /**
   * Creates a new object from another building block object, intended to be used in subclasses to copy properties
   * @param object   Object to copy
   */
  protected BuildingBlockObject(BuildingBlockObject object) {
    super(object);
    this.slab = object.slab;
    this.stairs = object.stairs;
  }

  /** Gets the slab for this block */
  public SlabBlock getSlab() {
    return Objects.requireNonNull(slab, "Building Block Object missing slab");
  }

  /** Gets the stairs for this block */
  public StairsBlock getStairs() {
    return Objects.requireNonNull(stairs, "Building Block Object missing stairs");
  }

  /**
   * Gets an array of the blocks in this object
   * @return  Array of the blocks in this object
   */
  public List<Block> values() {
    return Arrays.asList(get(), getSlab(), getStairs());
  }
}
