package slimeknights.mantle.registration.object;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;

public abstract class MantleFluid extends AbstractMantleFluid{

  public Flowing flowing;
  public Still still;

  private final Item bucketItem;
  private final BlockState blockState;

  public MantleFluid(Item bucketItem, BlockState blockState) {
    this.bucketItem = bucketItem;
    this.blockState = blockState;
  }

  @Override
  public Flowing getFlowing() {
    return flowing;
  }

  @Override
  public Still getStill() {
    return still;
  }

  @Override
  public Item getBucketItem() {
    return bucketItem;
  }

  @Override
  protected BlockState toBlockState(FluidState state) {
    return blockState;
  }

  public static class Flowing extends MantleFluid {

    public Still still;

    public Flowing(Item bucketItem, BlockState blockState) {
      super(bucketItem, blockState);
    }

    public void setStill(Still still) {
      this.still = still;
    }

    @Override
    public boolean isStill(FluidState state) {
      return false;
    }

    @Override
    public int getLevel(FluidState state) {
      return state.get(LEVEL);
    }
  }

  public static class Still extends MantleFluid{

    public Flowing flowing;

    public Still(Item bucketItem, BlockState blockState) {
      super(bucketItem, blockState);
    }

    public void setFlowing(Flowing flowing) {
      this.flowing = flowing;
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
      return null;
    }

    @Override
    public boolean isStill(FluidState state) {
      return true;
    }

    @Override
    public int getLevel(FluidState state) {
      return state.get(LEVEL);
    }
  }
}
