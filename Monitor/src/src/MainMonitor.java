package src.src;

public class MainMonitor {

	public static void main(String[] args) {		
		
		try {
			Monitor monitor = Monitor.get_instance();
			monitor.iniciar();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
