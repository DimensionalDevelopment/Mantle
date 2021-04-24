package slimeknights.mantle.client.model;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.Mantle;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

/**
 * Loads the first model from a list of models that has a loaded mod ID, ideal for optional CTM model support
 */
public class FallbackModelLoader extends JsonModelResourceProvider {

  /**
   * Loader instance
   */
  public static final FallbackModelLoader INSTANCE = new FallbackModelLoader();

  public FallbackModelLoader() {
    super(Mantle.getResource("fallback"));
  }

  @Override
  public UnbakedModel loadJsonModelResource(Identifier resourceId, JsonObject jsonObject, ModelProviderContext context) {
    return null;
  }

  /**
   * Wrapper around a single block model, redirects all standard calls to vanilla logic
   * Final baked model will still be the original instance, which is what is important
   */
  static class BlockModelWrapper implements UnbakedModel {

    private final JsonUnbakedModel model;

    public BlockModelWrapper(JsonUnbakedModel model) {
      this.model = model;
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
      return Collections.emptyList();
    }

    @Override
    public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier, UnbakedModel> unbakedModelGetter, Set<Pair<String, String>> unresolvedTextureReferences) {
      return model.getTextureDependencies(unbakedModelGetter, unresolvedTextureReferences);
    }

    @Nullable
    @Override
    public BakedModel bake(ModelLoader loader, Function<SpriteIdentifier, Sprite> textureGetter, ModelBakeSettings rotationContainer, Identifier modelId) {
      return model.bake(loader, model, textureGetter, rotationContainer, modelId, true);
    }
  }
}
