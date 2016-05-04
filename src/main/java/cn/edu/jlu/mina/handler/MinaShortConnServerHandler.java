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
import java.util.Iterator;
import java.util.Map;

public class MinaShortConnServerHandler extends IoHandlerAdapter {
    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());

    @Override
    public void sessionOpened(IoSession session) {
        InetSocketAddress remoteAddress = (InetSocketAddress) session.getRemoteAddress();
        logger.info(remoteAddress.getAddress().getHostAddress());
        logger.info(String.valueOf(remoteAddress.getPort()));
        logger.info(String.valueOf(session.getId()));
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        InetSocketAddress remoteAddress = (InetSocketAddress) session.getRemoteAddress();
        String remoteIp = remoteAddress.getAddress().getHostAddress();
        logger.info("Message received in the short connect server... from " + remoteIp);
        MyMessage msg = (MyMessage) message;
        if (message == null) {
            session.write(SYSTEM.ERROR);
            return;
        }
        byte[] data = msg.getMessage();
        String expression = ByteUtils.bytesToHexString(data);
        logger.info("Message is:" + expression);
        Initialization init = Initialization.getInstance();
        Map<String, IoSession> clientMap = init.getClientMap();
        if (clientMap == null || clientMap.size() == 0) {
            //   session.write("error");
            session.write(SYSTEM.ERROR);
        } else {
            logger.info("ShortConnect Server Session ID :" + String.valueOf(session.getId()));
            IoSession longConnSession = null;
            Iterator<String> iterator = clientMap.keySet().iterator();
            String key;
            while (iterator.hasNext()) {
                key = iterator.next();
                longConnSession = clientMap.get(key);
            }
            if (longConnSession != null) {
                logger.info("LongConnect Server Session ID :" + String.valueOf(longConnSession.getId()));
                longConnSession.setAttribute("shortConnSession", session);
                // 转发给集中器即可
                longConnSession.write(msg);
            }
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
