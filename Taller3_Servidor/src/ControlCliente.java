import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;

import Serializable.Post;

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
				e.printStackTrace();
				System.out.println("[ PROBLEMA CON CLIENTE: " + e + " ]");
				setChanged();
				jefe.update(this, "cliente_no_disponible");
				disponible = false;
				clearChanged();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
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

	private void recibirMensajes() throws IOException, ClassNotFoundException {
		ObjectInputStream ois= new ObjectInputStream(s.getInputStream());
		Object o= ois.readObject();
		if(o instanceof Post){
			Post p= (Post) o;
			System.out.println(p.nombreUsuario);
			System.out.println(p.contenidoPost);
		}else if(o instanceof String){
		
		String mensaje = (String) o;
		System.out.println("[ MENSAJE A RECIBIDO: " + mensaje + " ]");
		jefe.update(this, mensaje);
		}
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
	
	

}
