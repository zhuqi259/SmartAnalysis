package cn.edu.jlu.mina.codec;

import cn.edu.jlu.mina.model.MyMessage;
import cn.edu.jlu.mina.util.ByteUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class MyDecoder extends CumulativeProtocolDecoder {

    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        byte begin = in.get();
        byte[] length = new byte[2];
        in.get(length);
        byte[] length2 = new byte[2];
        in.get(length2);
        byte begin2 = in.get();
        int len = ByteUtils.getMyLength(length);
        // System.out.println("$$$$$ len => " + len);
        byte[] data = new byte[len];
        in.get(data);
        byte cs = in.get();
        byte end = in.get();
        MyMessage msg = new MyMessage();
        msg.setBegin(begin);
        msg.setLength(length);
        msg.setData(data);
        msg.setCs(cs);
        msg.setEnd(end);
        out.write(msg);
        return false;
    }
}
