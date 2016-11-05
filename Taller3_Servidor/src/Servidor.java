import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import Serializable.Post;
import processing.data.XML;



public class Servidor extends Thread implements Observer {

	private ServerSocket ss;
	private ArrayList<ControlCliente> clientes;
	private ControlXMLUsuarios cxmlUsuarios;
	private ControlXMLMensajes cxmlMensajes;
	private ControlXMLPost cxmlPosts;
	private Vector <Post> postes;

	public Servidor() {
		cxmlUsuarios = new ControlXMLUsuarios();
		cxmlMensajes = new ControlXMLMensajes();
		cxmlPosts= new ControlXMLPost();
		clientes = new ArrayList<ControlCliente>();
		postes=new Vector<>();
		inicializarPost();
		System.out.println(postes.size());
		try {
			ss = new ServerSocket(5000);
			System.out.println("[ SERVIDOR INICIADO EN: "+ss.toString()+" ]"+ ss.getInetAddress().getHostAddress());
	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			try {
				System.out.println("[ ESPERANDO CLIENTE ]");
				clientes.add(new ControlCliente(ss.accept(), this));
				System.out.println("[ NUEVO CLIENTE ES: " + clientes.get(clientes.size()-1).toString() + " ]");
				System.out.println("[ CANTIDAD DE CLIENTES: " + clientes.size() + " ]");
				sleep(100);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	private void comprobarDirectorio(String usuario){
		   File file = new File("data/"+usuario);
	        boolean isDirectoryCreated = file.exists();

	        if(!isDirectoryCreated){
	            isDirectoryCreated = file.mkdirs();
	        System.out.println("Datos del usuario "+ usuario + ": No encontrados, creando directorio");
	        }
	        if(isDirectoryCreated){
	            System.out.println("Datos del usuario "+ usuario + ": Encontrados");
	        }
	}
	
	private void inicializarPost(){
		postes= cxmlPosts.initPostBD();
	}

	@Override
	public void update(Observable observado, Object mensaje) {
		
		
		
		if(mensaje instanceof String){
		String notificacion = (String) mensaje;
		if (notificacion.contains("login_req:")) {
			String[] partes = notificacion.split(":");			
			int resultadoLogin = cxmlUsuarios.validarUsuario(partes[1], partes[2]);
			((ControlCliente)observado).enviarMensaje("login_resp:"+resultadoLogin);
			if(resultadoLogin==1){
			comprobarDirectorio(partes[1]);
			}
		}
		if (notificacion.contains("signup_req:")) {
			String[] partes = notificacion.split(":");			
			boolean resultadoAgregar = cxmlUsuarios.agregarUsuario(partes[1], partes[2]);			
			((ControlCliente) observado).enviarMensaje("signup_resp:"+(resultadoAgregar==true?1:0));			
		}
		if (notificacion.contains("cliente_no_disponible")) {
			clientes.remove(observado);
			System.out.println("[ SE HA IDO UN CLIENTE, QUEDAN: " + clientes.size()+ " ]");
		}
		//
		if (notificacion.contains("mensaje_send:")) {			
			for (Iterator<ControlCliente> iterator = clientes.iterator(); iterator.hasNext();) {
				ControlCliente controlCliente = iterator.next();
				controlCliente.enviarMensaje(notificacion);
				String[] partes = notificacion.split(":");
				cxmlMensajes.agregarMensaje("NA", partes[1]);
			}
			
			//System.out.println("[ SE HA IDO UN CLIENTE, QUEDAN: " + clientes.size()+ " ]");
		}
		}else if(mensaje instanceof Post){
			Post posti= (Post) mensaje;
			if(cxmlPosts.agregarPost(posti.nombreUsuario, posti.contenidoPost, posti.fecha, posti.imgURL)){
				postes.add(posti);
			}
		}
		
		
		
	}
}
