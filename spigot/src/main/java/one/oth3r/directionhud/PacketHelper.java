package one.oth3r.directionhud;

import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.utils.Player;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class PacketHelper {
    public static String getChannel(Assets.packets packetType) {
        return "directionhud:"+packetType.getIdentifier();
    }
    public static void sendPacket(Player player, String packetType, String data) {
        player.getPlayer().sendPluginMessage(DirectionHUD.plugin,packetType,getByteArray(data));
    }
    private static byte[] getByteArray(String str) {
        return ByteBuffer.wrap(str.getBytes(Charset.defaultCharset())).array();
    }
}
