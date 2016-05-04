package cn.edu.jlu.mina.handler;

import cn.edu.jlu.mina.model.MyMessage;
import cn.edu.jlu.mina.model.SYSTEM;
import cn.edu.jlu.mina.util.ByteUtils;
import cn.edu.jlu.mina.util.Initialization;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;

public class MinaLongConnServerHandler extends IoHandlerAdapter {
    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());

    @Override
    public void sessionOpened(IoSession session) {
        InetSocketAddress remoteAddress = (InetSocketAddress) session.getRemoteAddress();
        String clientIp = remoteAddress.getAddress().getHostAddress();
        logger.info("LongConnect Server opened Session ID =" + String.valueOf(session.getId()));
        logger.info("接收来自客户端 :" + clientIp + "的连接.");
        Initialization init = Initialization.getInstance();
        Map<String, IoSession> clientMap = init.getClientMap();
        IoSession oldSession = clientMap.get(clientIp);
        if (oldSession != null && oldSession.isConnected()) {
            // 关闭相同ip的历史keep-alive
            oldSession.close(true);
        }
        clientMap.put(clientIp, session);
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        logger.info("Message received in the long connect server..");
        MyMessage msg = (MyMessage) message;
        if (message == null) {
            session.write(SYSTEM.ERROR);
            return;
        }
        byte[] fullData = msg.getMessage();
        String expression = ByteUtils.bytesToHexString(fullData);
        logger.info("Message is:" + expression);
        // 查看是否给谁发送消息
        if (fullData[6] == ByteUtils.OX_C9) {
            logger.info("登陆/心跳 => 集中器(keep-alive)");
            byte[] data = msg.getData();
            data[0] = ByteUtils.OX_0B;
            data[9] = ByteUtils.increase(data[9]);
            msg.setData(data);
            byte cs = ByteUtils.CS(data);
            msg.setCs(cs);
            logger.info("Return  is:" + ByteUtils.bytesToHexString(msg.getMessage()));
            session.write(msg);
        } else if (fullData[6] == ByteUtils.OX_A8) {
            logger.info("得到电表数据 => 普通客户(shortClient)");
            IoSession shortConnSession = (IoSession) session.getAttribute("shortConnSession");
            if (shortConnSession == null) {
                logger.info("ShortConnect Server Session ID  null");
            } else {
                logger.info("ShortConnect Server Session ID =" + String.valueOf(shortConnSession.getId()));
                // 给shortClient发消息
                // byte[] oldData = msg.getData();
                byte bLen = fullData[32]; // 转发数据的长度 = 8或7, 前四位 是复制的请求命令
                // 33,34,35,36 这四位代表hexCMD (读电量或功率)
                int len = ((int) bLen) - 4;
                byte[] data = new byte[len];
                System.arraycopy(fullData, 37, data, 0, len); // 也可以使用msg.getData();得到
                for (int i = 0; i < len; i++) {
                    data[i] -= ByteUtils.OX_33;
                }
                MyMessage sendData = new MyMessage();
                sendData.setLength(ByteUtils.getMyByteLength(len));
                sendData.setData(data);
                sendData.setCs(ByteUtils.CS(data));

                byte[] eclcData = sendData.getMessage();
                String result = ByteUtils.bytesToHexString(eclcData);
                logger.info("Client get data => " + result);
                shortConnSession.write(sendData);
            }
        } else if (fullData[6] == ByteUtils.OX_4A) {
            logger.info("转发读取电表数据 => 集中器(读取数据)");
            session.write(msg); // 直接转发即可
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
        logger.info("Disconnecting the idle.");
        // disconnect an idle client
        session.close(true);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        // close the connection on exceptional situation
        logger.warn(cause.getMessage(), cause);
        session.close(true);
    }
}
