package pucrs;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeImpl extends UnicastRemoteObject implements Time {

	private static final long serialVersionUID = -6810169856453308607L;

	private LocalTime time;

	public TimeImpl(LocalTime time) throws RemoteException {
		this.time = time;
	}

	@Override
	public LocalTime getTime() throws RemoteException {
		return time;
	}

	@Override
	public void fixTime(LocalTime horaClient, long diffNanos) throws RemoteException {
		long timeAsNanos = horaClient.toNanoOfDay();
		long thisNanos = this.getTime().toNanoOfDay();
		var newNanos = thisNanos - timeAsNanos;
		newNanos = newNanos * -1 + diffNanos + thisNanos;
		LocalTime newLocalTime = LocalTime.ofNanoOfDay(newNanos);
		System.out.println("Time updated: " + newLocalTime);
		this.time = newLocalTime;
	}

}