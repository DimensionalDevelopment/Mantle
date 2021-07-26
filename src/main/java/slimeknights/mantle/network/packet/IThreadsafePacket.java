package slimeknights.mantle.network.packet;

import java.io.IOException;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

/**
 * Packet instance that automatically runs the logic on the main thread for thread safety
 */
public abstract class IThreadsafePacket extends ISimplePacket {


  public IThreadsafePacket(PacketByteBuf packetByteBuf) {
    super(packetByteBuf);
  }

  @Override
  public void handle(PlayerEntity playerEntity) { }

  /**
   * Handles receiving the packet on the correct thread
   * Packet is automatically set to handled as well by the base logic
   *
   * @param player
   */
  public abstract void handleThreadsafe(PlayerEntity player);

  @Override
  public void read(PacketByteBuf buf) throws IOException {
    encode(buf);
  }
}
