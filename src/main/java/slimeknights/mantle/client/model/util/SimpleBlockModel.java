package slimeknights.mantle.client.model.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BasicBakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.model.HBMABFIB;
import slimeknights.mantle.client.model.JsonModelResourceProvider;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Simplier version of {@link JsonUnbakedModel} for use in an {@link slimeknights.mantle.client.model.JsonModelResourceProvider}, as the owner handles most block model properties
 */
@SuppressWarnings("WeakerAccess")
public class SimpleBlockModel implements BakedModel, UnbakedModel {
  /** Model loader for vanilla block model, mainly intended for use in fallback registration */
  public static final Loader LOADER = new Loader();
  /** Location used for baking dynamic models, name does not matter so just using a constant */
  private static final Identifier BAKE_LOCATION = Mantle.getResource("dynamic_model_baking");

  private final JsonUnbakedModel owner;

  /** Parent model location, used to fetch parts and for textures if the owner is not a block model */
  @Nullable
  private Identifier parentLocation;
  /** Model parts for baked model, if empty uses parent parts */
  private final List<ModelElement> parts;
  /** Fallback textures in case the owner does not contain a block model */
  private final Map<String,Either<SpriteIdentifier, String>> textures;
  private JsonUnbakedModel parent;

  /**
   * Creates a new simple block model
   * @param parentLocation  Location of the parent model, if unset has no parent
   * @param textures        List of textures for iteration, in case the owner is not BlockModel
   * @param parts           List of parts in the model
   * @param owner           Json Model
   */
  public SimpleBlockModel(@Nullable Identifier parentLocation, Map<String, Either<SpriteIdentifier, String>> textures, List<ModelElement> parts, JsonUnbakedModel owner) {
    this.parts = parts;
    this.textures = textures;
    this.parentLocation = parentLocation;
    this.owner = owner;
  }


  /* Properties */

  /**
   * Gets the elements in this simple block model
   * @return  Elements in the model
   */
  @SuppressWarnings("deprecation")
  public List<ModelElement> getElements() {
    return parts.isEmpty() && parent != null ? parent.getElements() : parts;
  }

  /* Textures */

  /**
   * Fetches parent models for this model and its parents
   * @param modelGetter  Model getter function
   */
  public void fetchParent(JsonUnbakedModel owner, Function<Identifier,UnbakedModel> modelGetter) {
    // no work if no parent or the parent is fetched already
    if (parent != null || parentLocation == null) {
      return;
    }

    // iterate through model parents
    Set<UnbakedModel> chain = Sets.newLinkedHashSet();

    // load the first model directly
    parent = getParent(modelGetter, chain, parentLocation, owner.id);
    // null means no model, so set missing
    if (parent == null) {
      parent = getMissing(modelGetter);
      parentLocation = net.minecraft.client.render.model.ModelLoader.MISSING;
    }

    // loop through each parent, adding in parents
    for (JsonUnbakedModel link = parent; link.parentId != null && link.parent == null; link = link.parent) {
      chain.add(link);

      // fetch model parent
      link.parent = getParent(modelGetter, chain, link.parentId, link.id);

      // null means no model, so set missing
      if (link.parent == null) {
        link.parent = getMissing(modelGetter);
        link.parentId = net.minecraft.client.render.model.ModelLoader.MISSING;
      }
    }
  }

  /**
   * Gets the parent for a model
   * @param modelGetter  Model getter function
   * @param chain        Chain of models that are in progress
   * @param location     Location to fetch
   * @param name         Name of the model being fetched
   * @return  Block model instance, null if there was an error
   */
  @Nullable
  private static JsonUnbakedModel getParent(Function<Identifier,UnbakedModel> modelGetter, Set<UnbakedModel> chain, Identifier location, String name) {
    // model must exist
    UnbakedModel unbaked = modelGetter.apply(location);
    if (unbaked == null) {
      Mantle.logger.warn("No parent '{}' while loading model '{}'", location, name);
      return null;
    }
    // no loops in chain
    if (chain.contains(unbaked)) {
      Mantle.logger.warn("Found 'parent' loop while loading model '{}' in chain: {} -> {}", name, chain.stream().map(Object::toString).collect(Collectors.joining(" -> ")), location);
      return null;
    }
    // model must be block model, this is a serious error in vanilla
    if (!(unbaked instanceof JsonUnbakedModel)) {
      throw new IllegalStateException("BlockModel parent has to be a block model.");
    }
    return (JsonUnbakedModel) unbaked;
  }

