package slimeknights.mantle.client.model.util;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.SpriteIdentifier;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.model.HBMABFIB;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

public class ModelTextureIteratable implements Iterable<Map<String,Either<SpriteIdentifier, String>>> {
  /** Initial map for iteration */
  @Nullable
  private final Map<String,Either<SpriteIdentifier, String>> startMap;
  /** Initial model for iteration */
  @Nullable
  private final JsonUnbakedModel startModel;

  /**
   * Creates an iterable over the given model
   * @param model  Model
   */
  public ModelTextureIteratable(JsonUnbakedModel model) {
    this(null, model);
  }

  public ModelTextureIteratable(@Nullable Map<String, Either<SpriteIdentifier, String>> startMap, @Nullable JsonUnbakedModel startModel) {
    this.startMap = startMap;
    this.startModel = startModel;
  }

  /**
   *
   * @param owner     Model configuration owner
   * @param fallback  Fallback in case the owner does not contain a block model
   * @return  Iteratable over block model texture maps
   */
  public static ModelTextureIteratable of(JsonUnbakedModel owner, SimpleBlockModel fallback) {
    UnbakedModel unbaked = HBMABFIB.getModelSafe(owner.parentId);
    if (unbaked instanceof JsonUnbakedModel) {
      return new ModelTextureIteratable(null, (JsonUnbakedModel)unbaked);
    }
    return new ModelTextureIteratable(fallback.getTextures(), fallback.getParent());
  }

  @Override
  public MapIterator iterator() {
    return new MapIterator(startMap, startModel);
  }

  private static class MapIterator implements Iterator<Map<String,Either<SpriteIdentifier, String>>> {
    /** Initial map for iteration */
    @Nullable
    private Map<String,Either<SpriteIdentifier, String>> initial;
    /** current model in the iterator */
    @Nullable
    private JsonUnbakedModel model;

    public MapIterator(@Nullable Map<String, Either<SpriteIdentifier, String>> initial, @Nullable JsonUnbakedModel model) {
      this.initial = initial;
      this.model = model;
    }

    @Override
    public boolean hasNext() {
      return initial != null || model != null;
    }

    @Override
    public Map<String,Either<SpriteIdentifier,String>> next() {
      Map<String,Either<SpriteIdentifier, String>> map;
      if (initial != null) {
        map = initial;
        initial = null;
      } else if (model != null) {
        map = model.textureMap;
        model = model.parent;
      } else {
        throw new NoSuchElementException();
      }
      return map;
    }
  }
}
