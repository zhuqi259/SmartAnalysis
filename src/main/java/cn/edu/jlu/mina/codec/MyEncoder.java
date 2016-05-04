package cn.edu.jlu.mina.codec;

import cn.edu.jlu.mina.model.MyMessage;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class MyEncoder implements ProtocolEncoder {
    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        MyMessage msg = (MyMessage) message;
        IoBuffer buffer = IoBuffer.allocate(1024).setAutoExpand(true);
        buffer.put(msg.getBegin());
        buffer.put(msg.getLength());
        buffer.put(msg.getLength());
        buffer.put(msg.getBegin());
        buffer.put(msg.getData());
        buffer.put(msg.getCs());
        buffer.put(msg.getEnd());
        buffer.flip();
        out.write(buffer);
    }

    @Override
    public void dispose(IoSession session) throws Exception {
    }
}
