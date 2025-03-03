package slimeknights.mantle.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.Tag;
import slimeknights.mantle.util.JsonHelper;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Extension of the vanilla ingredient to make stack size checks
 */
public class SizedIngredient implements Predicate<ItemStack> {
  /** Empty sized ingredient wrapper. Matches only the empty stack of size o */
  public static final SizedIngredient EMPTY = new SizedIngredient(Ingredient.EMPTY, 0);

  /** Ingredient to use in recipe match */
  private final Ingredient ingredient;
  /** Amount of this ingredient needed */
  private final int amountNeeded;

  /** Last list of matching stacks from the ingredient */
  private WeakReference<ItemStack[]> lastIngredientMatch;
  /** Cached matching stacks from last time it was requested */
  private List<ItemStack> matchingStacks;

  public SizedIngredient(Ingredient ingredient, int amountNeeded) {
    this.ingredient = ingredient;
    this.amountNeeded = amountNeeded;
  }

  /**
   * Gets a new sized ingredient with a size of 1
   * @param ingredient  Ingredient
   * @return  Sized ingredient matching any size
   */
  public static SizedIngredient of(Ingredient ingredient) {
    return new SizedIngredient(ingredient, 1);
  }

  /**
   * Gets a new sized ingredient with a size of 1
   * @param amountNeeded  Number that must match of this ingredient
   * @param items         List of items
   * @return  Sized ingredient matching any size
   */
  public static SizedIngredient fromItems(int amountNeeded, ItemConvertible... items) {
    return new SizedIngredient(Ingredient.ofItems(items), amountNeeded);
  }

  /**
   * Gets a new sized ingredient with a size of 1
   * @param items  List of items
   * @return  Sized ingredient matching any size
   */
  public static SizedIngredient fromItems(ItemConvertible... items) {
    return fromItems(1, items);
  }

  /**
   * Gets a new sized ingredient with a size of 1
   * @param tag           Tag to match
   * @param amountNeeded  Number that must match of this ingredient
   * @return  Sized ingredient matching any size
   */
  public static SizedIngredient fromTag(Tag<Item> tag, int amountNeeded) {
    return new SizedIngredient(Ingredient.fromTag(tag), amountNeeded);
  }

  /**
   * Gets a new sized ingredient with a size of 1
   * @param tag  Tag to match
   * @return  Sized ingredient matching any size
   */
  public static SizedIngredient fromTag(Tag<Item> tag) {
    return fromTag(tag, 1);
  }

  @Override
  public boolean test(ItemStack stack) {
    return stack.getCount() >= amountNeeded && ingredient.test(stack);
  }

  /**
   * Checks if the ingredient has no matching stacks
   * @return  True if the ingredient has no matching stacks
   */
  public boolean hasNoMatchingStacks() {
    return ingredient.isEmpty();
  }

  /**
   * Gets a list of matching stacks for display in JEI
   * @return  List of matching stacks
   */
  public List<ItemStack> getMatchingStacks() {
    ItemStack[] ingredientMatch = ingredient.getMatchingStacksClient();
    // if we never cached, or the array instance changed since we last cached, recache
    if (matchingStacks == null || lastIngredientMatch.get() != ingredientMatch) {
      matchingStacks = Arrays.stream(ingredientMatch).map(stack -> {
        if (stack.getCount() != amountNeeded) {
          stack = stack.copy();
          stack.setCount(amountNeeded);
        }
        return stack;
      }).collect(Collectors.toList());
      lastIngredientMatch = new WeakReference<>(ingredientMatch);
    }
    return matchingStacks;
  }

  /**
   * Writes this ingredient to the packet buffer
   * @param buffer  Buffer instance
   */
  public void write(PacketByteBuf buffer) {
    buffer.writeVarInt(amountNeeded);
    ingredient.write(buffer);
  }

  /**
   * Writes this sized ingredient to a JSON object
   * @return  JsonObject of sized ingredient
   */
  public JsonObject serialize() {
    JsonElement ingredient = this.ingredient.toJson();
    JsonObject json = null;
    // try using the object itself as our JSON
    if (ingredient.isJsonObject()) {
      json = ingredient.getAsJsonObject();
      // if it has a property conflict, do nested
      if (json.has("ingredient") || json.has("amount_needed")) {
        json = null;
      }
    }
    // if we could not use the ingredient, nest it
    if (json == null) {
      json = new JsonObject();
      json.add("ingredient", ingredient);
    }
    // add amount needed and return
    if (amountNeeded != 1) {
      json.addProperty("amount_needed", amountNeeded);
    }
    return json;
  }

  /**
   * Reads a sized ingredient from the packet buffer
   * @param buffer  Buffer instance
   * @return  Sized ingredient
   */
  public static SizedIngredient read(PacketByteBuf buffer) {
    int amountNeeded = buffer.readVarInt();
    Ingredient ingredient = Ingredient.fromPacket(buffer);
    return new SizedIngredient(ingredient, amountNeeded);
  }

  /**
   * Reads a sized ingredient from JSON
   * @param json  JSON instance
   * @return  Sized ingredient
   */
  public static SizedIngredient deserialize(JsonObject json) {
    int amountNeeded = net.minecraft.util.JsonHelper.getInt(json, "amount_needed", 1);
    // if we have a nested value, read as nested
    Ingredient ingredient;
    if (json.has("ingredient")) {
      ingredient = Ingredient.fromJson(JsonHelper.getElement(json, "ingredient"));
    } else {
      ingredient = Ingredient.fromJson(json);
    }

    // return ingredient
    return new SizedIngredient(ingredient, amountNeeded);
  }

  public int getAmountNeeded() {
    return amountNeeded;
  }
}
