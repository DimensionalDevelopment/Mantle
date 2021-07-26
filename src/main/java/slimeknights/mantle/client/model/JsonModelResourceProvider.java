package slimeknights.mantle.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import java.io.IOException;

import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.util.Identifier;

public abstract class JsonModelResourceProvider implements ModelResourceProvider {

  public String type;

  public JsonModelResourceProvider(String type) {
    this.type = type;
  }

  public JsonModelResourceProvider(Identifier type) {
    this.type = type.toString();
  }

  public UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context) {
    try {
      if(resourceId.getNamespace().equals("minecraft") && resourceId.getPath().contains("builtin")) {
        return null;
      }

      JsonObject json = getModelJson(resourceId);

      return json.has("loader") && json.getAsJsonPrimitive("loader").getAsString().equals(type) ? loadJsonModelResource(resourceId, json, context) : null;

    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public JsonDeserializationContext getContext() {
    return JsonUnbakedModel.GSON::fromJson;
  }

  abstract public UnbakedModel loadJsonModelResource(Identifier resourceId, JsonObject jsonObject, ModelProviderContext context);

  static JsonObject getModelJson(Identifier location) throws IOException {
    return JsonUnbakedModel.GSON.fromJson(HBMABFIB.getModelJson(location), JsonObject.class);
  }
}
