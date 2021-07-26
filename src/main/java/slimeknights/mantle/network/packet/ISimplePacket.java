package slimeknights.mantle.network.packet;

import java.io.IOException;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;

/**
 * Packet interface to add common methods for registration
 */
public abstract class ISimplePacket<T extends ISimplePacket> implements Packet<PacketListener> {

  public ISimplePacket(PacketByteBuf packetByteBuf) {};

  /**
   * Encodes a packet for the buffer
   *
   * @param buf Buffer instance
   */
  public abstract void encode(PacketByteBuf buf);

  /**
   * Handles receiving the packet
   *
   * @param playerEntity the packet sender
   */
  public abstract void handle(PlayerEntity playerEntity);

  @Override
  public void write(PacketByteBuf buf) throws IOException {
    encode(buf);
  }

  @Override
  public void apply(PacketListener listener) { }

}
