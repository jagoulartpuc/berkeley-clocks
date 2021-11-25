package pucrs;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.time.LocalTime;

public interface Time extends Remote {

	LocalTime getTime() throws RemoteException;

	void fixTime(LocalTime clientTime, long nanos) throws RemoteException;
}