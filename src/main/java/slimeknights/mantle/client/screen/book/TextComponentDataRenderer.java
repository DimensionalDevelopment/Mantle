package slimeknights.mantle.client.screen.book;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Language;
import slimeknights.mantle.client.book.data.element.TextComponentData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Environment(EnvType.CLIENT)
public class TextComponentDataRenderer {

  /**
   * Renders the given Text Components on the screen and returns the action if any of them have one.
   *
   * @param matrixStack the matrix stack to render with
   * @param x           the x position to render at
   * @param y           the y position to render at
   * @param boxWidth    the width of the given render box
   * @param boxHeight   the height of the given render box
   * @param data        the list of text component data to draw
   * @param mouseX      the mouseY
   * @param mouseY      the mouseX
   * @param fr          the font renderer
   * @param tooltip     the list of tooltips
   * @return the action if there's any
   */
  public static String drawText(MatrixStack matrixStack, int x, int y, int boxWidth, int boxHeight, TextComponentData[] data, int mouseX, int mouseY, TextRenderer fr, List<Text> tooltip) {
    String action = "";

    int atX = x;
    int atY = y;

    float prevScale = 1.F;

    for (TextComponentData item : data) {
      int box1X, box1Y, box1W = 9999, box1H = y + fr.fontHeight;
      int box2X, box2Y = 9999, box2W, box2H;
      int box3X = 9999, box3Y = 9999, box3W, box3H;

      if (item == null || item.text == null) {
        continue;
      }

      if (item.text.getString().equals("\n")) {
        atX = x;
        atY += fr.fontHeight;
        continue;
      }

      if (item.isParagraph) {
        atX = x;
        atY += fr.fontHeight * 2 * prevScale;
      }

      prevScale = item.scale;

      List<StringVisitable> textLines = splitTextComponentBySize(item.text, boxWidth, boxHeight - (atY - y), boxWidth - (atX - x), fr, item.scale);

      box1X = atX;
      box1Y = atY;
      box2X = x;
      box2W = x + boxWidth;

      for (int lineNumber = 0; lineNumber < textLines.size(); ++lineNumber) {
        if (lineNumber == textLines.size() - 1) {
          box3X = atX;
          box3Y = atY;
        }

        StringVisitable textComponent = textLines.get(lineNumber);
        drawScaledTextComponent(matrixStack, fr, textComponent, atX, atY, item.dropShadow, item.scale);

        if (lineNumber < textLines.size() - 1) {
          atY += fr.fontHeight;
          atX = x;
        }

        if (lineNumber == 0) {
          box2Y = atY;

          if (atX == x) {
            box1W = x + boxWidth;
          } else {
            box1W = atX;
          }
        }
      }

      box2H = atY;

      atX += fr.getWidth(Language.getInstance().reorder(textLines.get(textLines.size() - 1))) * item.scale;
      if (atX - x >= boxWidth) {
        atX = x;
        atY += fr.fontHeight * item.scale;
      }

      box3W = atX;
      box3H = (int) (atY + fr.fontHeight * item.scale);

      boolean mouseCheck = (mouseX >= box1X && mouseX <= box1W && mouseY >= box1Y && mouseY <= box1H && box1X != box1W && box1Y != box1H) || (mouseX >= box2X && mouseX <= box2W && mouseY >= box2Y && mouseY <= box2H && box2X != box2W && box2Y != box2H) || (mouseX >= box3X && mouseX <= box3W && mouseY >= box3Y && mouseY <= box3H && box3X != box3W && box1Y != box3H);

      if (item.tooltips != null && item.tooltips.length > 0) {
        // Uncomment to render bounding boxes for event handling
        if (BookScreen.debug) {
          drawGradientRect(box1X, box1Y, box1W, box1H, 0xFF00FF00, 0xFF00FF00);
          drawGradientRect(box2X, box2Y, box2W, box2H, 0xFFFF0000, 0xFFFF0000);
          drawGradientRect(box3X, box3Y, box3W, box3H, 0xFF0000FF, 0xFF0000FF);
          drawGradientRect(mouseX, mouseY, mouseX + 5, mouseY + 5, 0xFFFF00FF, 0xFFFFFF00);
        }

        if (mouseCheck) {
          tooltip.addAll(Arrays.asList(item.tooltips));
        }
      }

      if (item.action != null && !item.action.isEmpty()) {
        if (mouseCheck) {
          action = item.action;
        }
      }

      if (atY >= y + boxHeight) {
        if (item.dropShadow) {
          fr.drawWithShadow(matrixStack, "...", atX, atY, 0);
        } else {
          fr.draw(matrixStack, "...", atX, atY, 0);
        }
        break;
      }

      y = atY;
    }

    if (BookScreen.debug && !action.isEmpty()) {
      tooltip.add(LiteralText.EMPTY);
      tooltip.add(new LiteralText("Action: " + action).formatted(Formatting.GRAY));
    }

    return action;
  }