  /**
   * Gets the missing model, ensuring its the right type
   * @param modelGetter  Model getter function
   * @return  Missing model as a {@link JsonUnbakedModel}
   */
  @NotNull
  private static JsonUnbakedModel getMissing(Function<Identifier,UnbakedModel> modelGetter) {
    UnbakedModel model = modelGetter.apply(net.minecraft.client.render.model.ModelLoader.MISSING);
    if (!(model instanceof JsonUnbakedModel)) {
      throw new IllegalStateException("Failed to load missing model");
    }
    return (JsonUnbakedModel) model;
  }

  /**
   * Gets the texture dependencies for a list of elements, allows calling outside a simple block model
   * @param owner                 Model configuration
   * @param elements              List of elements to check for textures
   * @param missingTextureErrors  Missing texture set
   * @return  Textures dependencies
   */
  public static Collection<SpriteIdentifier> getTextures(JsonUnbakedModel owner, List<ModelElement> elements, Set<Pair<String,String>> missingTextureErrors) {
    // always need a particle texture
    Set<SpriteIdentifier> textures = Sets.newHashSet(owner.resolveSprite("particle"));
    // iterate all elements, fetching needed textures from the material
    for(ModelElement part : elements) {
      for(ModelElementFace face : part.faces.values()) {
        SpriteIdentifier material = owner.resolveSprite(face.textureId);
        if (Objects.equals(material.getTextureId(), MissingSprite.getMissingSpriteId())) {
          missingTextureErrors.add(Pair.of(face.textureId, owner.id));
        }
        textures.add(material);
      }
    }
    return textures;
  }

