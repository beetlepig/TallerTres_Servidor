import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

public class ControlCliente extends Observable implements Runnable {

	private Socket s;
	private Observer jefe;
	private boolean disponible;
	int i;

	public ControlCliente(Socket s, Observer jefe) {
		this.s = s;
		this.jefe = jefe;
		disponible = true;
		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		while (disponible) {
			try {
				recibirMensajes();
				Thread.sleep(33);
			} catch (IOException e) {
				System.out.println("[ PROBLEMA CON CLIENTE: " + e + " ]");
				setChanged();
				jefe.update(this, "cliente_no_disponible");
				disponible = false;
				clearChanged();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			s.close();
		} catch (IOException e) {			
			e.printStackTrace();
		} finally {
			s = null;
		}
	}

	private void recibirMensajes() throws IOException {
		DataInputStream dis = new DataInputStream(s.getInputStream());
		String mensaje = dis.readUTF();
		System.out.println("[ MENSAJE A RECIBIDO: " + mensaje + " ]");
		jefe.update(this, mensaje);
	}

	public void enviarMensaje(String mensaje) {
		try {
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			dos.writeUTF(mensaje);
			System.out.println("[ MENSAJE A ENVIADO: " + mensaje + " ]");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {	
		return s.toString();
	}

}
