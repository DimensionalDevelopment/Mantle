package slimeknights.mantle.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.util.TriConsumer;
import slimeknights.mantle.network.packet.ISimplePacket;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * A small network implementation/wrapper using AbstractPackets instead of IMessages.
 * Instantiate in your mod class and register your packets accordingly.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class NetworkWrapper {
  private static final String PROTOCOL_VERSION = "1";
  private final Identifier identifier;

  Map<Integer, Packet> packetIdMap = new HashMap<>();
  Map<Class<?>, Packet> packetClassMap = new HashMap<>();

  private int id = 0;

  /**
   * Creates a new network wrapper
   */
  public NetworkWrapper(Identifier identifer) {
    this.identifier = identifer;
    ServerPlayNetworking.registerGlobalReceiver(identifier, (minecraftServer, serverPlayerEntity, serverPlayNetworkHandler, packetByteBuf, packetSender) -> {
      int id = packetByteBuf.readInt();

      Packet packet = packetIdMap.get(id);

      ISimplePacket object = packet.decoder.apply(packetByteBuf);

      packet.consumer.accept(object, serverPlayerEntity, packetSender);
    });

    ClientPlayNetworking.registerGlobalReceiver(identifier, (minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) -> {
      int id = packetByteBuf.readInt();

      Packet packet = packetIdMap.get(id);

      ISimplePacket object = packet.decoder.apply(packetByteBuf);

      packet.consumer.accept(object, minecraftClient.player, packetSender);
    });
  }

  public  <T extends ISimplePacket> void registerPacket(Class<T> clazz, Function<PacketByteBuf, T> decoder, NetworkSide side) {
    registerPacket(clazz, ISimplePacket::encode, decoder, ISimplePacket::handle, side);
  }

    /**
     * Registers a new generic packet
     * @param clazz      Packet class
     * @param encoder    Encodes a packet to the buffer
     * @param decoder    Packet decoder, typically the constructor
     * @param direction  Network direction for validation. Pass null for no direction
     */
  public <T extends ISimplePacket> void registerPacket(Class<T> clazz, BiConsumer<T, PacketByteBuf> encoder, Function<PacketByteBuf, T> decoder, TriConsumer<T, PlayerEntity, PacketSender> consumer, NetworkSide direction) {
    Packet packet = Packet.of(id, clazz, encoder, decoder, consumer, direction);

    packetIdMap.put(id, packet);
    packetClassMap.put(clazz, packet);
    id++;

  }


  /* Sending packets */

  /**
   * Sends a packet to the server
   * @param msg  Packet to send
   */
  public void sendToServer(ISimplePacket msg) {
    Class<?> clazz = msg.getClass();

    Packet packet = packetClassMap.get(clazz);

    if(packet == null || packet.direction == NetworkSide.CLIENTBOUND) return;

    PacketByteBuf packetByteBuf = PacketByteBufs.create();

    packetByteBuf.writeInt(id);

    packet.encoder.accept(msg, packetByteBuf);

    ClientPlayNetworking.send(identifier, packetByteBuf);
  }

  /**
   * Sends a packet to the given packet distributor
   * @param target   Packet target
   * @param msg  Packet to send
   */
  public void send(ServerPlayerEntity target, ISimplePacket msg) {
    Class<?> clazz = msg.getClass();

    Packet packet = packetClassMap.get(clazz);

    if(packet == null || packet.direction == NetworkSide.SERVERBOUND) return;

    PacketByteBuf packetByteBuf = PacketByteBufs.create();

    packetByteBuf.writeInt(id);

    packet.encoder.accept(msg, packetByteBuf);

    ClientPlayNetworking.send(identifier, packetByteBuf);

    ServerPlayNetworking.send(target, identifier, packetByteBuf);
  }

  /**
   * Sends a vanilla packet to the given entity
   * @param player  Player receiving the packet
   * @param packet  Packet
   */
  public void sendVanillaPacket(net.minecraft.network.Packet<?> packet, Entity player) {
    if (player instanceof ServerPlayerEntity && ((ServerPlayerEntity) player).networkHandler != null) {
      ((ServerPlayerEntity) player).networkHandler.sendPacket(packet);
    }
  }

  /**
   * Sends a packet to a player
   * @param msg     Packet
   * @param player  Player to send
   */
  public void sendTo(ISimplePacket msg, ServerPlayerEntity player) {
    send(player, msg);
  }

  /**
   * Sends a packet to players near a location
   * @param msg          Packet to send
   * @param serverWorld  World instance
   * @param position     Position within range
   */
  public void sendToClientsAround(ISimplePacket msg, ServerWorld serverWorld, BlockPos position) {
    for (ServerPlayerEntity playerEntity : PlayerLookup.around(serverWorld, position, 16)) {
      send(playerEntity, msg);
    }
  }

  static class Packet {
    NetworkSide direction;
    BiConsumer<ISimplePacket, PacketByteBuf> encoder;
    Function<PacketByteBuf, ISimplePacket> decoder;
    TriConsumer<ISimplePacket, PlayerEntity, PacketSender> consumer;
    Class<?> clazz;
    int id;

    public static <T extends ISimplePacket> Packet of(int id, Class<T> clazz, BiConsumer<T, PacketByteBuf> encoder, Function<PacketByteBuf, T> decoder, TriConsumer<T, PlayerEntity, PacketSender> consumer, NetworkSide side) {
      Packet packet = new Packet();
      packet.clazz = clazz;
      packet.encoder = (BiConsumer<ISimplePacket, PacketByteBuf>) encoder;
      packet.decoder = (Function<PacketByteBuf, ISimplePacket>) decoder;
      packet.consumer = (TriConsumer<ISimplePacket, PlayerEntity, PacketSender>) consumer;
      packet.direction = side;
      packet.id = id;
      return packet;
    }
  }
}
