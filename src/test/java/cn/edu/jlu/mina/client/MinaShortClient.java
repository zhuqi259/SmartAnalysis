package cn.edu.jlu.mina.client;

import cn.edu.jlu.mina.codec.MyCodeFactory;
import cn.edu.jlu.mina.handler.MinaShortClientHandler;
import cn.edu.jlu.mina.model.MyMessage;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MinaShortClient {
    private static final int PORT = 1235;

    public static void main(String[] args) throws IOException, InterruptedException {
        IoConnector connector = new NioSocketConnector();
        connector.getSessionConfig().setReadBufferSize(2048);
        connector.getFilterChain().addLast("logger", new LoggingFilter("~~~MinaShortClient~~~"));
        //connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));
        connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new MyCodeFactory()));
        connector.setHandler(new MinaShortClientHandler());
        for (int i = 1; i <= 10; i++) {
            ConnectFuture future = connector.connect(new InetSocketAddress("127.0.0.1", PORT));
            future.awaitUninterruptibly();
            IoSession session = future.getSession();
            MyMessage msg = new MyMessage();
            session.write(msg);
            session.getCloseFuture().awaitUninterruptibly();
            System.out.println("result=" + session.getAttribute("result"));
            Thread.sleep(5000);
        }
        connector.dispose();
    }
}
