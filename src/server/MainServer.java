package server;

import java.io.File;
import java.rmi.Naming;

public class MainServer {
	
	public static void main(String[] args) {
		int waitPlayers = 2;
		String ip = "";
		String ipS = "";
		int exTipe = 0;
		try {
			ip = args[0];
			System.out.println(ip);
		}
		catch (Exception e) {
			System.out.println("Usage: MainServer.java <ip> -s <ipserver> OR -n <waitPlayer>");
			System.exit(1);
		}
		try {
			String optionS = args [1];
			if (optionS.equals("-s")) {
				exTipe = 1; // enqueued server
				try {
					ipS = args[2];
				}
				catch (Exception e) {
					System.out.println("Usage: MainServer.java <ip> -s <ipserver>");
					System.exit(1);
				}
			}
			else if (optionS.equals("-n")) {
				exTipe = 2; // first server
				try {
					
						waitPlayers = Integer.parseInt(args[2]);
					}
				catch (Exception e) {
					System.out.println("Usage: MainServer.java <ip> -n <waitPlayer>");
					System.exit(1);
				}
			}
			else {
				System.out.println("Usage: MainServer.java <ip> -s <ipserver> OR -n <waitPlayer>");
				System.exit(1);
			}
			
		}
		catch (Exception e) {
			// do nothing
		}
		try {
			System.setProperty("java.rmi.server.hostname", ip);
			if (exTipe == 1) {
				iServer server = new Server(ip);
				iServer bossServer = (iServer) Naming.lookup("rmi://"+ipS+":1099/ABC");
				bossServer.addServer(server);
				Naming.bind("rmi://"+ip+":1099/ABC", server);
				System.out.println("Server " + ip + " waiting for migration from server "+ipS);
			}
			else {
				iServer serverS;
				File f = new File("snapshot.txt");
				if(f.exists()) {
					serverS = new Server(ip, f);
				}
				else {
					serverS = new Server(waitPlayers, ip);
				}
				Naming.bind("rmi://"+ip+":1099/ABC", serverS);
				System.out.println("Main server UP, in execution");
			}
		}catch(Exception e){
			System.out.println("La excepcion la capture en el main");
			e.printStackTrace();
		}
	}	
}
