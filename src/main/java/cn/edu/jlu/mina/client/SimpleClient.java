package cn.edu.jlu.mina.client;

import cn.edu.jlu.mina.model.MyMessage;
import cn.edu.jlu.mina.model.SYSTEM;
import cn.edu.jlu.mina.util.ByteUtils;
import cn.edu.jlu.mina.util.StringUtils;
import org.joda.time.DateTime;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public enum SimpleClient {
    INSTANCE; // 唯一实例
    private final String ELECTRONIC[] = {"33333333", "33333635"};
    private int PFC = 0;

    private String sendCMD(String ip, Integer port, byte[] cmd) {
        String ret = "";
        Socket s = new Socket();
        try {
            s.connect(new InetSocketAddress(InetAddress.getByName(ip), port),
                    60000);
            InputStream ips = s.getInputStream();
            OutputStream ops = s.getOutputStream();
            ops.write(cmd, 0, cmd.length);
            ops.flush();
            s.setSoTimeout(60000);
            byte[] receiveBytes;
            while (true) {
                if (ips.available() > 0) {
                    receiveBytes = new byte[ips.available()];
                    ips.read(receiveBytes);
                    ret = ByteUtils.bytesToHexString(receiveBytes);
                    break;
                }
            }
            ips.close();
            ops.close();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            return "error";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        } finally {
            try {
                s.close();
            } catch (Exception e) {
                e.printStackTrace();
                ret = "error";
            }
        }
        return ret;
    }

    private String readElectricity(String ip, Integer port, String control, String meter, int index) {
        MyMessage msg = new MyMessage();
        byte[] length = ByteUtils.hexStringToBytes("EA00");
        msg.setLength(length);
        byte[] data = new byte[58];
        data[0] = (byte) 0x4A;
        // control
        byte[] bControl = ByteUtils.hexStringToBytes(control);
        System.arraycopy(bControl, 0, data, 1, 6);

        data[7] = (byte) 0x04;
        data[8] = (byte) 0x10;
        data[9] = ByteUtils.increase2(PFC);
        data[10] = (byte) 0x00;
        data[11] = (byte) 0x00;
        data[12] = (byte) 0x01;
        data[13] = (byte) 0x00;

        data[14] = (byte) 0x1F;
        data[15] = (byte) 0x6B;
        data[16] = (byte) 0xBC;
        data[17] = (byte) 0x0A;
        data[18] = (byte) 0x10;
        data[19] = (byte) 0x00;

        data[20] = (byte) 0x68;

        byte[] bMeter = ByteUtils.hexStringToBytes(meter);
        System.arraycopy(bMeter, 0, data, 21, 6);

        data[27] = (byte) 0x68;
        data[28] = (byte) 0x11;
        data[29] = (byte) 0x04;

        String type = ELECTRONIC[index];
        byte[] bType = ByteUtils.hexStringToBytes(type);
        System.arraycopy(bType, 0, data, 30, 4);

//        data[30] = 0x33;
//        data[31] = 0x33;
//        data[32] = 0x33; // -0x33,  0x33 组合有功，0x34 正向有功， 0x35 反向有功
//        data[33] = 0x33;

        byte[] rv = new byte[14];
        System.arraycopy(data, 20, rv, 0, 14);

        data[34] = ByteUtils.CS(rv);
        data[35] = (byte) 0x16;
        for (int i = 0; i < 16; i++) {
            data[i + 36] = (byte) 0x00;
        }
        data[52] = (byte) PFC;
        PFC = (PFC + 1) % 256;

        DateTime now = DateTime.now();
        int sec = now.getSecondOfMinute();
        int min = now.getMinuteOfHour();
        int hour = now.getHourOfDay();
        int day = now.getDayOfMonth();
        // System.out.println(day + " ," + hour + " ," + min + " ," + sec);

        data[53] = ByteUtils.hexStringToBytes(String.format("%02d", sec))[0];
        data[54] = ByteUtils.hexStringToBytes(String.format("%02d", min))[0];
        data[55] = ByteUtils.hexStringToBytes(String.format("%02d", hour))[0];
        data[56] = ByteUtils.hexStringToBytes(String.format("%02d", day))[0];
        data[57] = (byte) 0x05;

        msg.setData(data);
        msg.setCs(ByteUtils.CS(data));


        byte[] cmd = msg.getMessage();
        //              "68EA00EA00684A0222235235220410E5000001001F6BBC0A100068290129061220681104333334333D16000000000000000000000000000000000545581326050416"     V
        // String str = "68EA00EA00684A0222761061040410E0000001001F6BBC0A100068696999010000681104333334332116000000000000000000000000000000000034262011059A16"; // V
        //  byte[] cmd = ByteUtils.hexStringToBytes(str);
        String ret = sendCMD(ip, port, cmd);
        String error = ByteUtils.bytesToHexString(SYSTEM.ERROR.getMessage());
        if ("error".equals(ret) || error == null || error.equals(ret)) {
            return "error";
        } else {
            switch (index) {
                case 0:
                    // 681200120068123456781416
                    //  System.out.println("0 ==> " + ret);
                    ret = ret.substring(12, 20);
                    //  System.out.println("0 ==> " + ret);
                    ret = ret.substring(6, 8) + ret.substring(4, 6) + ret.substring(2, 4) + "." + ret.substring(0, 2);
                    //  System.out.println("0 ==> " + ret);
                    break;
                case 1:
                    // 68XXXXXXXX68123456XX16
                    //  System.out.println("1 ==> " + ret);
                    ret = ret.substring(12, 18);
                    //  System.out.println("1 ==> " + ret);
                    ret = ret.substring(4, 6) + "." + ret.substring(2, 4) + ret.substring(0, 2);
                    //   System.out.println("1 ==> " + ret);
                    break;
            }
            ret = StringUtils.trimStart(ret, "0");
            if (ret.startsWith(".")) {
                ret = "0" + ret;
            }
            return ret;
        }
    }

//    public String readElectricityMeter2(String ip, Integer port) {
//        String str = "68EA00EA00684A0222761061040410E0000001001F6BBC0A100068696999010000681104333334332116000000000000000000000000000000000034262011059A16";
//        byte[] cmd = ByteUtils.hexStringToBytes(str);
//        String ret = sendCMD(ip, port, cmd);
//        if ("-2".equals(ret)) {
//            return "-2";
//        } else if ("-1".equals(ret)) {
//            return "-1";
//        } else {
//            return ret;
//        }
//    }

    public String readElectricityMeter(String ip, Integer port, String control, String meter) {
        return readElectricity(ip, port, control, meter, 0);
    }

    public String readElectricityPower(String ip, Integer port, String control, String meter) {
        // 02 03 00 00 => 00 00 03 02 => 33 33 36 35
        return readElectricity(ip, port, control, meter, 1);
    }
}
