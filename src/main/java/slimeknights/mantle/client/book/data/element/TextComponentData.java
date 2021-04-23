package slimeknights.mantle.client.book.data.element;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class TextComponentData {

  public static final TextComponentData LINEBREAK = new TextComponentData("\n");

  public Text text;

  public boolean isParagraph = false;
  public boolean dropShadow = false;
  public float scale = 1.F;
  public String action = "";
  public Text[] tooltips = null;

  public TextComponentData(String text) {
    this(new LiteralText(text));
  }

  public TextComponentData(Text text) {
    this.text = text;
  }
}
