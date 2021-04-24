package slimeknights.mantle.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ResourceManager;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.model.FallbackModelLoader;
import slimeknights.mantle.client.model.RetexturedModel;
import slimeknights.mantle.client.model.fluid.FluidsModel;
import slimeknights.mantle.client.model.inventory.InventoryModel;
import slimeknights.mantle.client.model.util.ModelHelper;

@SuppressWarnings("unused")
public class MantleClient implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
    if (resourceManager instanceof ReloadableResourceManager) {
      ((ReloadableResourceManager)resourceManager).registerListener(ModelHelper.LISTENER);
      ((ReloadableResourceManager)resourceManager).registerListener(new BookLoader());
    }

    ModelLoadingRegistry.INSTANCE.registerResourceProvider((manager) -> FallbackModelLoader.INSTANCE);
    ModelLoadingRegistry.INSTANCE.registerResourceProvider((manager) -> InventoryModel.Loader.INSTANCE);
//    ModelLoadingRegistry.INSTANCE.registerResourceProvider((manager) -> ConnectedModel.Loader.INSTANCE);
    ModelLoadingRegistry.INSTANCE.registerResourceProvider((manager) -> FluidsModel.Loader.INSTANCE);
    ModelLoadingRegistry.INSTANCE.registerResourceProvider((manager) -> RetexturedModel.Loader.INSTANCE);
  }
}
