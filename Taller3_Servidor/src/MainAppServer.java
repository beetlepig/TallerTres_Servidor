import processing.core.PApplet;


public class MainAppServer {	
	static Servidor server;	
	public static void main (String [ ] args) {
		server = new Servidor();
		server.start();
	}
}
