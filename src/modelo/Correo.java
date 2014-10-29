package modelo;

import java.util.HashMap;
import java.util.Map;

public class Correo {

	private String subject;
	private String content;
	private Map<String, Integer> palabras;
	
	public Correo(String subject, String content){
		this.subject = subject;
		this.content  = content;
		generaPalabras();
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public Map<String, Integer> getPalabras() {
		return palabras;
	}

	public void setPalabras(Map<String, Integer> palabras) {
		this.palabras = palabras;
	}

	private void generaPalabras(){
		palabras = new HashMap<String, Integer>();
		for(String s : getContent().split(" ")){
			if(palabras.containsKey(s)){
				palabras.put(s, palabras.getOrDefault(s, 0) + 1);
			}else{
				palabras.put(s, 1);
			}
		}
	}
	
}
