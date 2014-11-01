package algorithms;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
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
		 * Antes de aplicar el algoritmo en si, debemos tener V (vocabulario que se extrae del conjunto de entrenamiento D)
		 * y una segunda variable N =  número de documentos de D
		 */
	
		
		//Ruta donde estan los corpus
		String path   = "C:\\Users\\Alberto\\Desktop\\IA2\\corpus";
		String category= "spam"; 
		String category2 = "ham";
		
		//mapa con el vocabulario que aparece en los correos spam y su numero de apariciones
		Map<String, Integer> mapspam = Utilities.loadWords( Utilities.loadTrainingMails(path, category));
		
		//mapa con el vocabulario que aparece en los correos ham y su numero de apariciones
		Map<String,Integer> mapNospam = Utilities.loadWords(Utilities.loadTrainingMails(path, category2));
		
		//Varibale que nos indica el número total de documentos hay que dividirlo por 2 al llamar dos veces a iterateDirectories
		//Una vez por cada categoría
		Integer variableN = Utilities.numeroDocumentos/2;
		
		//Mapa con las pobabilidades
		//Map<String,List<Float>> mapa =Utilities.generateProbabilities(mapspam, mapNospam);

		
		
		
	
	}

}
