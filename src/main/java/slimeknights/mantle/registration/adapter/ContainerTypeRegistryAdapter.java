package slimeknights.mantle.registration.adapter;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

@SuppressWarnings("unused")
public class ContainerTypeRegistryAdapter extends RegistryAdapter<ScreenHandlerType<?>> {
  /** @inheritDoc */
  public ContainerTypeRegistryAdapter(String modId) {
    super(modId);
  }

  /**
   * Registers a container type
   * @param <C>      Container type
   * @param factory  Container factory
   * @param name     Container name
   * @return  Registry object containing the container type
   */
  public <C extends ScreenHandler> ScreenHandlerType<?> registerType(ScreenHandlerRegistry.SimpleClientHandlerFactory<C> factory, Identifier name) {
    return ScreenHandlerRegistry.registerSimple(name, factory);
  }
}
