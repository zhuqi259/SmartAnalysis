package cn.edu.jlu.mina.util;

public class ByteUtils {
    public static final byte OX_C9 = (byte) 0xC9;
    public static final byte OX_0B = (byte) 0x0B;
    public static final byte OX_A8 = (byte) 0xA8;
    public static final byte OX_4A = (byte) 0x4A;
    public static final byte OX_33 = (byte) 0x33;
    public static final byte BEGIN = (byte) 0x68;
    public static final byte END = (byte) 0x16;
    public static final byte[] ZERO_DATA = new byte[0];

    /**
     * Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
     *
     * @param src byte[] data
     * @return hex string
     */
    public static String bytesToHexString(byte[] src) {
        StringBuffer sb = new StringBuffer("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                sb.append(0);
            }
            sb.append(hv);
        }
        return sb.toString().toUpperCase();
    }

    /**
     * Convert hex string to byte[]
     *
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Convert char to byte
     *
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    //将指定byte数组以16进制的形式打印到控制台
    public static void printHexString(byte[] b) {
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            System.out.print(hex.toUpperCase());
        }
    }

    public static int byteArrayToInt(byte[] b, int offset) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = (4 - 1 - i) * 8;
            value += (b[i + offset] & 0x000000FF) << shift;
        }
        return value;
    }

    private static byte[] intToByteArray(final int integer) {
        int byteNum = (40 - Integer.numberOfLeadingZeros(integer < 0 ? ~integer : integer)) / 8;
        byte[] byteArray = new byte[4];
        for (int n = 0; n < byteNum; n++)
            byteArray[3 - n] = (byte) (integer >>> (n * 8));
        return (byteArray);
    }

    public static int getMyLength(byte[] b) {
        byte[] bs = new byte[4];
        bs[0] = 0;
        bs[1] = 0;
        bs[2] = b[1];
        bs[3] = b[0];
        return byteArrayToInt(bs, 0) >> 2;
    }

    public static byte[] getMyByteLength(int length) {
        byte[] b = new byte[2];
        int len = (length << 2) + 2;
        byte[] b4 = intToByteArray(len);
        b[0] = b4[3];
        b[1] = b4[2];
        return b;
    }

    public static byte CS(byte[] memorySpace) {
        int num = 0;
        for (int i = 0; i < memorySpace.length; i++) {
            num = (num + memorySpace[i]) % 256;
        }
        return (byte) num;
    }

    public static byte increase(byte b) {
        return (byte) (((byte) 0x6 << 4) + (b % 16));
    }

    public static byte increase2(int b) {
        return (byte) (((byte) 0xE << 4) + (b % 16));
    }
}
