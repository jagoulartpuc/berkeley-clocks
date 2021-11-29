package pucrs;

import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UnicastReceiver extends Thread {
    private static final Logger log = Logger.getLogger(UnicastReceiver.class.getName());
    private final Slave slave;
    private final Configuration slaveConfiguration;

    public UnicastReceiver(Slave slave) {
        this.slave = slave;
        this.slaveConfiguration = slave.slave;
    }

    @Override
    public void run() {
        ServerSocket server = null;
        Socket socket = null;
        try {
            server = new ServerSocket(this.slaveConfiguration.getPort());
            while (true) {
                socket = server.accept();
                var inputStream = socket.getInputStream();
                var dataInputStream = new DataInputStream(inputStream);
                var res = dataInputStream.readUTF();
                System.out.println("SLAVE[" + this.slaveConfiguration.getId() + "] - Received(Unicast): " + res);
                var time = LocalTime.parse(res);
                this.slave.setTime(time);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            Utils.closeSocketConnection(server, socket);
        }
    }
}
