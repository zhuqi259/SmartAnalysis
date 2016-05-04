package cn.edu.jlu.mina.test;

import cn.edu.jlu.mina.client.SimpleClient;

public class ClientTest {
    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new Thread() {
                @Override
                public void run() {
                    String result = SimpleClient.INSTANCE.readElectricityMeter("localhost", 1235, "022276106104", "696999010000");
                    System.out.println("电量 =>" + result);
                    try {
                        Thread.sleep(1234);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    result = SimpleClient.INSTANCE.readElectricityPower("localhost", 1235, "022276106104", "696999010000");
                    System.out.println("功率 =>" + result);
                }
            }.start();
        }
    }
}
