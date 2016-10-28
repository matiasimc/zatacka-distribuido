
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface iClientGame extends Remote {

	void start() throws RemoteException;

}
