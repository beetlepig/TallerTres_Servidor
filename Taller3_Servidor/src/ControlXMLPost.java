import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import Serializable.Post;
import processing.core.PApplet;
import processing.data.XML;

public class ControlXMLPost extends PApplet{
	
	private XML posts;

	public ControlXMLPost() {
		
		try {
			posts = loadXML("data/BD_post.xml");
		} catch (Exception e) {
			posts = parseXML("<post></post>");
		}
		
		
		
	}
	
	public boolean agregarPost(String nombre, String descripcion, String fecha, String rutaImagen) {
		boolean agregado = false;
			XML hijo = parseXML("<post nombreUsuario=\""+nombre+" \" descripcion=\"" + descripcion + "\" ruta=\"" +rutaImagen +"\" fecha=\""+fecha+"\"></post>");
			posts.addChild(hijo);
			saveXML(posts, "data/BD_post.xml");
			agregado = true;
		return agregado;
	}
	
	public ArrayList<Post> initPostBD(){
		//leo el xml y paso un arreglo con todos los post al servidor
		XML[] hijos = posts.getChildren("post");
		ArrayList<Post> postis=new ArrayList<>();
		for (int i = 0; i < hijos.length; i++) {
			File fil= new File(hijos[i].getString("ruta"));
			byte[] imgSer=null;;
			String nombreImagen= fil.getName();
			try{
			byte[] b = new byte[(int) fil.length()];
			 FileInputStream fileInputStream = new FileInputStream(fil);
             fileInputStream.read(b);
             fileInputStream.close();
             imgSer= b;
			}catch (IOException e) {
				e.printStackTrace();
			}
            
		Post posti= new Post(hijos[i].getString("nombreUsuario"), hijos[i].getString("fecha"),  hijos[i].getString("descripcion"), imgSer, nombreImagen);
		postis.add(posti);
			
		}
		return postis;
	}
	
	
	

}
