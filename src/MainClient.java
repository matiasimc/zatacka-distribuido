

import java.rmi.*;

public class MainClient {
	public static void main(String[] args) {
		iServer server;
		try {
			server = (iServer) Naming.lookup("//localhost/ABC");
			iClient client = new Client(server);
			System.out.println("Me estoy conectando");
			server.addClient(client);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}	
}
