package cn.edu.jlu.mina.server;

import cn.edu.jlu.mina.codec.MyCodeFactory;
import cn.edu.jlu.mina.handler.MinaLongConnServerHandler;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * 长连接服务器
 */
public class MinaLongConnServer {
    private int port;

    public MinaLongConnServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        IoAcceptor acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("logger", new LoggingFilter("###MinaLongConnServer###"));
        //  acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MyCodeFactory()));
        acceptor.setHandler(new MinaLongConnServerHandler());
        acceptor.getSessionConfig().setReadBufferSize(2048);
        acceptor.bind(new InetSocketAddress(port));
        System.out.println("Long Listening on port " + port);
    }
}