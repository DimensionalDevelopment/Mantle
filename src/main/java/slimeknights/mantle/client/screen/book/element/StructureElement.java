package slimeknights.mantle.client.screen.book.element;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.AffineTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.structure.Structure;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Quaternion;
import slimeknights.mantle.client.book.structure.StructureInfo;
import slimeknights.mantle.client.book.structure.world.TemplateWorld;
import slimeknights.mantle.client.screen.book.BookScreen;

import java.util.List;
import java.util.stream.IntStream;

public class StructureElement extends SizedBookElement {

  public boolean canTick = false;

  public float scale = 50f;
  public float transX = 0;
  public float transY = 0;
  public AffineTransformation additionalTransform;
  public final StructureInfo renderInfo;
  public final TemplateWorld structureWorld;

  public long lastStep = -1;
  public long lastPrintedErrorTimeMs = -1;

  public StructureElement(int x, int y, int width, int height, Structure template, List<Structure.StructureBlockInfo> structure) {
    super(x, y, width, height);

    int[] size = {template.getSize().getX(), template.getSize().getY(), template.getSize().getZ()};

    this.scale = 100f / (float) IntStream.of(size).max().getAsInt();

    float sx = (float) width / (float) BookScreen.PAGE_WIDTH;
    float sy = (float) height / (float) BookScreen.PAGE_HEIGHT;

    this.scale *= Math.min(sx, sy);

    this.renderInfo = new StructureInfo(structure);

    this.structureWorld = new TemplateWorld(structure, renderInfo);

    this.transX = x + width / 2F;
    this.transY = y + height / 2F;

    this.additionalTransform = new AffineTransformation(null, new Quaternion(25, 0, 0, true), null, new Quaternion(0, -45, 0, true));
  }

  @Override
  public void draw(MatrixStack transform, int mouseX, int mouseY, float partialTicks, TextRenderer fontRenderer) {
    VertexConsumerProvider.Immediate buffer = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
    MatrixStack.Entry lastEntryBeforeTry = transform.peek();

    try {
      long currentTime = System.currentTimeMillis();

      if (this.lastStep < 0)
        this.lastStep = currentTime;
      else if (this.canTick && currentTime - this.lastStep > 200) {
        this.renderInfo.step();
        this.lastStep = currentTime;
      }

      if (!this.canTick) {
        this.renderInfo.reset();
      }

      int structureLength = this.renderInfo.structureLength;
      int structureWidth = this.renderInfo.structureWidth;
      int structureHeight = this.renderInfo.structureHeight;

      transform.push();

      final BlockRenderManager blockRender = MinecraftClient.getInstance().getBlockRenderManager();

      transform.translate(this.transX, this.transY, Math.max(structureHeight, Math.max(structureWidth, structureLength)));
      transform.scale(this.scale, -this.scale, 1);
      push(this.additionalTransform, transform);
      transform.multiply(new Quaternion(0, 0, 0, true));

      transform.translate(structureLength / -2f, structureHeight / -2f, structureWidth / -2f);

      for (int h = 0; h < structureHeight; h++) {
        for (int l = 0; l < structureLength; l++) {
          for (int w = 0; w < structureWidth; w++) {
            BlockPos pos = new BlockPos(l, h, w);
            BlockState state = this.structureWorld.getBlockState(pos);

            if (!this.structureWorld.getBlockState(pos).isAir()) {
              transform.push();
              transform.translate(l, h, w);

              int overlay;

              if (pos.equals(new BlockPos(1, 1, 1)))
                overlay = OverlayTexture.getUv(0, true);
              else
                overlay = OverlayTexture.DEFAULT_UV;

              BlockEntity te = structureWorld.getBlockEntity(pos);

              if (te != null)

                RenderSystem.disableLighting();

              blockRender.renderBlockAsEntity(state, transform, buffer, 0xf000f0, overlay);
              transform.pop();
            }
          }
        }
      }

      transform.pop();
      transform.pop();

    } catch (Exception e) {
      final long now = System.currentTimeMillis();

      if (now > this.lastPrintedErrorTimeMs + 1000) {
        e.printStackTrace();
        this.lastPrintedErrorTimeMs = now;
      }

      while (lastEntryBeforeTry != transform.peek())
        transform.pop();
    }

    buffer.draw();
  }

  private static void push(AffineTransformation transformation, MatrixStack stack) {
    stack.peek().getModel().multiply(transformation.getMatrix());
  }

  @Override
  public void mouseClicked(double mouseX, double mouseY, int mouseButton) {
    super.mouseClicked(mouseX, mouseY, mouseButton);
  }

  @Override
  public void mouseDragged(double clickX, double clickY, double mouseX, double mouseY, double lastX, double lastY, int button) {
    double dx = mouseX - lastX;
    double dy = mouseY - lastY;
    this.additionalTransform = forRotation(dx * 80D / 104, dy * 0.8).multiply(this.additionalTransform);
  }

  @Override
  public void mouseReleased(double mouseX, double mouseY, int clickedMouseButton) {
    super.mouseReleased(mouseX, mouseY, clickedMouseButton);
  }

  private AffineTransformation forRotation(double rX, double rY) {
    Vector3f axis = new Vector3f((float) rY, (float) rX, 0);
    float angle = (float) Math.sqrt(axis.dot(axis));

    if (!axis.normalize())
      return AffineTransformation.identity();

    return new AffineTransformation(null, new Quaternion(axis, angle, true), null, null);
  }
}
