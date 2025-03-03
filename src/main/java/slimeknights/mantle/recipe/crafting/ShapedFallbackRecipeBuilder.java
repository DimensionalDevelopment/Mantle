package slimeknights.mantle.recipe.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonFactory;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class ShapedFallbackRecipeBuilder {
  private final ShapedRecipeJsonFactory base;
  private final List<Identifier> alternatives = new ArrayList<>();

  public ShapedFallbackRecipeBuilder(ShapedRecipeJsonFactory base) {
    this.base = base;
  }

  /**
   * Adds a single alternative to this recipe. Any matching alternative causes this recipe to fail
   * @param location  Alternative
   * @return  Builder instance
   */
  public ShapedFallbackRecipeBuilder addAlternative(Identifier location) {
    this.alternatives.add(location);
    return this;
  }

  /**
   * Adds a list of alternatives to this recipe. Any matching alternative causes this recipe to fail
   * @param locations  Alternative list
   * @return  Builder instance
   */
  public ShapedFallbackRecipeBuilder addAlternatives(Collection<Identifier> locations) {
    this.alternatives.addAll(locations);
    return this;
  }

  /**
   * Builds the recipe using the output as the name
   * @param consumer  Recipe consumer
   */
  public void build(Consumer<RecipeJsonProvider> consumer) {
    base.offerTo(base -> consumer.accept(new Result(base, alternatives)));
  }

  /**
   * Builds the recipe using the given ID
   * @param consumer  Recipe consumer
   * @param id        Recipe ID
   */
  public void build(Consumer<RecipeJsonProvider> consumer, Identifier id) {
    base.offerTo(base -> consumer.accept(new Result(base, alternatives)), id);
  }

  public class Result implements RecipeJsonProvider {
    private final RecipeJsonProvider base;
    private final List<Identifier> alternatives;

    public Result(RecipeJsonProvider base, List<Identifier> alternatives) {
      this.base = base;
      this.alternatives = alternatives;
    }

    @Override
    public void serialize(JsonObject json) {
      base.serialize(json);
      json.add("alternatives", alternatives.stream()
                                           .map(Identifier::toString)
                                           .collect(JsonArray::new, JsonArray::add, JsonArray::addAll));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.SHAPED;
    }

    @Override
    public Identifier getRecipeId() {
      return base.getRecipeId();
    }

    @Nullable
    @Override
    public JsonObject toAdvancementJson() {
      return base.toAdvancementJson();
    }

    @Nullable
    @Override
    public Identifier getAdvancementId() {
      return base.getAdvancementId();
    }
  }
}
