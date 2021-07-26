package slimeknights.mantle.network.packet;

import slimeknights.mantle.client.book.BookHelper;
import java.io.IOException;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;

public class UpdateSavedPagePacket extends IThreadsafePacket {

  private String pageName;

  public UpdateSavedPagePacket(String pageName) {
    super(null);
    this.pageName = pageName;
  }

  public UpdateSavedPagePacket(PacketByteBuf buffer) {
    super(buffer);
    this.pageName = buffer.readString(32767);
  }

  @Override
  public void encode(PacketByteBuf buf) {
    buf.writeString(this.pageName);
  }



  @Override
  public void handleThreadsafe(PlayerEntity player) {
    if (player != null && this.pageName != null) {
      ItemStack is = player.getStackInHand(Hand.MAIN_HAND);
      if (!is.isEmpty()) {
        BookHelper.writeSavedPageToBook(is, this.pageName);
      }
    }
  }

  @Override
  public void read(PacketByteBuf buf) throws IOException {
    new UpdateSavedPagePacket(buf);
  }

  @Override
  public void write(PacketByteBuf buf) {
    this.encode(buf);
  }
}
