package cn.edu.jlu.mina.util;

public class ByteUtilsTest {
    public static void main(String[] args) {
        String str = "B600";
        byte[] b = ByteUtils.hexStringToBytes(str);
        ByteUtils.printHexString(b);
        System.out.println();
        String str2 = ByteUtils.bytesToHexString(b);
        System.out.println(str2);
        byte[] bint = new byte[4];
        bint[0] = 0;
        bint[1] = 0;
        bint[2] = b[1];
        bint[3] = b[0];
        int x = ByteUtils.byteArrayToInt(bint, 0);
        System.out.println(x);
        System.out.println(ByteUtils.getMyLength(b));
        byte[] bbb = ByteUtils.hexStringToBytes("0000");
        ByteUtils.printHexString(bbb);
        System.out.println();
        byte[] test = ByteUtils.getMyByteLength(44);
        String test2 = ByteUtils.bytesToHexString(test);
        System.out.println(test2);
    }
}
