package cn.edu.jlu.mina.handler;

import cn.edu.jlu.mina.model.MyMessage;
import cn.edu.jlu.mina.util.ByteUtils;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinaShortClientHandler extends IoHandlerAdapter {
    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());

    public MinaShortClientHandler() {
    }

    @Override
    public void sessionOpened(IoSession session) {
    }

    @Override
    public void messageReceived(IoSession session, Object message) {
        logger.info("Message received in the client..");
        MyMessage msg = (MyMessage) message;
        byte[] data = msg.getMessage();
        String expression = ByteUtils.bytesToHexString(data);
        logger.info("Message is:" + expression);
        session.setAttribute("result", expression);
        session.close(true);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        session.close(true);
    }
}
