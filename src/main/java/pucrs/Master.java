package pucrs;


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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
				System.out.println("Horario Local: " + DateTimeFormatter.ofPattern("HH:mm:ss").format(localTime));

				Registry registry1 = LocateRegistry.getRegistry(ips[0], 1200);
				Time hs1 = (Time) registry1.lookup(TimeImpl.class.getSimpleName());
				System.out.println("Conex�o com Servidor 1 estabelecida com sucesso.");
				LocalTime horarioServidor1 = hs1.getTime();
				times.add(horarioServidor1);
				System.out.println("Horario Servidor 1: " + DateTimeFormatter.ofPattern("HH:mm:ss").format(horarioServidor1));

				Registry registry2 = LocateRegistry.getRegistry(ips[1], 1201);
				Time hs2 = (Time) registry2.lookup(TimeImpl.class.getSimpleName());
				System.out.println("Conexao com Servidor 2 estabelecida com sucesso.");
				LocalTime horarioServidor2 = hs2.getTime();
				times.add(horarioServidor2);
				System.out.println("Horario Servidor 2: " + DateTimeFormatter.ofPattern("HH:mm:ss").format(horarioServidor2));

				Registry registry3 = LocateRegistry.getRegistry(ips[2], 1202);
				Time hs3 = (Time) registry3.lookup(TimeImpl.class.getSimpleName());
				System.out.println("Conexao com Servidor 3 estabelecida com sucesso.");
				LocalTime horarioServidor3 = hs3.getTime();
				times.add(horarioServidor3);
				System.out.println("Horario Servidor 3: " + DateTimeFormatter.ofPattern("HH:mm:ss").format(horarioServidor3));

				var nanoLocal = localTime.toNanoOfDay();
				var diffSerber1 = horarioServidor1.toNanoOfDay() - nanoLocal;
				var diffSerber2 = horarioServidor2.toNanoOfDay() - nanoLocal;
				var diffSerber3 = horarioServidor3.toNanoOfDay() - nanoLocal;
				var avgDiff = (diffSerber1 + diffSerber2 + diffSerber3) / 3;

				// Atribuir Data Nova
				hs1.fixTime(localTime, avgDiff);
				hs2.fixTime(localTime, avgDiff);
				hs3.fixTime(localTime, avgDiff);
				localTime = localTime.plusNanos(avgDiff);
				//localTime = horarioNovo;
				System.out.println("Horarios atualizados");

				// Verificar horario em todas as inst�ncias
				System.out.println("Horario Local: " + DateTimeFormatter.ofPattern("HH:mm:ss").format(localTime));
				System.out.println("Horario Servidor 1: " + DateTimeFormatter.ofPattern("HH:mm:ss").format(hs1.getTime()));
				System.out.println("Horario Servidor 2: " + DateTimeFormatter.ofPattern("HH:mm:ss").format(hs2.getTime()));
				System.out.println("Horario Servidor 3: " + DateTimeFormatter.ofPattern("HH:mm:ss").format(hs3.getTime()));
			} catch (Exception ex) {
				System.out.println(ex);
			}
		};
	}

}