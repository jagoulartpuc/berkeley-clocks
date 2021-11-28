package pucrs;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {
    private static final Logger log = Logger.getLogger(Utils.class.getName());

    public static void closeSocketConnection(ServerSocket server, Socket socket) {
        try {
            if (Objects.nonNull(server)) server.close();
            if (Objects.nonNull(socket)) socket.close();
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public static void closeSocketConnection(OutputStream outputStream, DataOutputStream dataOutputStream, Socket socket) {
        try {
            if (Objects.nonNull(outputStream)) outputStream.close();
            if (Objects.nonNull(dataOutputStream)) dataOutputStream.close();
            if (Objects.nonNull(socket)) socket.close();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public static void closeMulticastConnection(MulticastSocket socket, InetAddress inetAddress) {
        try {
            if (Objects.nonNull(socket)) {
                socket.leaveGroup(inetAddress);
                socket.close();
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
