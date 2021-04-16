package slimeknights.mantle.network;

import net.minecraft.network.NetworkSide;
import net.minecraft.util.Identifier;

import slimeknights.mantle.network.packet.UpdateSavedPagePacket;

public class MantleNetwork {
  /** Network instance */
  public static final NetworkWrapper INSTANCE = new NetworkWrapper(new Identifier("mantle:networking"));

  /**
   * Registers packets into this network
   */
  public static void registerPackets() {
    INSTANCE.registerPacket(UpdateSavedPagePacket.class, UpdateSavedPagePacket::encode, UpdateSavedPagePacket::new, UpdateSavedPagePacket::handleThreadsafe, NetworkSide.SERVERBOUND);
  }
}
