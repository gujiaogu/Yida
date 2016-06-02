package com.yida.handset.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by gujiao on 2016/5/19.
 */
public class UDPUtil {

    private DatagramSocket socket;

    public byte[] send(byte[] buffer, byte[] cmd) {
        byte[] result = new byte[]{};
        try {
            socket = new DatagramSocket(9005);
            DatagramPacket packet = new DatagramPacket(cmd, cmd.length,
                    InetAddress.getByName("192.168.1.120"), 6090);
            socket.send(packet);
            result = displayReceiveInfo(buffer, socket);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public byte[] displayReceiveInfo(byte[] buffer, DatagramSocket socket)
            throws IOException {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

        byte data[] = packet.getData();
//        InetAddress address = packet.getAddress();
//        System.out.println("接收的文本:::" + new String(data));
//        System.out.println("接收的ip地址:::" + address.toString());
//        System.out.println("接收的端口::" + packet.getPort());
        return data;
    }

    public void close() {
        if (socket != null) {
            try {
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
