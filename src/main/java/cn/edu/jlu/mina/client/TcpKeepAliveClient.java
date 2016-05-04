package cn.edu.jlu.mina.client;

import cn.edu.jlu.mina.util.ByteUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 模拟集中控制器的 Keep-Alive
 */
public class TcpKeepAliveClient {
    private String ip;
    private int port;
    private static Socket socket = null;

    public TcpKeepAliveClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    private void start() {
        try {
            if (socket == null || socket.isClosed() || !socket.isConnected()) {
                socket = new Socket();
                InetSocketAddress endpoint = new InetSocketAddress(ip, port);
                int timeout = 60 * 1000;
                socket.connect(endpoint, timeout);
                socket.setSoTimeout(timeout);
                System.out.println("TcpKeepAliveClient created ~~~");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("TcpClient socket create error");
        }
    }

    private void heart() {
        Runnable runnable = new Runnable() {
            public void run() {
                if (socket != null && socket.isConnected()) {
                    // 68 3A 00 3A 00 68 C9 02 22 60 27 35 22 00 02 70 00 00 01 00 3E 16
                    String str = "683A003A0068C9022260273522000270000001003E16";
                    byte[] msg = ByteUtils.hexStringToBytes(str);
                    OutputStream output;
                    try {
                        output = socket.getOutputStream();
                        String result = ByteUtils.bytesToHexString(msg);
                        System.out.println("TcpKeepAliveClient send heart data :" + result);
                        output.write(msg, 0, msg.length);
                        output.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("TcpClient socket heart error");
                    }
                }
            }
        };
        ScheduledExecutorService service = Executors
                .newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        service.scheduleAtFixedRate(runnable, 10, 60, TimeUnit.SECONDS);
    }

    public void receiveAndSend() throws IOException {
        InputStream input;
        OutputStream output;
        try {
            start();
            heart();
            input = socket.getInputStream();
            output = socket.getOutputStream();
            // read body
            byte[] receiveBytes;// 收到的包字节数组
            while (true) {
                if (input.available() > 0) {
                    receiveBytes = new byte[input.available()];
                    input.read(receiveBytes);
                    // String result = new String(receiveBytes);
                    String result = ByteUtils.bytesToHexString(receiveBytes);
                    System.out.println("TcpKeepAliveClient received   data :" + result);
                    // System.out.println(result + ", " + len);
                    if (receiveBytes[6] == ByteUtils.OX_0B) {
                        // 心跳反馈数据，不管了
                    } else if (receiveBytes[6] == ByteUtils.OX_4A) {
                        // 读取电表数据(模拟 返回数据)
                        // 查看转发数据中DATA到底是 读电量/功率
                        byte[] cmd4 = new byte[4];
                        System.arraycopy(receiveBytes, 36, cmd4, 0, 4);
                        String hexCMD = ByteUtils.bytesToHexString(cmd4);
                        String str = "68B600B60068A80222235235220410E5000001001F14006829012906122068910833333433456789AB8D16D805054558132605B216";
                        if ("33333333".equals(hexCMD)) {
                            str = "68B600B60068A80222235235220410E5000001001F14006829012906122068910833333433456789AB8D16D805054558132605B216";
                        } else if ("33333635".equals(hexCMD)) {
                            str = "68B200B20068A80222235235220410E5000001001F140068290129061220689107333336354567898D16D805054558132605B216";
                        }
                        // send
                        byte[] msg = ByteUtils.hexStringToBytes(str);
                        System.out.println("TcpKeepAliveClient send elec  data :" + str);
                        output.write(msg, 0, msg.length);
                        output.flush();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("TcpClient socket error");
        }
    }

//    public static void main(String[] args) throws Exception {
//        TcpKeepAliveClient client = new TcpKeepAliveClient("127.0.0.1", 8000);
//        client.receiveAndSend();
//    }
}
