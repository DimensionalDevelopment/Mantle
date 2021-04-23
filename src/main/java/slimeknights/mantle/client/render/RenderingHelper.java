package slimeknights.mantle.client.render;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class RenderingHelper {
  /* Rotation */

  /**
   * Applies horizontal rotation to the given TESR
   * @param matrices  Matrix stack
   * @param state     Block state, checked for {@link Properties#HORIZONTAL_FACING}
   * @return  True if rotation was applied. Caller is expected to call {@link MatrixStack#pop()} if true
   */
  public static boolean applyRotation(MatrixStack matrices, BlockState state) {
    if (state.contains(Properties.HORIZONTAL_FACING)) {
      return applyRotation(matrices, state.get(Properties.HORIZONTAL_FACING));
    }
    return false;
  }

  /**
   * Applies horizontal rotation to the given TESR
   * @param matrices  Matrix stack
   * @param facing    Direction of rotation
   * @return  True if rotation was applied. Caller is expected to call {@link MatrixStack#pop()} if true
   */
  public static boolean applyRotation(MatrixStack matrices, Direction facing) {
    // south has a facing of 0, no rotation needed
    if (facing.getAxis().isHorizontal() && facing != Direction.SOUTH) {
      matrices.push();
      matrices.translate(0.5, 0, 0.5);
      matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-90f * (facing.getHorizontal())));
      matrices.translate(-0.5, 0, -0.5);
      return true;
    }
    return false;
  }


  /* Items */

  /**
   * Renders a single item in a TESR
   * @param matrices    Matrix stack inst ance
   * @param buffer      Buffer instance
   * @param item        Item to render
   * @param light       Model light
   */
  public static void renderItem(MatrixStack matrices, VertexConsumerProvider buffer, ItemStack item, int light) {
    // render the actual item
    MinecraftClient.getInstance().getItemRenderer().renderItem(item, Mode.NONE, light, OverlayTexture.DEFAULT_UV, matrices, buffer);
    matrices.pop();
  }

  /**
   * Draws hovering text on the screen
   * @param mStack         Matrix stack instance
   * @param textLines      Tooltip lines
   * @param mouseX         Mouse X position
   * @param mouseY         Mouse Y position
   * @param screenWidth    Screen width
   * @param screenHeight   Screen height
   * @param maxTextWidth   Max text width
   * @param font           Font
   * @deprecated   Remove in 1.17, use {@link GuiUtils#drawHoveringText(MatrixStack, List, int, int, int, int, int, FontRenderer)}
   */
  @Deprecated
  public static void drawHoveringText(MatrixStack mStack, List<Text> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, TextRenderer font) {
    // FIXME: PORT
//    GuiUtils.drawHoveringText(mStack, textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth,
//            0xF0100010,
//            0x505000FF,
//            (0x505000FF & 0xFEFEFE) >> 1 | 0x505000FF & 0xFF000000, font);
  }

  /**
   * @deprecated   Remove in 1.17, use {@link GuiUtils#drawHoveringText(MatrixStack, List, int, int, int, int, int, int, int, int, TextRenderer)}
   */
  @Deprecated
  private static void drawHoveringText(MatrixStack mStack, List<Text> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth,
                                       int backgroundColor, int borderColorStart, int borderColorEnd, TextRenderer font) {
    // FIXME: PORT
//    GuiUtils.drawHoveringText(mStack, textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, backgroundColor, borderColorStart, borderColorEnd, font);
  }
}
