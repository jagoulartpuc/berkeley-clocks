package pucrs;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLOutput;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Master {

	public static void main(String[] args) {
		String[] ips = args[0].split(",");
		ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(berkeleyTask(ips), 1, 5, TimeUnit.SECONDS);
	}

	public static Runnable berkeleyTask(String[] ips) {
		return () -> {
			try {
				var times = new ArrayList<LocalTime>();

				LocalTime localTime = LocalTime.now();
				System.out.println(localTime);
				times.add(localTime);
				System.out.println("Time at master: " + localTime);

				Registry registry1 = LocateRegistry.getRegistry("localhost", 1200);
				Time time1 = (Time) registry1.lookup(TimeImpl.class.getSimpleName());
				LocalTime timeAtSlave1 = time1.getTime();
				times.add(timeAtSlave1);
				System.out.println("Connected! Time at slave 1: " +timeAtSlave1);

				Registry registry2 = LocateRegistry.getRegistry("localhost", 1201);
				Time time2 = (Time) registry2.lookup(TimeImpl.class.getSimpleName());
				LocalTime timeAtSlave2 = time2.getTime();
				times.add(timeAtSlave2);
				System.out.println("Connected! Time at slave 2: " +timeAtSlave2);

				Registry registry3 = LocateRegistry.getRegistry("localhost", 1202);
				Time time3 = (Time) registry3.lookup(TimeImpl.class.getSimpleName());
				LocalTime timeAtSlave3 = time3.getTime();
				times.add(timeAtSlave3);
				System.out.println("Connected! Time at slave 3: " + timeAtSlave3);

				var nanoLocal = localTime.toNanoOfDay();
				var diff1 = timeAtSlave1.toNanoOfDay() - nanoLocal;
				var diff2 = timeAtSlave2.toNanoOfDay() - nanoLocal;
				var diff3 = timeAtSlave3.toNanoOfDay() - nanoLocal;
				var avgDiff = (diff1 + diff2 + diff3) / 3;

				time1.fixTime(localTime, avgDiff);
				time2.fixTime(localTime, avgDiff);
				time3.fixTime(localTime, avgDiff);
				localTime = localTime.plusNanos(avgDiff);
				System.out.println("Times updated");

				System.out.println("Horario Local: " + localTime);
				System.out.println("Horario Servidor 1: " + time1.getTime());
				System.out.println("Horario Servidor 2: " + time2.getTime());
				System.out.println("Horario Servidor 3: " + time3.getTime());
			} catch (Exception ex) {
				System.out.println(ex);
			}
		};
	}

}