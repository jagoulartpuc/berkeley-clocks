package pucrs;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalTime;

public class Slave {

	public static void main(String[] args) {
		int port = Integer.parseInt(args[0]);
		try {
			Time time = new TimeImpl(LocalTime.now());
			Registry registry = LocateRegistry.createRegistry(port);
			registry.rebind(TimeImpl.class.getSimpleName(), time);
			System.out.printf("Slave iniciado na porta %s%n", port);
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

}
