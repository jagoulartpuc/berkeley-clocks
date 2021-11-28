package pucrs;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MulticastSender {
    private static final Logger log = Logger.getLogger(MulticastSender.class.getName());
    private final String group;
    private final int port;

    public MulticastSender(String group, int port) {
        this.group = group;
        this.port = port;
    }

    public void send(String message) {
        try {
            byte[] out;
            out = message.getBytes();
            var socket = new DatagramSocket();
            var inetAddress = InetAddress.getByName(this.group);
            var packet = new DatagramPacket(out, out.length, inetAddress, this.port);
            socket.send(packet);
            socket.close();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
