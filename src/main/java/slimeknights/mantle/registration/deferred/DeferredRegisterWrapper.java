package slimeknights.mantle.registration.deferred;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import slimeknights.mantle.registration.object.EnumObject;
import slimeknights.mantle.registration.object.ItemEnumObject;

import java.util.function.BiFunction;
import java.util.function.Supplier;

@SuppressWarnings("WeakerAccess")
public abstract class DeferredRegisterWrapper{
  /** Mod ID for registration */
  public final String modID;
  /* Utilities */

  public DeferredRegisterWrapper(String modID) {
    this.modID = modID;
  }

  /**
   * Gets a resource location object for the given name
   * @param name  Name
   * @return  Resource location string
   */
  protected Identifier resource(String name) {
    return new Identifier(modID, name);
  }

  /**
   * Gets a resource location string for the given name
   * @param name  Name
   * @return  Resource location string
   */
  protected String resourceName(String name) {
    return modID + ":" + name;
  }

  /* Enum objects */

  /**
   * Registers an item with multiple variants, prefixing the name with the value name
   * @param values    Enum values to use for this block
   * @param name      Name of the block
   * @param register  Function to register an entry
   * @return  EnumObject mapping between different block types
   */
  protected static <E extends Enum<E> & StringIdentifiable, V extends T, T extends Block> EnumObject<E,V> registerEnum(E[] values, String name, BiFunction<String,E, Supplier<? extends V>> register) {
    if (values.length == 0) {
      throw new IllegalArgumentException("Must have at least one value");
    }
    // note this cast only works because you cannot extend an enum
    EnumObject.Builder<E,V> builder = new EnumObject.Builder<>(values[0].getDeclaringClass());
    for (E value : values) {
      builder.put(value, register.apply(value.asString() + "_" + name, value));
    }
    return builder.build();
  }


  /**
   * Registers an item with multiple variants, suffixing the name with the value name
   * @param name      Name of the block
   * @param values    Enum values to use for this block
   * @param register  Function to register an entry
   * @return  EnumObject mapping between different block types
   */
  protected static <E extends Enum<E> & StringIdentifiable, V extends Block> EnumObject<E,V> registerEnum(String name, E[] values, BiFunction<String,E,Supplier<? extends V>> register) {
    if (values.length == 0) {
      throw new IllegalArgumentException("Must have at least one value");
    }
    // note this cast only works because you cannot extend an enum
    EnumObject.Builder<E,V> builder = new EnumObject.Builder<>(values[0].getDeclaringClass());
    for (E value : values) {
      builder.put(value, register.apply(name + "_" + value.asString(), value));
    }
    return builder.build();
  }

  /**
   * Registers an item with multiple variants, prefixing the name with the value name
   * @param values    Enum values to use for this block
   * @param name      Name of the block
   * @param register  Function to register an entry
   * @return  EnumObject mapping between different block types
   */
  protected static <E extends Enum<E> & StringIdentifiable, V extends T, T extends Item> ItemEnumObject<E,V> registerItemEnum(E[] values, String name, BiFunction<String,E, Supplier<? extends V>> register) {
    if (values.length == 0) {
      throw new IllegalArgumentException("Must have at least one value");
    }
    // note this cast only works because you cannot extend an enum
    ItemEnumObject.Builder<E,V> builder = new ItemEnumObject.Builder<>(values[0].getDeclaringClass());
    for (E value : values) {
      builder.put(value, register.apply(value.asString() + "_" + name, value));
    }
    return builder.build();
  }


  /**
   * Registers an item with multiple variants, suffixing the name with the value name
   * @param name      Name of the block
   * @param values    Enum values to use for this block
   * @param register  Function to register an entry
   * @return  EnumObject mapping between different block types
   */
  protected static <E extends Enum<E> & StringIdentifiable, V extends Item> ItemEnumObject<E,V> registerItemEnum(String name, E[] values, BiFunction<String,E,Supplier<? extends V>> register) {
    if (values.length == 0) {
      throw new IllegalArgumentException("Must have at least one value");
    }
    // note this cast only works because you cannot extend an enum
    ItemEnumObject.Builder<E,V> builder = new ItemEnumObject.Builder<>(values[0].getDeclaringClass());
    for (E value : values) {
      builder.put(value, register.apply(name + "_" + value.asString(), value));
    }
    return builder.build();
  }
}
