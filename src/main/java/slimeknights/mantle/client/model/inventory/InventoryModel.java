package slimeknights.mantle.client.model.inventory;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import com.mojang.datafixers.util.Pair;

import net.minecraftforge.client.model.IModelLoader;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import slimeknights.mantle.client.model.IModelConfiguration;
import slimeknights.mantle.client.model.util.SimpleBlockModel;

/**
 * This model contains a list of multiple items to display in a TESR
 */
public class InventoryModel implements UnbakedModel {
  protected final SimpleBlockModel model;
  protected final List<ModelItem> items;

  public InventoryModel(SimpleBlockModel model, List<ModelItem> items) {
    this.model = model;
    this.items = items;
  }

  @Override
  public Collection<SpriteIdentifier> getTextureDependencies(Function<Identifier,UnbakedModel> modelGetter, Set<Pair<String,String>> missingTextureErrors) {
    return model.getTextures(modelGetter, missingTextureErrors);
  }

  @Override
  public Collection<SpriteIdentifier> getTextures(IModelConfiguration owner,

  @Override
  public net.minecraft.client.render.model.BakedModel bake(IModelConfiguration owner, ModelLoader bakery, Function<SpriteIdentifier,Sprite> spriteGetter, ModelBakeSettings transform, ModelOverrideList overrides, Identifier location) {
    net.minecraft.client.render.model.BakedModel baked = model.bakeModel(owner, transform, overrides, spriteGetter, location);
    return new BakedModel(baked, items);
  }

  /** Baked model, mostly a data wrapper around a normal model */
  @SuppressWarnings("WeakerAccess")
  public static class BakedModel extends ForwardingBakedModel {
    private final List<ModelItem> items;
    public BakedModel(net.minecraft.client.render.model.BakedModel originalModel, List<ModelItem> items) {
      super.wrapped = originalModel;
      this.items = items;
    }

    public List<ModelItem> getItems() {
      return this.items;
    }
  }

  /** Loader for this model */
  public static class Loader implements IModelLoader<InventoryModel> {
    /**
     * Shared loader instance
     */
    public static final Loader INSTANCE = new Loader();

    @Override
    public void apply(ResourceManager resourceManager) {}

    @Override
    public InventoryModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
      SimpleBlockModel model = SimpleBlockModel.deserialize(deserializationContext, modelContents);
      List<ModelItem> items = ModelItem.listFromJson(modelContents, "items");
      return new InventoryModel(model, items);
    }
  }
}
