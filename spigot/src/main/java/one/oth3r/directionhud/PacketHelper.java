package one.oth3r.directionhud;

import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.utils.Player;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class PacketHelper {
    public static String getChannel(Assets.packets packetType) {
        return "directionhud:"+packetType.getIdentifier();
    }
    public static void sendPacket(Player player, Assets.packets packetType, String data) {
        player.getPlayer().sendPluginMessage(DirectionHUD.plugin,getChannel(packetType),getByteArray(data));
    }
    private static byte[] getByteArray(String str) {
        return ByteBuffer.wrap(str.getBytes(StandardCharsets.UTF_8)).array();
    }
}
