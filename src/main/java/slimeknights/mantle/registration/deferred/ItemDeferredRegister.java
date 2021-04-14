package slimeknights.mantle.registration.deferred;

import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.registry.Registry;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemEnumObject;
import slimeknights.mantle.registration.object.ItemObject;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Deferred register that registers items with wrappers
 */
@SuppressWarnings("unused")
public class ItemDeferredRegister extends DeferredRegisterWrapper {

  public ItemDeferredRegister(String modID) {
    super(modID);
  }

  /**
   * Adds a new item to the list to be registered, using the given supplier
   * @param name   Item name
   * @param sup    Supplier returning an item
   * @return  Item registry object
   */
  public <I extends Item> ItemObject<I> register(String name, Supplier<I> sup) {
    return new ItemObject<>(Registry.register(Registry.ITEM, new Identifier(modID, name), sup.get()));
  }

  /**
   * Adds a new item to the list to be registered, based on the given item properties
   * @param name   Item name
   * @param props  Item properties
   * @return  Item registry object
   */
  public ItemObject<Item> register(String name, Item.Settings props) {
    return register(name, () -> new Item(props));
  }

  /**
   * Registers an item with multiple variants, prefixing the name with the value name
   * @param values   Enum values to use for this item
   * @param name     Name of the block
   * @param mapper   Function to get a item for the given enum value
   * @return  EnumObject mapping between different item types
   */
  public <T extends Enum<T> & StringIdentifiable, I extends Item> ItemEnumObject<T,I> registerEnum(T[] values, String name, Function<T,? extends I> mapper) {
    return registerItemEnum(values, name, (fullName, type) -> register(fullName, () -> mapper.apply(type)));
  }

  /**
   * Registers an item with multiple variants, suffixing the name with the value name
   * @param values   Enum values to use for this item
   * @param name     Name of the block
   * @param mapper   Function to get a item for the given enum value
   * @return  EnumObject mapping between different item types
   */
  public <T extends Enum<T> & StringIdentifiable, I extends Item> ItemEnumObject<T,I> registerEnum(String name, T[] values, Function<T,? extends I> mapper) {
    return registerItemEnum(name, values, (fullName, type) -> register(fullName, () -> mapper.apply(type)));
  }
}
