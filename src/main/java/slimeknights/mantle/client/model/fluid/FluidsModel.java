package slimeknights.mantle.client.model.fluid;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.model.HBMABFIB;
import slimeknights.mantle.client.model.JsonModelResourceProvider;
import slimeknights.mantle.client.model.util.SimpleBlockModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

/**
 * This model contains a list of fluid cuboids for the sake of rendering multiple fluid regions in world. It is used by the faucet at this time
 */
public class FluidsModel implements UnbakedModel {
  private final SimpleBlockModel model;
  private final List<FluidCuboid> fluids;
  public final JsonUnbakedModel owner;

  public FluidsModel(SimpleBlockModel model, List<FluidCuboid> fluids, JsonUnbakedModel owner) {
    this.model = model;
    this.fluids = fluids;
    this.owner = owner;
  }

  @Override
  public Collection<Identifier> getModelDependencies() {
    return null;
  }

  @Override
  public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
    return model.getTextures(owner, unbakedModelGetter, unresolvedTextureReferences);
  }

  @Nullable
  @Override
  public net.minecraft.client.render.model.BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
    net.minecraft.client.render.model.BakedModel baked = model.bakeModel(owner, rotationContainer, ModelOverrideList.EMPTY, textureGetter, modelId);
    return new BakedModel(baked, fluids, HBMABFIB.getModelSafe(modelId));
  }

  /** Baked model, mostly a data wrapper around a normal model */
  @SuppressWarnings("WeakerAccess")
  public static class BakedModel implements net.minecraft.client.render.model.BakedModel {
    private final List<FluidCuboid> fluids;
    private final JsonUnbakedModel owner;

    public BakedModel(net.minecraft.client.render.model.BakedModel originalModel, List<FluidCuboid> fluids, JsonUnbakedModel owner) {
      this.fluids = fluids;
      this.owner = owner;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
      return null;
    }

    @Override
    public boolean useAmbientOcclusion() {
      return false;
    }

    @Override
    public boolean hasDepth() {
      return false;
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
    public Sprite getSprite() {
      return null;
    }

    @Override
    public ModelTransformation getTransformation() {
      return ModelTransformation.NONE;
    }

    @Override
    public ModelOverrideList getOverrides() {
      return ModelOverrideList.EMPTY;
    }

    public List<FluidCuboid> getFluids() {
      return this.fluids;
    }
  }

  /** Loader for this model */
  public static class Loader extends JsonModelResourceProvider {
    /**
     * Shared loader instance
     */
    public static final Loader INSTANCE = new Loader();

    public Loader() {
      super(Mantle.getResource("fluids"));
    }

    @Override
    public UnbakedModel loadJsonModelResource(Identifier resourceId, JsonObject jsonObject, ModelProviderContext context) {
      SimpleBlockModel model = SimpleBlockModel.deserialize(getContext(), jsonObject, HBMABFIB.getModelSafe(resourceId));
      List<FluidCuboid> fluid = FluidCuboid.listFromJson(jsonObject, "fluids");
      return new FluidsModel(model, fluid, HBMABFIB.getModelSafe(resourceId));
    }
  }
}
