package slimeknights.mantle.client.model.util;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

/**
 * Cross between {@link BakedModel} and {@link net.minecraftforge.client.model.data.IDynamicBakedModel}.
 * Used to create a baked model wrapper that has a dynamic {@link #getQuads(BlockState, Direction, Random, JsonUnbakedModel)} (BlockState, Direction, Random, IModelData)} without worrying about overriding the deprecated variant.
 * @param <T>  Baked model parent
 */
@SuppressWarnings("WeakerAccess")
public abstract class DynamicBakedWrapper<T extends BakedModel> implements BakedModel {

  private final T owner;

  protected DynamicBakedWrapper(T originalModel) {
    this.owner = originalModel;
  }

  /**
   * @deprecated use {@link #getQuads(BlockState, Direction, Random, net.minecraft.client.render.model.json.JsonUnbakedModel)}
   */
  @Override
  @Deprecated
  public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
    return this.getQuads(state, side, rand, (JsonUnbakedModel) owner);
  }

  @Override
  public boolean useAmbientOcclusion() {
    return false;
  }

  @Override
  public boolean hasDepth() {
    return true;
  }

  @Override
  public boolean isSideLit() {
    return false;
  }

  @Override
  public boolean isBuiltin() {
    return false;
  }

  @Override
  public ModelTransformation getTransformation() {
    return ModelTransformation.NONE;
  }

  @Override
  public ModelOverrideList getOverrides() {
    return ModelOverrideList.EMPTY;
  }

  public abstract List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, JsonUnbakedModel extraData);
}
