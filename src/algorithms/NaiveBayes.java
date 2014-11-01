package algorithms;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Locale.Category;

import jUnit.LoadFilesTest;
import utils.*;
public class NaiveBayes {

	public static void main(String[] args) {
		
		
		/*Importante*/
		/*
		 * REVISAR el numero total de Documentos: ahora mismo da 33722 pero al sumar lo indicado en los txt son mas
		 */
	
		
		//Ruta donde estan los corpus
		String path   = "C:\\Users\\Alberto\\Desktop\\IA2\\corpus";
		//Llamamos a la funcion principal
		Utilities.loadTrainingMails(path);
		/*Matriz auxiliar prior*/
		Integer prior[]  = new Integer[2];
	
		 /*Numero de documentos del conjunto de entrenamiento*/
		 Integer numeroDocumentos = Utilities.numeroDocumentosHam+ Utilities.numeroDocumentosSpam;
	
	
		 /*Spam*/
		 Map<String, Integer> mapspam = Utilities.mapspam;
		 prior[0] = Utilities.numeroDocumentosSpam/numeroDocumentos;
		 String texto_c_spam = Utilities.auxiliarSpam; 
		 
		/*Ham*/
		 Map<String, Integer> mapham =  Utilities.mapham;		 
		 prior[1] = Utilities.numeroDocumentosHam/numeroDocumentos;	 
		 String texto_c_ham =  Utilities.auxiliarHam;
		
		/*Vocabulario total del conjunto de entrenamiento*/
		Map<String,Integer> vocabulario =  getVocabulario(mapspam,mapham);
		/*NumeroTotalPalabras*/
		Integer totalwords =0;
		totalwords+=total(mapspam);
		totalwords+=total(mapham);
		 /*Matriz auxiliar condpro*/
		Map<String,List<Float> > condprob = Utilities.generateProbabilities(mapspam, mapham);
		
	
		
		System.out.println(condprob.toString());
	}
	
	public static Integer total(Map<String,Integer> map)
	{
		Integer i = 0;
		Iterator<Integer> it = map.values().iterator();		
		while(it.hasNext())
		{
			i += it.next() +1;
			
		}
		return i;
	}
	
	
	
	/*Unifica los dos mapas*/
	public static Map<String,Integer> getVocabulario (Map<String,Integer> mapaSpam , Map<String,Integer> mapaHam){
		Map<String,Integer> vocabulario = mapaSpam;
		
		Iterator<String> it = mapaHam.keySet().iterator();
	

		
		String s = null;
		while(it.hasNext())
		{
			 s =  it.next();
			 
			 if(!vocabulario.containsKey(s)){
				 vocabulario.put(s, mapaHam.get(s));
				}else{
					vocabulario.put(s, vocabulario.get(s) +  mapaHam.get(s));
				}
			
		}
		
		
		
		return vocabulario;
		
	}
}
