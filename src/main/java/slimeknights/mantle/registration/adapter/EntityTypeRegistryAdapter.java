package slimeknights.mantle.registration.adapter;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

/**
 * Registry adapter for registering entity types
 */
@SuppressWarnings("unused")
public class EntityTypeRegistryAdapter extends RegistryAdapter<EntityType<?>> {
  /** @inheritDoc */
  public EntityTypeRegistryAdapter(String modId) {
    super(modId);
  }

  /**
   * Registers an entity type from a builder
   * @param builder  Builder instance
   * @param name     Type name
   * @param <T>      Entity type
   * @return  Registered entity type
   */
  public <T extends Entity> EntityType<T> register(EntityType.Builder<T> builder, String name) {
    return register(builder.build(resourceName(name)), name);
  }
}
