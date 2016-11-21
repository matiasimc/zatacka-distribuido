package client;


import java.rmi.*;

import server.iServer;

public class MainClient {
	public static void main(String[] args) {
		iServer server;
		String ip = args[0];
		System.out.println(ip);
		try {
			server = (iServer) Naming.lookup("rmi://"+ip+":1099/ABC");
			iClient client = new Client(server);
			System.out.println("Me estoy conectando");
			if (!server.canPlay()) {
				System.out.println("No hay espacio disponible para jugar");
				System.exit(0);
			}
			server.addClient(client);
			
			
		}catch(Exception e){
			System.out.println("aca mori");
			e.printStackTrace();
		}
	}	
}
