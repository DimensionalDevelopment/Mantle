package slimeknights.mantle.client.model;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.model.util.DynamicBakedWrapper;
import slimeknights.mantle.client.model.util.ModelConfigurationWrapper;
import slimeknights.mantle.client.model.util.ModelHelper;
import slimeknights.mantle.client.model.util.ModelTextureIteratable;
import slimeknights.mantle.client.model.util.SimpleBlockModel;
import slimeknights.mantle.item.RetexturedBlockItem;
import slimeknights.mantle.util.RetexturedHelper;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

/**
 * Model that dynamically retextures a list of textures based on data from {@link RetexturedHelper}.
 */
@SuppressWarnings("WeakerAccess")
public class RetexturedModel implements BakedModel, UnbakedModel {
  private final SimpleBlockModel model;
  private final Set<String> retextured;
  private JsonUnbakedModel owner;

  protected RetexturedModel(SimpleBlockModel model, Set<String> retextured) {
    this.model = model;
    this.retextured = retextured;
  }

  /**
   * Gets a list of all names to retexture based on the block model texture references
   * @param owner        Model config instance
   * @param model        Model fallback
   * @param originalSet  Original list of names to retexture
   * @return  Set of textures including parent textures
   */
  public static Set<String> getAllRetextured(JsonUnbakedModel owner, SimpleBlockModel model, Set<String> originalSet) {
    Set<String> retextured = Sets.newHashSet(originalSet);
    for (Map<String,Either<SpriteIdentifier, String>> textures : ModelTextureIteratable.of(owner, model)) {
      textures.forEach((name, either) ->
        either.ifRight(parent -> {
          if (retextured.contains(parent)) {
            retextured.add(name);
          }
        })
      );
    }
    return ImmutableSet.copyOf(retextured);
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
    return null;
  }

  @Override
  public ModelOverrideList getOverrides() {
    return null;
  }

  @Override
  public Collection<Identifier> getModelDependencies() {
    return null;
  }