  /**
   * @param textComponent the actual text component to split
   * @param width         the width of the text
   * @param height        the height of the text
   * @param firstWidth    the first with of the text
   * @param fontRenderer  the font renderer to use
   * @param scale         the scale to use
   * @return the list of split text components based on the given size
   */
  public static List<StringVisitable> splitTextComponentBySize(Text textComponent, int width, int height, int firstWidth, TextRenderer fontRenderer, float scale) {
    int curWidth = (int) (fontRenderer.getWidth(textComponent) * scale);

    int curHeight = (int) (fontRenderer.fontHeight * scale);
    boolean needsWrap = false;
    List<StringVisitable> textLines = new ArrayList<>();

    if ((curHeight == (int) (fontRenderer.fontHeight * scale) && curWidth > firstWidth) || (curHeight != (int) (fontRenderer.fontHeight * scale) && curWidth > width)) {
      needsWrap = true;
    }

    if (needsWrap) {
      textLines = new ArrayList<>(fontRenderer.getTextHandler().wrapLines(textComponent, firstWidth, Style.EMPTY));
    } else {
      textLines.add(textComponent);
    }

    return textLines;
  }

  /**
   * Draws a text component with the given scale at the given position
   *
   * @param matrixStack   the given matrix stack used for rendering.
   * @param font          the font renderer to render with
   * @param textComponent the text component to render
   * @param x             the x position to render at
   * @param y             the y position to render at
   * @param dropShadow    if there should be a shadow on the text
   * @param scale         the scale to render as
   */
  public static void drawScaledTextComponent(MatrixStack matrixStack, TextRenderer font, StringVisitable textComponent, float x, float y, boolean dropShadow, float scale) {
    RenderSystem.pushMatrix();
    RenderSystem.translatef(x, y, 0);
    RenderSystem.scalef(scale, scale, 1F);

    if (dropShadow) {
      font.drawWithShadow(matrixStack, Language.getInstance().reorder(textComponent), 0, 0, 0);
    } else {
      font.drawWithShadow(matrixStack, Language.getInstance().reorder(textComponent), 0, 0, 0);
    }

    RenderSystem.popMatrix();
  }

  /**
   * Draws a gradient box with the from the given points
   * Only used in debug
   *
   * @param left       the left position
   * @param top        the top position
   * @param right      the right position
   * @param bottom     the bottom position
   * @param startColor the start color to use
   * @param endColor   the end color to use
   */
  private static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
    float f = (float) (startColor >> 24 & 255) / 255.0F;
    float f1 = (float) (startColor >> 16 & 255) / 255.0F;
    float f2 = (float) (startColor >> 8 & 255) / 255.0F;
    float f3 = (float) (startColor & 255) / 255.0F;
    float f4 = (float) (endColor >> 24 & 255) / 255.0F;
    float f5 = (float) (endColor >> 16 & 255) / 255.0F;
    float f6 = (float) (endColor >> 8 & 255) / 255.0F;
    float f7 = (float) (endColor & 255) / 255.0F;
    RenderSystem.disableTexture();
    RenderSystem.disableAlphaTest();
    RenderSystem.blendFuncSeparate(770, 771, 1, 0);
    RenderSystem.shadeModel(7425);
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder vertexBuffer = tessellator.getBuffer();
    vertexBuffer.begin(7, VertexFormats.POSITION_COLOR);
    vertexBuffer.vertex(right, top, 0D).color(f1, f2, f3, f).next();
    vertexBuffer.vertex(left, top, 0D).color(f1, f2, f3, f).next();
    vertexBuffer.vertex(left, bottom, 0D).color(f5, f6, f7, f4).next();
    vertexBuffer.vertex(right, bottom, 0D).color(f5, f6, f7, f4).next();
    tessellator.draw();
    RenderSystem.shadeModel(7424);
    RenderSystem.enableAlphaTest();
    RenderSystem.enableTexture();
  }
}
