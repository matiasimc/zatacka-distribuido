

import java.rmi.*;

public class MainClient {
	public static void main(String[] args) {
		iServer server;
		String ip = args[0];
		System.out.println(ip);
		try {
			server = (iServer) Naming.lookup("rmi://"+ip+":1099/ABC");
			iClient client = new Client(server);
			System.out.println("Me estoy conectando");
			server.addClient(client);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}	
}
