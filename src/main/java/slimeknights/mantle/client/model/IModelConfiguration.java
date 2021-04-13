package slimeknights.mantle.client.model;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import org.jetbrains.annotations.Nullable;

public interface IModelConfiguration {

  @Nullable
  UnbakedModel getOwnerModel();

  String getModelName();

  boolean isTexturePresent(String name);

  RenderMaterial resolveTexture(String name);

  boolean isShadedInGui();

  boolean isSideLit();

  boolean useSmoothLighting();

  ModelTransformation getCameraTransforms();

  ModelBakeSettings getCombinedTransform();

  default boolean getPartVisibility(IModelGeometryPart part, boolean fallback) {
    return fallback;
  }

  default boolean getPartVisibility(IModelGeometryPart part) {
    return getPartVisibility(part, true);
  }

}
