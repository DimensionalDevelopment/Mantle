package slimeknights.mantle.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.CraftingResultSlot;

public class CraftingCustomSlot extends CraftingResultSlot {

  private final CraftingInventory craftMatrix;
  private final IContainerCraftingCustom callback;

  /**
   * @param callback          Container that gets the crafting call on crafting
   * @param player            Player that does the crafting
   * @param craftingInventory Inventory where the ingredients are taken from
   * @param craftResult       Inventory where the result is put
   */
  public CraftingCustomSlot(IContainerCraftingCustom callback, PlayerEntity player, CraftingInventory craftingInventory, Inventory craftResult, int slotIndex, int xPosition, int yPosition) {
    super(player, craftingInventory, craftResult, slotIndex, xPosition, yPosition);

    this.craftMatrix = craftingInventory;
    this.callback = callback;
  }

  @Override
  public ItemStack onTakeItem(PlayerEntity playerIn, ItemStack stack) {
//    BasicEventHooks.firePlayerCraftingEvent(playerIn, stack, this.craftMatrix); what the fuck is a firePlayerCraftingEvent?
    this.onCrafted(stack);

    this.callback.onCrafting(playerIn, stack, this.craftMatrix);

    return stack;
  }
}