  @Override
  public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
    return SimpleBlockModel.getTextures(owner, unbakedModelGetter, unresolvedTextureReferences);
  }

  @Nullable
  @Override
  public net.minecraft.client.render.model.BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
    // bake the model and return
    net.minecraft.client.render.model.BakedModel baked = model.bakeModel(owner, rotationContainer, getOverrides(), textureGetter, modelId);
    return new BakedModel(baked, owner, model, rotationContainer, getAllRetextured(owner, this.model, retextured));
  }


  /** Registered model loader instance registered */
  public static class Loader extends JsonModelResourceProvider {
    public static final Loader INSTANCE = new Loader();

    public Loader() {
      super(Mantle.getResource("retextured"));
    }

    /**
     * Gets the list of retextured textures from the model
     * @param json  Model json
     * @return  List of textures
     */
    public static Set<String> getRetextured(JsonObject json) {
      if (json.has("retextured")) {
        // if an array, set from each texture in array
        JsonElement retextured = json.get("retextured");
        if (retextured.isJsonArray()) {
          JsonArray array = retextured.getAsJsonArray();
          if (array.size() == 0) {
            throw new JsonSyntaxException("Must have at least one texture in retextured");
          }
          ImmutableSet.Builder<String> builder = ImmutableSet.builder();
          for (int i = 0; i < array.size(); i++) {
            builder.add(JsonHelper.asString(array.get(i), "retextured[" + i + "]"));
          }
          return builder.build();
        }
        // if string, single texture
        if (retextured.isJsonPrimitive()) {
          return ImmutableSet.of(retextured.getAsString());
        }
      }
      // if neither or missing, error
      throw new JsonSyntaxException("Missing retextured, expected to find a String or a JsonArray");
    }

    @Override
    public UnbakedModel loadJsonModelResource(Identifier resourceId, JsonObject jsonObject, ModelProviderContext context) {
      // get base model
      SimpleBlockModel model = SimpleBlockModel.deserialize(getContext(), jsonObject, HBMABFIB.getModelSafe(resourceId));

      // get list of textures to retexture
      Set<String> retextured = getRetextured(jsonObject);

      // return retextured model
      return new RetexturedModel(model, retextured);
    }
  }

  /** Baked variant of the model, used to swap out quads based on the texture */
  public static class BakedModel extends DynamicBakedWrapper<net.minecraft.client.render.model.BakedModel> {
    /** Cache of texture name to baked model */
    private final Map<Identifier,net.minecraft.client.render.model.BakedModel> cache = new HashMap<>();
    /* Properties for rebaking */
    private final JsonUnbakedModel owner;
    private final SimpleBlockModel model;
    private final ModelBakeSettings transform;
    /** List of texture names that are retextured */
    private final Set<String> retextured;

    public BakedModel(net.minecraft.client.render.model.BakedModel baked, JsonUnbakedModel owner, SimpleBlockModel model, ModelBakeSettings transform, Set<String> retextured) {
      super(baked);
      this.model = model;
      this.owner = owner;
      this.transform = transform;
      this.retextured = retextured;
    }

    /**
     * Gets the model with the given texture applied
     * @param name  Texture location
     * @return  Retextured model
     */
    private net.minecraft.client.render.model.BakedModel getRetexturedModel(Identifier name) {
      return model.bakeDynamic(new RetexturedConfiguration(owner, retextured, name), transform);
    }

    /**
     * Gets a cached retextured model, computing it if missing from the cache
     * @param block  Block determining the texture
     * @return  Retextured model
     */
    private net.minecraft.client.render.model.BakedModel getCachedModel(Block block) {
      return cache.computeIfAbsent(ModelHelper.getParticleTexture(block), this::getRetexturedModel);
    }

    @Override
    public Sprite getSprite() {
      // if particle is retextured, fetch particle from the cached model
      if (retextured.contains("particle")) {
//        Block block = data.getData(RetexturedHelper.BLOCK_PROPERTY);
//        if (block != null) {
//          return getCachedModel(block).getSprite();
//        }
      }
      return null;
//      return originalModel.getParticleTexture();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, Random random) {
      return Collections.emptyList();
/*      Block block = data.getData(RetexturedHelper.BLOCK_PROPERTY);
      if (block == null) {
        return originalModel.getQuads(state, direction, random);
      }
      return getCachedModel(block).getQuads(state, direction, random);*/
    }

    @Override
    public ModelOverrideList getOverrides() {
      return ModelOverrideList.EMPTY;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, JsonUnbakedModel extraData) {
      return Collections.emptyList();
    }
  }

  /**
   * Model configuration wrapper to retexture the block
   */
  public static class RetexturedConfiguration extends ModelConfigurationWrapper {

    /**
     * List of textures to retexture
     */
    private final Set<String> retextured;
    /**
     * Replacement texture
     */
    private final SpriteIdentifier texture;

    /**
     * Creates a new configuration wrapper
     *
     * @param base       Original model configuration
     * @param retextured Set of textures that should be retextured
     * @param texture    New texture to replace those in the set
     */
    public RetexturedConfiguration(JsonUnbakedModel base, Set<String> retextured, Identifier texture) {
      super(base);
      this.retextured = retextured;
      this.texture = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, texture);
    }

/*    @Override
    public boolean isTexturePresent(String name) {
      if (retextured.contains(name)) {
        return !MissingSprite.getMissingSpriteId().equals(texture.getTextureId());
      }
      return super.isTexturePresent(name);
    }

    @Override
    public SpriteIdentifier resolveTexture(String name) {
      if (retextured.contains(name)) {
        return texture;
      }
      return super.resolveTexture(name);
    }
  }*/

    /**
     * Override list to swap the texture in from NBT
     */
    private static class RetexturedOverride extends ModelOverrideList {

      private static final RetexturedOverride INSTANCE = new RetexturedOverride();

      private RetexturedOverride() {
        super();
      }

      public RetexturedOverride(ModelLoader modelLoader, JsonUnbakedModel unbakedModel, Function<Identifier, UnbakedModel> unbakedModelGetter, List<ModelOverride> overrides) {
        super(modelLoader, unbakedModel, unbakedModelGetter, overrides);
      }

      @Nullable
      @Override
      public net.minecraft.client.render.model.BakedModel apply(net.minecraft.client.render.model.BakedModel originalModel, ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
        if (stack.isEmpty() || !stack.hasTag()) {
          return originalModel;
        }

        // get the block first, ensuring its valid
        Block block = RetexturedBlockItem.getTexture(stack);
        if (block == Blocks.AIR) {
          return originalModel;
        }

        // if valid, use the block
        return ((BakedModel) originalModel).getCachedModel(block);
      }
    }
  }
}
