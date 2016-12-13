package client;


import java.rmi.*;

import server.iServer;

public class MainClient {
	public static void main(String[] args) {
		iServer server;
		String serverip = args[0];
		String myip = args[1];
		System.out.println(serverip);
		try {
			server = (iServer) Naming.lookup("rmi://"+serverip+":1099/ABC");
			iClient client = new Client(server, myip);
			System.out.println("Me estoy conectando");
			if (!server.canPlay()) {
				System.out.println("No hay espacio disponible para jugar");
				System.exit(0);
			}
			server.addClient(client);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}	
}
