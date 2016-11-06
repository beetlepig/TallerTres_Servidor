import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
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
			String ruta= guardarArchivo(p.nameImage, p.imagen, p.nombreUsuario);
			System.out.println("[ POST RECIBIDO DE: " + p.nombreUsuario + " ]");
			System.out.println("RUTA: " + ruta);
			Post pSimplificado= new Post(p.nombreUsuario, p.fecha, p.contenidoPost, ruta);
			jefe.update(this, pSimplificado);
		}else if(o instanceof String){
		
		String mensaje = (String) o;
		System.out.println("[ MENSAJE A RECIBIDO: " + mensaje + " ]");
		jefe.update(this, mensaje);
		}
	}
	
	private String guardarArchivo(String nombre, byte[] buf, String usuario) throws IOException {
		try {
			File archivo = new File("data/" +usuario+"/" +nombre);
			archivo.createNewFile();
			FileOutputStream salida = new FileOutputStream(archivo);
			salida.write(buf);
			
			salida.flush();
			salida.close();
			return archivo.getPath();
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	


	public void enviarMensaje(Object mensaje) {
		try {
			ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());
			dos.writeObject(mensaje);
			System.out.println("[ MENSAJE A ENVIADO: " + mensaje + " ]");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	

}