  /**
   * Gets the texture and model dependencies for a block model
   * @param unbakedModelGetter           Model getter to fetch parent models
   * @param unresolvedTextureReferences  Missing texture set
   * @return  Textures dependencies
   */
  @Override
  public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
    this.fetchParent(owner, unbakedModelGetter);
    return getTextures(owner, getElements(), unresolvedTextureReferences);
  }


  /* Baking */

  /**
   * Bakes a list of block part elements into a model
   * @param owner         Model configuration
   * @param elements      Model elements
   * @param transform     Model transform
   * @param overrides     Model overrides
   * @param spriteGetter  Sprite getter instance
   * @param location      Model bake location
   * @return  Baked model
   */
  public static BakedModel bakeModel(JsonUnbakedModel owner, List<ModelElement> elements, ModelBakeSettings transform, ModelOverrideList overrides, Function<SpriteIdentifier,Sprite> spriteGetter, Identifier location) {
    // iterate parts, adding to the builder
    Sprite particle = spriteGetter.apply(owner.resolveSprite("particle"));
    BasicBakedModel.Builder builder = new BasicBakedModel.Builder(owner, overrides, true).setParticle(particle);
    for(ModelElement part : elements) {
      for(Direction direction : part.faces.keySet()) {
        ModelElementFace face = part.faces.get(direction);
        // ensure the name is not prefixed (it always is)
        String texture = face.textureId;
        if (texture.charAt(0) == '#') {
          texture = texture.substring(1);
        }
        Sprite sprite = spriteGetter.apply(owner.resolveSprite(texture));
        // apply cull face
        if (face.cullFace == null) {
          builder.addQuad(JsonUnbakedModel.createQuad(part, face, sprite, direction, transform, location));
        } else {
          builder.addQuad(Direction.transform(transform.getRotation().getMatrix(), face.cullFace), JsonUnbakedModel.createQuad(part, face, sprite, direction, transform, location));
        }
      }
    }
    return builder.build();
  }

  /**
   * Same as {@link #bakeModel(JsonUnbakedModel, List, ModelBakeSettings, ModelOverrideList, Function, Identifier)}, but passes in sensible defaults for values unneeded in dynamic models
   * @param owner      Model configuration
   * @param elements   Elements to bake
   * @param transform  Model transform
   * @return Baked model
   */
  public static BakedModel bakeDynamic(JsonUnbakedModel owner, List<ModelElement> elements, ModelBakeSettings transform) {
    return bakeModel(owner, elements, transform, ModelOverrideList.EMPTY, SpriteIdentifier::getSprite, BAKE_LOCATION);
  }

  /**
   * Bakes the given block model
   * @param owner         Model configuration
   * @param transform     Transform to apply
   * @param overrides     Item overrides in baking
   * @param spriteGetter  Sprite getter instance
   * @param location      Bake location
   * @return  Baked model
   */
  public BakedModel bakeModel(JsonUnbakedModel owner, ModelBakeSettings transform, ModelOverrideList overrides, Function<SpriteIdentifier,Sprite> spriteGetter, Identifier location) {
    return bakeModel(owner, this.getElements(), transform, overrides, spriteGetter, location);
  }

  /**
   * Same as {@link #bakeModel(JsonUnbakedModel, ModelBakeSettings, ModelOverrideList, Function, Identifier)}, but passes in sensible defaults for values unneeded in dynamic models
   * @param owner         Model configuration
   * @param transform     Transform to apply
   * @return  Baked model
   */
  public BakedModel bakeDynamic(JsonUnbakedModel owner, ModelBakeSettings transform) {
    return bakeDynamic(owner, this.getElements(), transform);
  }


  /* Deserializing */

  /**
   * Deserializes a SimpleBlockModel from JSON
   * @param context  Json Context
   * @param json     Json element containing the model
   * @param owner    Json Model
   * @return  Serialized JSON
   */
  public static SimpleBlockModel deserialize(JsonDeserializationContext context, JsonObject json, JsonUnbakedModel owner) {
    // parent, null if missing
    String parentName = JsonHelper.getString(json, "parent", "");
    Identifier parent = parentName.isEmpty() ? null : new Identifier(parentName);

    // textures, empty map if missing
    Map<String, Either<SpriteIdentifier, String>> textureMap;
    if (json.has("textures")) {
      ImmutableMap.Builder<String, Either<SpriteIdentifier, String>> builder = new ImmutableMap.Builder<>();
      Identifier atlas = PlayerScreenHandler.BLOCK_ATLAS_TEXTURE;
      JsonObject textures = JsonHelper.getObject(json, "textures");
      for(Entry<String, JsonElement> entry : textures.entrySet()) {
        builder.put(entry.getKey(), JsonUnbakedModel.Deserializer.resolveReference(atlas, entry.getValue().getAsString()));
      }
      textureMap = builder.build();
    } else {
      textureMap = Collections.emptyMap();
    }

    // elements, empty list if missing
    List<ModelElement> parts;
    if (json.has("elements")) {
      parts = getModelElements(context, JsonHelper.getArray(json, "elements"), "elements");
    } else {
      parts = Collections.emptyList();
    }
    return new SimpleBlockModel(parent, textureMap, parts, owner);
  }

  /**
   * Gets a list of models from a JSON array
   * @param context  Json Context
   * @param array    Json array
   * @return  Model list
   */
  public static List<ModelElement> getModelElements(JsonDeserializationContext context, JsonElement array, String name) {
    // if just one element, array is optional
    if (array.isJsonObject()) {
      return ImmutableList.of(context.deserialize(array.getAsJsonObject(), ModelElement.class));
    }
    // if an array, get array of elements
    if (array.isJsonArray()) {
      ImmutableList.Builder<ModelElement> builder = ImmutableList.builder();
      for(JsonElement json : array.getAsJsonArray()) {
        builder.add((ModelElement)context.deserialize(json, ModelElement.class));
      }
      return builder.build();
    }

    throw new JsonSyntaxException("Missing " + name + ", expected to find a JsonArray or JsonObject");
  }

  public @Nullable Identifier getParentLocation() {
    return this.parentLocation;
  }

  public Map<String, Either<SpriteIdentifier, String>> getTextures() {
    return this.textures;
  }

  public JsonUnbakedModel getParent() {
    return this.parent;
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
    return ModelOverrideList.EMPTY;
  }

  @Override
  public Collection<Identifier> getModelDependencies() {
    return Collections.emptySet();
  }

  @Nullable
  @Override
  public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
    return bakeModel(parent, rotationContainer, getOverrides(), textureGetter, modelId);
  }

  /** Logic to implement a vanilla block model */
  private static class Loader extends JsonModelResourceProvider {

    public Loader() {
      super("");
      throw new RuntimeException("CRAB!");
    }

    @Override
    public UnbakedModel loadJsonModelResource(Identifier resourceId, JsonObject jsonObject, ModelProviderContext context) {
      return deserialize(getContext(), jsonObject, HBMABFIB.getModelSafe(resourceId));
    }
  }
}
