package load;

import java.util.HashMap;

public class Load {
	private static HashMap<Long, String> load = new HashMap<Long,String>();
	private static HashMap<Long, String> load2 = new HashMap<Long,String>();
	private static HashMap<Long, String> load3 = new HashMap<Long,String>();
	private static HashMap<Long, String> load4 = new HashMap<Long,String>();
	private static HashMap<Long, String> load5 = new HashMap<Long,String>();
	private static HashMap<Long, String> load6 = new HashMap<Long,String>();
	private static long l = 0;
	public static void main(String[] args){
		while(true){
			load.put(l,"999999999999999999999999999999999999999999999999999999999999999999999999999999999");
			load2.put(l,"999999999999999999999999999999999999999999999999999999999999999999999999999999999");
			load3.put(l,"999999999999999999999999999999999999999999999999999999999999999999999999999999999");
			load4.put(l,"999999999999999999999999999999999999999999999999999999999999999999999999999999999");
			load5.put(l,"999999999999999999999999999999999999999999999999999999999999999999999999999999999");
			load6.put(l,"999999999999999999999999999999999999999999999999999999999999999999999999999999999");
			
			l++;
		}
	}
}
