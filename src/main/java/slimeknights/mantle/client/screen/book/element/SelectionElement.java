package slimeknights.mantle.client.screen.book.element;

import com.mojang.blaze3d.systems.RenderSystem;
import slimeknights.mantle.client.book.data.SectionData;
import slimeknights.mantle.client.book.data.element.TextData;
import slimeknights.mantle.client.screen.book.TextDataRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class SelectionElement extends SizedBookElement {

  public static final int IMG_SIZE = 32;

  public static final int WIDTH = 42;
  public static final int HEIGHT = 42;

  private SectionData section;
  private ImageElement iconRenderer;

  private final int iconX;
  private final int iconY;

  public SelectionElement(int x, int y, SectionData section) {
    super(x, y, WIDTH, HEIGHT);

    this.section = section;

    this.iconX = this.x + WIDTH / 2 - IMG_SIZE / 2;
    this.iconY = this.y + HEIGHT / 2 - IMG_SIZE / 2;
    this.iconRenderer = new ImageElement(this.iconX, this.iconY, IMG_SIZE, IMG_SIZE, section.icon);
  }

  @Override
  public void draw(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, TextRenderer fontRenderer) {
    boolean unlocked = this.section.isUnlocked(this.parent.advancementCache);
    boolean hover = this.isHovered(mouseX, mouseY);

    if (hover) {
      fill(matrixStack, this.iconX, this.iconY, this.iconX + IMG_SIZE, this.iconY + IMG_SIZE, this.parent.book.appearance.hoverColor);
    }
    if (unlocked) {
      RenderSystem.color4f(1F, 1F, 1F, hover ? 1F : 0.5F);
    } else {
      float r = ((this.parent.book.appearance.lockedSectionColor >> 16) & 0xff) / 255.F;
      float g = ((this.parent.book.appearance.lockedSectionColor >> 8) & 0xff) / 255.F;
      float b = (this.parent.book.appearance.lockedSectionColor & 0xff) / 255.F;
      RenderSystem.color4f(r, g, b, 0.75F);
    }

    this.iconRenderer.draw(matrixStack, mouseX, mouseY, partialTicks, fontRenderer);

    if (this.section.parent.appearance.drawSectionListText) {
      String title = this.section.getTitle().replace("\\n", "\n");
      String[] splitTitle = TextDataRenderer.cropStringBySize(title, "", WIDTH + 2,
        fontRenderer.fontHeight * 2 + 1, fontRenderer, 1F);

      for (int i = 0; i < splitTitle.length; i++) {
        int textW = fontRenderer.getWidth(splitTitle[i]);
        int textX = this.x + WIDTH / 2 - textW / 2;
        int textY = this.y + HEIGHT - fontRenderer.fontHeight / 2 + fontRenderer.fontHeight * i;

        fontRenderer.draw(matrixStack, splitTitle[i],
          textX,
          textY,
          hover ? 0xFF000000 : 0x7F000000);
      }
    }
  }

  @Override
  public void drawOverlay(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, TextRenderer fontRenderer) {
    if (this.section != null && this.isHovered(mouseX, mouseY)) {
      List<Text> text = new ArrayList<>();

      text.add(new LiteralText(this.section.getTitle()));

      if (!this.section.isUnlocked(this.parent.advancementCache)) {
        text.add(new LiteralText("Locked").formatted(Formatting.RED));
        text.add(new LiteralText("Requirements:"));

        for (String requirement : this.section.requirements) {
          text.add(new LiteralText(requirement));
        }
      }

      this.drawHoveringText(matrixStack, text, mouseX, mouseY, fontRenderer);
    }
  }

  @Override
  public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
    if (mouseButton == 0 && this.section != null && this.section.isUnlocked(this.parent.advancementCache) && this.isHovered(mouseX, mouseY)) {
      this.parent.openPage(this.parent.book.getFirstPageNumber(this.section, this.parent.advancementCache));
    }
  }
}
