package pucrs;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MulticastReceiver extends Thread {
    private static final Logger log = Logger.getLogger(MulticastReceiver.class.getName());
    private MulticastSocket socket;
    private InetAddress inetAddress;
    private final Slave slave;
    private final String group;
    private final int port;

    public MulticastReceiver(Slave slave, String group, int port) {
        this.slave = slave;
        this.group = group;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            this.socket = new MulticastSocket(port);
            this.inetAddress = InetAddress.getByName(group);
            this.socket.joinGroup(inetAddress);

            while (true) {
                byte[] in = new byte[256];
                var packet = new DatagramPacket(in, in.length);
//                this.socket.setSoTimeout(500);
                this.socket.receive(packet);
                var received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("SLAVE [" + this.slave.slave.getId() + "] - Received(Multicast): " + received);
                if (received.equals("1")) {
                    this.slave.sendTimeToMaster();
                }
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            Utils.closeMulticastConnection(this.socket, inetAddress);
        }
    }


}
