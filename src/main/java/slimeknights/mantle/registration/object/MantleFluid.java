package slimeknights.mantle.registration.object;

import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;

public abstract class MantleFluid extends AbstractMantleFluid{

  public Flowing flowing;
  public Still still;

  private BlockState blockState;
  private Item bucketItem;

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

  public void setBlockState(BlockState blockState) {
    this.blockState = blockState;
  }

  public void setBucketItem(Item bucketItem) {
    this.bucketItem = bucketItem;
  }

  @Override
  protected BlockState toBlockState(FluidState state) {
    return blockState.with(Properties.LEVEL_15, method_15741(state));
  }

  public static class Flowing extends MantleFluid {

    public Flowing(Item bucketItem, BlockState blockState) {
      super(bucketItem, blockState);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Fluid, FluidState> builder) {
      super.appendProperties(builder);
      builder.add(LEVEL);
    }

    @Override
    public Flowing getFlowing() {
      return this;
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

    public Still(Item bucketItem, BlockState blockState) {
      super(bucketItem, blockState);
    }

    @Override
    public Still getStill() {
      return this;
    }

    public void setFlowing(Flowing flowing) {
      this.flowing = flowing;
    }

    @Override
    public boolean isStill(FluidState state) {
      return true;
    }

    @Override
    public int getLevel(FluidState state) {
      return 8;
    }
  }
}
