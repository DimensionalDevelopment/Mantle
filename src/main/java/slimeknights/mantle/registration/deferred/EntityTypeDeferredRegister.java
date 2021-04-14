package slimeknights.mantle.registration.deferred;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.registry.Registry;
import slimeknights.mantle.registration.ItemProperties;

import java.util.function.Supplier;

/**
 * Deferred register for an entity, building the type from a builder instance and adding an egg
 */
@SuppressWarnings("unused")
public class EntityTypeDeferredRegister extends DeferredRegisterWrapper {

  public EntityTypeDeferredRegister(String modID) {
    super(modID);
  }

  /**
   * Registers a entity type for the given entity type builder
   * @param name  Entity name
   * @param sup   Entity builder instance
   * @param <T>   Entity class type
   * @return  Entity registry object
   */
  public <T extends Entity> EntityType<T> register(String name, Supplier<EntityType.Builder<T>> sup) {
    return Registry.register(Registry.ENTITY_TYPE, new Identifier(modID, name), sup.get().build(name));
  }

  /**
   * Registers a entity type for the given entity type builder, and registers a spawn egg for it
   * @param name       Entity name
   * @param sup        Entity builder instance
   * @param primary    Primary egg color
   * @param secondary  Secondary egg color
   * @param <T>   Entity class type
   * @return  Entity registry object
   */
  public <T extends Entity> EntityType<T> registerWithEgg(String name, Supplier<EntityType.Builder<T>> sup, int primary, int secondary) {
    EntityType<T> type = sup.get().build(resourceName(name));
    Registry.register(Registry.ITEM,name + "_spawn_egg", new SpawnEggItem(type, primary, secondary, ItemProperties.EGG_PROPS));
    return Registry.register(Registry.ENTITY_TYPE, name, type);
  }
}
