package cn.edu.jlu.mina;


import cn.edu.jlu.mina.server.MinaLongConnServer;
import cn.edu.jlu.mina.server.MinaShortConnServer;

public class ServerMain {
    public static void main(String[] args) {
        MinaLongConnServer minaLongConnServer = new MinaLongConnServer(8000);
        MinaShortConnServer minaShortConnServer = new MinaShortConnServer(1235);
        try {
            minaLongConnServer.start();
            minaShortConnServer.start();
        } catch (Exception ignored) {
        }
    }
}
