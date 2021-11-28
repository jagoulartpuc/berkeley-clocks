package pucrs;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Master {
    private static final Logger log = Logger.getLogger(Master.class.getName());
    private final Configuration master;
    private final int totalSlaves;
    private final List<Configuration> slavesConfigurations;
    private final MulticastSender multicastSender;

    public Master(Configuration master, int totalSlaves, List<Configuration> slavesConfigurations) {
        this.master = master;
        this.totalSlaves = totalSlaves;
        this.slavesConfigurations = slavesConfigurations;
        this.multicastSender = new MulticastSender("233.0.0.1", 9000);
//        new pucrs.MulticastReceiver().start();

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(berkeleyTask(), 1, 15, TimeUnit.SECONDS);
    }

    public Runnable berkeleyTask() {
        return () -> {
            this.multicastSender.send("1");
            var times = this.receiveTimes();

            var newTimes = Berkeley.calculate(times);

            newTimes.forEach(time -> {
                var slaveToFix = this.slavesConfigurations.stream()
                        .filter(slave -> slave.getId() == time.getId())
                        .findFirst()
                        .orElseThrow();
                this.sendTimeToSlave(slaveToFix, time.getTime().toString());
            });

//            List<pucrs.Configuration> slavesToFix = this.slavesConfigurations.stream()
//                    .filter(slave -> times.stream().anyMatch(time -> time.getId() == slave.getId()))
//                    .collect(Collectors.toList());
//            slavesToFix.forEach(slave -> {
//                this.sendTimeToSlave(slave, newTime);
//            });
        };
    }

    private List<Configuration> receiveTimes() {
        ServerSocket server = null;
        Socket socket = null;
        List<Configuration> times = new ArrayList<>();
        try {
            server = new ServerSocket(this.master.getPort());
            for (int i = 0; i < this.totalSlaves; i++) {
                socket = server.accept();
                var inputStream = socket.getInputStream();
                var dataInputStream = new DataInputStream(inputStream);
                var res = dataInputStream.readUTF();
                System.out.println("MASTER - Received: " + res);
                var split = res.split("\\|");

                var id = Integer.parseInt(split[0]);
                var time = LocalTime.parse(split[1]);
                var slaveWithDelay = this.slavesConfigurations.stream()
                        .filter(slave -> slave.getId() == id)
                        .findFirst()
                        .orElseThrow();

                var slaveTime = new Configuration(id, time, slaveWithDelay.getDelay());
                times.add(slaveTime);
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            Utils.closeSocketConnection(server, socket);
        }
        return times;
    }

    private void sendTimeToSlave(Configuration slave, String time) {
        Socket socket = null;
        OutputStream outputStream = null;
        DataOutputStream dataOutputStream = null;
        try {
            socket = new Socket(slave.getHost(), slave.getPort());
            outputStream = socket.getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeUTF(time);
            System.out.println("MASTER - Send: " + time);
            dataOutputStream.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            Utils.closeSocketConnection(outputStream, dataOutputStream, socket);
        }
    }
}
