package slimeknights.mantle.client.model;

import com.google.common.base.Charsets;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;

public interface JsonModelResourceProvider extends ModelResourceProvider {
	default UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context) throws ModelProviderException {
		try {
			JsonObject json = getModelJson(resourceId);

			return loadJsonModelResource(resourceId, json, context);
		} catch (IOException e) {
			throw new ModelProviderException("Unable to get json model object:", e);
		}
	}

	public UnbakedModel loadJsonModelResource(Identifier resourceId, JsonObject jsonObject, ModelProviderContext context);

	static JsonObject getModelJson(Identifier location) throws IOException {
		Identifier file = new Identifier(location.getNamespace(), location.getPath() + ".json");
		Resource resource = MinecraftClient.getInstance().getResourceManager().getResource(file);
		return JsonUnbakedModel.GSON.fromJson(new BufferedReader(new InputStreamReader(resource.getInputStream(), Charsets.UTF_8)), JsonObject.class);
	}
}
