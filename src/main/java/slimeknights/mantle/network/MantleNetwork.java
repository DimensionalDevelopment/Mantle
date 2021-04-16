package slimeknights.mantle.network;

import net.minecraft.network.NetworkSide;

import slimeknights.mantle.network.packet.UpdateSavedPagePacket;

public class MantleNetwork {
  /** Network instance */
  public static final NetworkWrapper INSTANCE = new NetworkWrapper();

  /**
   * Registers packets into this network
   */
  public static void registerPackets() {
    INSTANCE.registerPacket(UpdateSavedPagePacket.class, UpdateSavedPagePacket::encode, UpdateSavedPagePacket::new, UpdateSavedPagePacket::handleThreadsafe, NetworkSide.SERVERBOUND);
  }
}
