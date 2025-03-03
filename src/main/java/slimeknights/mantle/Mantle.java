package slimeknights.mantle;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.config.MantleConfig;
import slimeknights.mantle.network.MantleNetwork;

/**
 * Mantle
 *
 * Central mod object for Mantle
 *
 * @author Sunstrike <sun@sunstrike.io>
 */
public class Mantle implements ModInitializer {

  public static final String modId = "mantle";
  public static final Logger logger = LogManager.getLogger("Mantle");

  /* Instance of this mod, used for grabbing prototype fields */
  public static Mantle instance;
  public static MantleConfig config;

  @Override
  public void onInitialize() {
    AutoConfig.register(MantleConfig.class, GsonConfigSerializer::new);
    config = AutoConfig.getConfigHolder(MantleConfig.class).getConfig();

    MantleNetwork.registerPackets();
  }

  public Mantle() {
//    ModLoadingContext.get().registerConfig(Type.CLIENT, Config.CLIENT_SPEC);
//    ModLoadingContext.get().registerConfig(Type.SERVER, Config.SERVER_SPEC);

    instance = this;
//    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
//    bus.addListener(Config::configChanged);
//    bus.addGenericListener(RecipeSerializer.class, this::registerRecipeSerializers);
//    bus.addGenericListener(GlobalLootModifierSerializer.class, this::registerGlobalLootModifiers);
  }
//
//  private void registerRecipeSerializers(final RegistryEvent.Register<RecipeSerializer<?>> event) {
//    RegistryAdapter<RecipeSerializer<?>> adapter = new RegistryAdapter<>(event.getRegistry());
//    adapter.register(new ShapedFallbackRecipe.Serializer(), "crafting_shaped_fallback");
//    adapter.register(new ShapedRetexturedRecipe.Serializer(), "crafting_shaped_retextured");
//
//    CraftingHelper.register(IngredientWithout.ID, IngredientWithout.SERIALIZER);
//    CraftingHelper.register(IngredientIntersection.ID, IngredientIntersection.SERIALIZER);
//
//    // done here as no dedicated event
//    MantleLoot.register();
//  }
//
//  private void registerGlobalLootModifiers(final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
//    RegistryAdapter<GlobalLootModifierSerializer<?>> adapter = new RegistryAdapter<>(event.getRegistry());
//    adapter.register(new AddEntryLootModifier.Serializer(), "add_entry");
//    adapter.register(new ReplaceItemLootModifier.Serializer(), "replace_item");
//  }

  /**
   * Gets a resource location for Mantle
   * @param name Name
   * @return Identifier instance
   */
  public static Identifier getResource(String name) {
    return new Identifier(modId, name);
  }
}
