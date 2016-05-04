package cn.edu.jlu.mina.server;

import cn.edu.jlu.mina.codec.MyCodeFactory;
import cn.edu.jlu.mina.handler.MinaShortConnServerHandler;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MinaShortConnServer {
    private int port;

    public MinaShortConnServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        IoAcceptor acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("logger", new LoggingFilter("###MinaShortConnServer###"));
        //acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MyCodeFactory()));
        acceptor.setHandler(new MinaShortConnServerHandler());
        acceptor.getSessionConfig().setReadBufferSize(2048);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 30);
        acceptor.bind(new InetSocketAddress(port));
        System.out.println("Short Listening on port " + port);
    }
}