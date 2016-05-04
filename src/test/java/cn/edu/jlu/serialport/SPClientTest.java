package cn.edu.jlu.serialport;

class MyThread extends Thread {
    @Override
    public void run() {
        SPClientTest test = new SPClientTest("192.168.1.125", 8081);
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(1000);
                test.testOpenDevice_R();
                Thread.sleep(1100);
                test.testOpenDevice_r();
                Thread.sleep(1200);
                test.testCloseDevice_R();
                Thread.sleep(1300);
                test.testCloseDevice_r();
                Thread.sleep(1400);
                test.testQuerySingleState();
                Thread.sleep(1500);
                test.testQueryAllState();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

public class SPClientTest {
    private String ip;
    private int port;

    public SPClientTest(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void testOpenDevice_R() {
        String ret = SPClient.openDevice(ip, port, '1', '1', 'R');
        if ("-1".equals(ret)) {
            System.out.println("打开设备1(R)失败....");
        } else {
            System.out.println("打开设备1(R)成功....");
        }
    }

    public void testOpenDevice_r() {
        String ret = SPClient.openDevice(ip, port, '1', '2', 'r');
        if ("-1".equals(ret)) {
            System.out.println("打开设备2(r)失败....");
        } else {
            System.out.println("打开设备2(r)成功....");
        }
    }

    public void testCloseDevice_R() {
        String ret = SPClient.closeDevice(ip, port, '1', '3', 'R');
        if ("-1".equals(ret)) {
            System.out.println("关闭设备3(R)失败....");
        } else {
            System.out.println("关闭设备3(R)成功....");
        }
    }

    public void testCloseDevice_r() {
        String ret = SPClient.closeDevice(ip, port, '1', '4', 'r');
        if ("-1".equals(ret)) {
            System.out.println("关闭设备4(r)失败....");
        } else {
            System.out.println("关闭设备4(r)成功....");
        }
    }

    public void testQuerySingleState() {
        String ret = SPClient.querySingleState(ip, port, '1', '5');
        if ("-1".equals(ret)) {
            System.out.println("查询设备5失败....");
        } else {
            System.out.println("查询设备5成功.... => 状态: " + ret);
        }
    }

    public void testQueryAllState() {
        String ret = SPClient.queryAllState(ip, port, '1');
        if ("-1".equals(ret)) {
            System.out.println("查询所有设备失败....");
        } else {
            System.out.println("查询所有设备成功.... => 状态: " + ret);
        }
    }

    public static void main(String[] args) {
        new MyThread().start();
    }
}
