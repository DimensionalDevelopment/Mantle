package slimeknights.mantle.client.model.util;

import net.minecraft.client.render.model.json.JsonUnbakedModel;

/**
 * Wrapper around a {@link JsonUnbakedModel} instance to allow easier extending, mostly for dynamic textures
 */
@SuppressWarnings("WeakerAccess")
public class ModelConfigurationWrapper extends JsonUnbakedModel{
  private final JsonUnbakedModel base;

  /**
   * Creates a new configuration wrapper
   * @param base  Base model configuration
   */
  public ModelConfigurationWrapper(JsonUnbakedModel base) {
    super(base.parentId, base.getElements(), base.textureMap, base.useAmbientOcclusion(), base.getGuiLight(), base.getTransformations(), base.getOverrides());
    this.base = base;
  }

/*  @Nullable
  public UnbakedModel getOwnerModel() {
    return base.getRootModel();
  }

  public String getModelName() {
    return base.id;
  }

  public boolean isTexturePresent(String name) {
    return base.isTexturePresent(name);
  }

  public SpriteIdentifier resolveTexture(String name) {
    return base.resolveTexture(name);
  }

  public boolean isShadedInGui() {
    return base.isShadedInGui();
  }

  public boolean isSideLit() {
    return base.isSideLit();
  }

  public boolean useSmoothLighting() {
    return base.useSmoothLighting();
  }

  public ModelTransformation getCameraTransforms() {
    return base.getCameraTransforms();
  }

  public ModelBakeSettings getCombinedTransform() {
    return base.getCombinedTransform();
  }

  public boolean getPartVisibility(IModelGeometryPart part, boolean fallback) {
    return base.getPartVisibility(part, fallback);
  }*/


}
