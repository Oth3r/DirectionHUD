package one.oth3r.directionhud;

import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.utils.Player;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class PacketHelper {
    public static String getChannel(Assets.packets packetType) {
        return "directionhud:"+packetType.getIdentifier();
    }

    public static void sendPacket(Player player, Assets.packets packetType, String data) {
        PacketByteBuffer buffer = new PacketByteBuffer();
        buffer.writeString(data);

        player.getPlayer().sendPluginMessage(DirectionHUD.getData().getPlugin(),getChannel(packetType),buffer.asByteArray());
    }

    /**
     * A utility class to allow for reading and writing of complex types to/from a byte array.
     * based on Veinminer's PluginMessageByteBuffer
     */
    public static class PacketByteBuffer {

        private final ByteArrayOutputStream outputStream;

        /**
         * constructor for writing packet data
         */
        public PacketByteBuffer() {
            this.outputStream = new ByteArrayOutputStream();
        }

        /**
         * Write a variable-length integer.
         *
         * @param value the value to write
         */
        public void writeInt(int value) {
            while (true) {
                if ((value & ~0x7F) == 0) {
                    this.writeByte(value);
                    return;
                }

                this.writeByte((value & 0x7F) | 0x80);
                value >>>= 7;
            }
        }


        /**
         * Write a UTF-8 String.
         *
         * @param string the string to write
         */
        public void writeString(String string) {
            this.writeByteArray(string.getBytes(StandardCharsets.UTF_8));
        }

        /**
         * Write an array of bytes.
         *
         * @param bytes the bytes to write
         */
        public void writeBytes(byte[] bytes) {
            this.outputStream.writeBytes(bytes);
        }

        /**
         * Write an array of bytes prefixed by a variable-length int.
         *
         * @param bytes the bytes to write
         */
        public void writeByteArray(byte[] bytes) {
            this.writeInt(bytes.length);
            this.writeBytes(bytes);
        }

        /**
         * Write a raw byte.
         *
         * @param value the value to write
         */
        public void writeByte(int value) {

            this.outputStream.write((byte) value);
        }

        /**
         * Get this byte buffer as a byte array (for writing).
         *
         * @return the byte array
         */
        public byte[] asByteArray() {
            return outputStream.toByteArray();
        }
    }
}
