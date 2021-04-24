package slimeknights.mantle.client.model.inventory;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

/**
 * This model contains a list of multiple items to display in a TESR
 */
public class InventoryModel implements UnbakedModel {
  protected final SimpleBlockModel model;
  protected final List<ModelItem> items;
  protected final JsonUnbakedModel owner;

  public InventoryModel(SimpleBlockModel model, List<ModelItem> items, JsonUnbakedModel owner) {
    this.model = model;
    this.items = items;
    this.owner = owner;
  }

  @Override
  public Collection<Identifier> getModelDependencies() {
    return Collections.emptyList();
  }

  @Override
  public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
    return model.getTextures(owner, unbakedModelGetter, unresolvedTextureReferences);
  }

  @Nullable
  @Override
  public net.minecraft.client.render.model.BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
    net.minecraft.client.render.model.BakedModel baked = model.bakeModel(owner, rotationContainer, ModelOverrideList.EMPTY, textureGetter, modelId);
    return new MantleBakedModel(baked, items);
  }

  /** Baked model, mostly a data wrapper around a normal model */
  @SuppressWarnings("WeakerAccess")
  public static class MantleBakedModel implements BakedModel {
    private final List<ModelItem> items;
    private final BakedModel owner;

    public MantleBakedModel(net.minecraft.client.render.model.BakedModel owner, List<ModelItem> items) {
      this.owner = owner;
      this.items = items;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, Random random) {
      return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
      return true;
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

    public List<ModelItem> getItems() {
      return this.items;
    }
  }

  /** Loader for this model */
  public static class Loader extends JsonModelResourceProvider {
    /**
     * Shared loader instance
     */
    public static final Loader INSTANCE = new Loader();

    public Loader() {
      super(Mantle.getResource("inventory"));
    }

    @Override
    public UnbakedModel loadJsonModelResource(Identifier resourceId, JsonObject jsonObject, ModelProviderContext context) {
      SimpleBlockModel model = SimpleBlockModel.deserialize(getContext(), jsonObject, HBMABFIB.getModelSafe(resourceId));
      List<ModelItem> items = ModelItem.listFromJson(jsonObject, "items");
      return new InventoryModel(model, items, HBMABFIB.getModelSafe(resourceId));
    }
  }
}
