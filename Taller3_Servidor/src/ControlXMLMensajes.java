import processing.core.PApplet;
import processing.data.XML;

public class ControlXMLMensajes extends PApplet {
	
	private XML usuarios;
	

	public ControlXMLMensajes() {

		try {
			usuarios = loadXML("data/BD_mensajes.xml");
		} catch (Exception e) {
			usuarios = parseXML("<mensajes></mensajes>");
		}

	}

	public void agregarMensaje(String usuario, String mensaje) {
		XML hijo = parseXML("<mensaje usuario=\"" + usuario + "\">"
				+ mensaje + "</mensaje>");
		usuarios.addChild(hijo);
		saveXML(usuarios, "data/BD_mensajes.xml");
	}

}
