package slimeknights.mantle.client.model.inventory;

import com.google.gson.JsonObject;

import java.util.List;

import net.minecraft.client.util.math.Vector3f;

import slimeknights.mantle.client.model.util.ModelHelper;
import slimeknights.mantle.util.JsonHelper;

public class ModelItem {
  /** Model item for rendering no item */
  private static final ModelItem EMPTY = new ModelItem(new Vector3f(0, 0, 0), 0, 0, 0);

  /** Item center location in pixels */
  private final Vector3f center;
  /** Item size in pixels. If 0, item is skipped */
  private final float size;
  /** X axis rotation, applied first */
  private final float x;
  /** Y axis rotation, applied second */
  private final float y;

  /** Item center location in percentages, lazy loaded */
  private Vector3f centerScaled;
  /** Item size in percentages, lazy loaded */
  private Float sizeScaled;

  public ModelItem(Vector3f center, float size, float x, float y) {
    this.center = center;
    this.size = size;
    this.x = x;
    this.y = y;
  }

  /**
   * Gets the center for rendering this item, scaled for renderer
   * @return Scaled center
   */
  public Vector3f getCenterScaled() {
    if (centerScaled == null) {
      centerScaled = center.copy();
      centerScaled.scale(1f / 16f);
    }
    return centerScaled;
  }

  /**
   * Gets the size to render this item, scaled for the renderer
   * @return Size scaled
   */
  public float getSizeScaled() {
    if (sizeScaled == null) {
      sizeScaled = size / 16f;
    }
    return sizeScaled;
  }

  /**
   * Returns true if this model item is hidden, meaning no items should be rendered
   * @return  True if hidden
   */
  public boolean isHidden() {
    return size == 0;
  }

  /**
   * Gets a model item from a JSON object
   * @param json  JSON object instance
   * @return  Model item object
   */
  public static ModelItem fromJson(JsonObject json) {
    // if the size is 0, skip rendering this item
    float size = net.minecraft.util.JsonHelper.getFloat(json, "size");
    if (size == 0) {
      return ModelItem.EMPTY;
    }
    Vector3f center = ModelHelper.arrayToVector(json, "center");
    float x = ModelHelper.getRotation(json, "x");
    float y = ModelHelper.getRotation(json, "y");
    return new ModelItem(center, size, x, y);
  }

  /**
   * Gets a list of model items from JSON
   * @param parent  Parent JSON object
   * @param key     Name of the array of model item objects
   * @return  List of model items
   */
  public static List<ModelItem> listFromJson(JsonObject parent, String key) {
    return JsonHelper.parseList(parent, key, ModelItem::fromJson);
  }

  public Vector3f getCenter() {
    return this.center;
  }

  public float getSize() {
    return this.size;
  }

  public float getX() {
    return this.x;
  }

  public float getY() {
    return this.y;
  }
}
