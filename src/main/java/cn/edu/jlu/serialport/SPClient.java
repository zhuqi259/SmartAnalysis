package cn.edu.jlu.serialport;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class SPClient {

    private static String convertResponse(byte[] receiveBytes) {
        String response = Utils.bytes2ASCII(receiveBytes);
        System.out.println("response => " + response);
        String realRet = Utils.ERROR;
        if (response != null) {
            switch (response.charAt(3)) {
                case 'R':
                    String[] arr1 = {response.charAt(2) + "", response.charAt(5) + ""};
                    realRet = Utils.join(arr1, "|");
                    String x = response.substring(7);
                    int index = x.indexOf("\"");
                    x = x.substring(0, index);
                    if ("ON".equals(x)) {
                        realRet += "|1";
                    } else {
                        realRet += "|0";
                    }
                    break;
                case 'A':
                    String[] arr2 = {response.charAt(2) + "", response.charAt(3) + "", response.substring(5, 5+8)};
                    realRet = Utils.join(arr2, "|");
                    break;
            }
        } else {
            return Utils.ERROR;
        }
        return realRet;
    }

    /**
     * 控制接口
     *
     * @param ip   服务器IP地址
     * @param port 服务器端口
     * @param m_id 设备编号 0~F, 其中0表示广播
     * @param c_id 端口号 0~8, 其中0表示对所有端口有效
     * @param ctrl 指令 0/1/2, 0=>打开,1=>关闭,2=>查询
     * @param type 端口类型 R/r/A R=>有返回值， r没有返回值，A查询所有状态
     * @return TCP服务器返回值
     */
    private static String tcp_control(String ip, Integer port, char m_id,
                                      char c_id, char ctrl, char type) {
        // 1. 拼接命令
        StringBuffer sb = new StringBuffer("(");
        if (m_id == '0') {
            sb.append("FF");
        } else {
            sb.append("0").append(m_id);
        }
        sb.append(type);
        sb.append("0").append(c_id);
        switch (ctrl) {
            case '0':
                sb.append("ON");
                break;
            case '1':
                sb.append("OF");
                break;
            case '2':
                sb.append("RQ");
                break;
        }
        sb.append(")");
        boolean bReceive = true;
        if (m_id == '0' || type == 'r') {
            bReceive = false; // 不需要读取数据
        }
        byte[] cmd = Utils.ASCII2bytes(sb.toString());
        if (cmd == null) {
            return Utils.ERROR;
        }
        String ret = Utils.SUCCESS;
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
            while (bReceive) {
                if (ips.available() > 0) {
                    receiveBytes = new byte[ips.available()];
                    ips.read(receiveBytes);
                    ret = convertResponse(receiveBytes);
                    break;
                }
            }
            ips.close();
            ops.close();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            return Utils.ERROR;
        } catch (Exception e) {
            e.printStackTrace();
            return Utils.ERROR;
        } finally {
            try {
                s.close();
            } catch (Exception e) {
                e.printStackTrace();
                ret = Utils.ERROR;
            }
        }
        // 2. 返回串口服务器 返回值
        return ret;
    }


    /**
     * 打开设备端口
     *
     * @param ip   服务器IP地址
     * @param port 服务器端口
     * @param m_id 设备编号 0~F
     * @param c_id 端口号 0~8
     * @param type 端口类型 R/r
     * @return "0" : 成功 "-1"：失败
     */
    public static String openDevice(String ip, Integer port, char m_id,
                                    char c_id, char type) {
        return tcp_control(ip, port, m_id, c_id, '0', type);
    }

    /**
     * 关闭设备端口
     *
     * @param ip   服务器IP地址
     * @param port 服务器端口
     * @param m_id 设备编号 0~F
     * @param c_id 端口号 0~8
     * @param type 端口类型 R/r
     * @return "0" : 成功 "-1"：失败
     */
    public static String closeDevice(String ip, Integer port, char m_id,
                                     char c_id, char type) {
        return tcp_control(ip, port, m_id, c_id, '1', type);
    }

    /**
     * 查询单一设备端口状态
     *
     * @param ip   服务器IP地址
     * @param port 服务器端口
     * @param m_id 设备编号 1~F
     * @param c_id 端口号 1~8
     * @return "-1"：失败 "1" : 打开 "0" ：关闭
     */
    public static String querySingleState(String ip, Integer port, char m_id,
                                          char c_id) {
        String ret = tcp_control(ip, port, m_id, c_id, '2', 'R');
        if (Utils.ERROR.equals(ret)) {
            return Utils.ERROR;
        } else {
            char ch = ret.charAt(ret.length() - 1);
            return (ch - '0') + "";
        }
    }

    /**
     * 查询所有设备端口状态
     *
     * @param ip   服务器IP地址
     * @param port 服务器端口
     * @param m_id 设备编号 1~F
     * @return "-1"：失败  "8位字符串(1 : 打开 0 ：关闭 )"<br>
     * <font color="red"><b>【警告】</b></font> String 判断相等使用equals,不能使用==
     */
    public static String queryAllState(String ip, Integer port, char m_id) {
        String ret = tcp_control(ip, port, m_id, '0', '2', 'A');
        if (Utils.ERROR.equals(ret)) {
            return Utils.ERROR;
        } else {
            int index = ret.lastIndexOf("|") + 1;
            return ret.substring(index);
        }
    }

}
