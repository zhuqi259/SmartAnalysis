package cn.edu.jlu.mina.util;

import org.apache.mina.core.session.IoSession;

import java.util.HashMap;
import java.util.Map;

public class Initialization {
    private static Initialization instance;
    private Map<String, IoSession> clientMap;

    private Initialization() {
        init();
    }

    public static Initialization getInstance() {
        if (instance == null)
            instance = new Initialization();
        return instance;
    }

    public void init() {
        this.clientMap = new HashMap<>();
    }

    public Map<String, IoSession> getClientMap() {
        return clientMap;
    }

    public void setClientMap(Map<String, IoSession> clientMap) {
        this.clientMap = clientMap;
    }
}
