package sources;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EntrenaYClasifica {

	// //////////////////////////////////////////////////////////////////////////////////////////
	// mapaEntrenamiento lo usaremos para llevar un recuento de apariciones de
	// cada una //
	// de las palabras procesadas durante el entrenamiento tanto en correo Ham
	// como Spam. //
	// Al final del entrenamiento calcularemos la probabilidad de cada una de
	// ser Spam o Ham //
	// El formato que utilizaremos en dicho mapa ser�: //
	// <palabra,<n�apariciones_spam,n�apariciones_ham,prob_spam,prob-ham>> //
	// //////////////////////////////////////////////////////////////////////////////////////////

	public Map<String, List<Double>> mapaEntrenamiento = new HashMap<String, List<Double>>();

	// ////////////////////////////////////////////////////////////////////////////////////////////
	// En conjuntoEntrenamiento vamos a guardar los �ndices asignados en el
	// array de ficheros //
	// de cada uno de los ficheros procesados, para que, en el caso de usar un
	// porcentje distinto//
	// de 100, tengamos una referencia de qu� ficheros no tenemos que volver a
	// procesar, ya que //
	// hemos considerado que los qrchivo utilizados para el entrenamiento,
	// aunque se seleccionen //
	// de forma aleatoria, deben de ser diferentes a los utilizados
	// posteriormente en el conjunto//
	// de prueba //
	// ////////////////////////////////////////////////////////////////////////////////////////////

	private Set<Integer> conjuntoEntrenamiento = new HashSet<Integer>();

	// ////////////////////////////////////////////////////////////////////////////////////////////
	// ruta guardar� la ruta del directorio que posteriormente procesaremos para
	// realizar //
	// el entrenamiento //
	// ////////////////////////////////////////////////////////////////////////////////////////////

	private File ruta;

	// ////////////////////////////////////////////////////////////////////////////////////////////
	// ruta guardar� la ruta del directorio que posteriormente procesaremos para
	// realizar //
	// las pruebas //
	// ////////////////////////////////////////////////////////////////////////////////////////////

	private File rutaPrueba;

	// ////////////////////////////////////////////////////////////////////////////////////////////
	// porcentaje indicar� cu�ntos archivos de los contenidos en la ruta de
	// entrenamiento //
	// queremos procesar como entrenamiento (valor entre 0 y 100).El valor
	// restante ser� la //
	// proporci�n de archivos que utilizaremos como conjunto de pruebas. //
	// Si porcentaje vale 100, en el conjunto de pruebas deberemosindicar una
	// ruta de prueba, en //
	// caso contrario, se utilizar� la misma ruta que la de entrenamiento //
	// ////////////////////////////////////////////////////////////////////////////////////////////

	private Integer porcentaje;

	// ////////////////////////////////////////////////////////////////////////////////////////////
	// Utilizaremos umbral para evitar que la aparici�n de una palabra muchas
	// veces en un correo //
	// spam penalice igual que la aparici�n de una palabra una vez en muchos
	// correos spam. //
	// Con �sto lo que pretendemos es reducir los errores de clasificaci�n. //
	// ////////////////////////////////////////////////////////////////////////////////////////////
	private Integer umbral;

	// contador total de correos spam en entrenamiento
	public static Double contSpam = 0.0;

	// contador total de correos spam en entrenamiento
	public static Double contHam = 0.0;

	// contador usado para contar las palabras sin tener en cuenta el n�mero
	// de apariciones, es decir, las palabras que se leen por primera vez
	public Integer contPalabras = 0;

	// contador total de palabras
	public Integer contPalabrasTotal = 0;

	// content guardar� en un �nico string el contenido del arcivho que estamos
	// procesando
	public String content = null;

	// En el array lista guardaremos cada una de las palabras procesadas del
	// texto
	public String[] lista;

	// Cuando procesemos un directorio con ficheros .txt, cada uno de �stos se
	// guardar� con
	// una posici�n asignada en �sta tabla.
	public File[] ficherosE;

	// usamos la variable nArchivos para saber el n�mero de archivos a procesar
	// con respecto al porcentaje de entrenamiento
	public Integer nArchivos = 0;

	// ////////////////////////////////////////////////////////////////////////////////////////////
	// Usaremos el array evaluci�n para guardar el n�mero de correos que se han
	// clasificado //
	// de cada forma. El formato de �ste array es el siguiente: //
	// {SpamClasificadoSpam, HamClasificadoHam, SpamClasificadoHam,
	// HamClasificadoSpam} //
	// ////////////////////////////////////////////////////////////////////////////////////////////

	public Integer[] evaluacion = { 0, 0, 0, 0 };

	// ////////////////////////////////////////////////////////////////////////////////////////////
	// Cada vez que clasifiquemos un correo, guardaremos una cadena, que
	// imprimiremos //
	// posteriormente, que podr� ser: //
	// Clasificado como SPAM correctamente //
	// Clasificado como SPAM incorrectamente //
	// Clasificado como HAM correctamente //
	// Clasificado como HAM incorrectamente //
	// ////////////////////////////////////////////////////////////////////////////////////////////
	public List<String> clasificados = new ArrayList<String>();

	// Constructor que inicializa los valores de los atributos
	public EntrenaYClasifica(String ruta, Integer porcentaje, Integer umbral) {

		this.ruta = new File(ruta);
		ficherosE = this.ruta.listFiles();
		this.porcentaje = porcentaje;
		this.umbral = umbral;
		nArchivos = (int) ((ficherosE.length * porcentaje) / 100);

	}

	// ////////////////////////////////////////////////////////////////////////////////////////////
	// �ste m�todo realiza el entrenamiento de nuestro clasificador. //
	// Para realizar dicho entrenamiento, tendremos que implementar dos casos
	// diferentes, //
	// el que procesa una �nica ruta haciendo uso de un porcentaje distinto de
	// 100, y el que //
	// procesa dos rutas con un porcentaje igual a 100. //
	// ////////////////////////////////////////////////////////////////////////////////////////////
	public Map<String, List<Double>> entrena() {

		// �ste ser� el caso en el que tengamos una �nica ruta y un porcentaje
		// para entrenamiento
		if (porcentaje < 100.0) {

			// Calculamos un n�mero aleatorio para seleccionar un archivo
			// cualquiera dentro del
			// array de archivos, para lo que necesitamos el tama�o del array
			// paraa calcular
			// el n�mero de archivos deseado a procesar, y el n�mero aleatorio
			// generado con
			// la funci�n random de la librer�a Math

			// Calculamos cu�ntos archivos suponen el porcentaje indicado como
			// par�metro del total
			// Vamos a utilizar numArchivos como contador, el cu�l iremos
			// decrementado cada vez
			// que procesemos un archivo
			int numArchivos = nArchivos;

			// mientras nos queden archivos por procesar
			while (numArchivos > 0) {

				// creamos una variable aleatoria
				Double x = Math.random();

				// Para tener un n�mero aleatorio entre 0 y ficheros.length ,
				// multiplicamos y truncamos,
				// con lo que conseguiremos una posici�n aleatoria en el array
				// de File
				Integer y = (int) (x * ficherosE.length);

				// Como en conjuntoEntrenamiento contiene los �ndices de los
				// archivos que ya hemos
				// procesado, si el n�mero generado no est� contenido en dicho
				// conjunto, significa
				// que podemos procesar el fichero asignado a ese �ndice
				if (!conjuntoEntrenamiento.contains(y)) {

					// Guardamos el valor de la posici�n procesada en
					// conjuntoEntrenamiento, para garantizar
					// que no procesaresmos 2 veces el mismo documento
					conjuntoEntrenamiento.add(y);

					// Por cada archivo tendremos que realizar el
					// correspondiente entrenamiento
					mapaEntrenamiento = entrenaYRellena(mapaEntrenamiento,
							ficherosE[y]);

					numArchivos--;
				}

			}

			// Una vez que terminemos de procesar todos los archivos del
			// conjunto de entrenamiento,
			// tenemos que rellenar las probabilidades de spam y ham de cada
			// palabra de mapaEntrenamineto
			mapaEntrenamiento = rellenaProbabilidades(mapaEntrenamiento);

			// �ste es el caso de que tengamos el porcentaje a 100, y por lo
			// tanto, un directorio
			// de prueba diferente al de entrenamiento
		} else {

			// Como se va a procesar el directorio completo, podemos hacer el
			// entrenamiento
			// directamente sin tener que elegir ficheros al azar.
			// Haremos lo mismo que antes, por cada archivo procesado, haremos
			// entrenamiento
			// y una vez procesados todos los archivos, rellenaremos las
			// probabilidades de
			// cada palabra
			for (int i = 0; i < ficherosE.length; i++) {

				mapaEntrenamiento = entrenaYRellena(mapaEntrenamiento,
						ficherosE[i]);

			}

			mapaEntrenamiento = rellenaProbabilidades(mapaEntrenamiento);
		}

		// Devolveremos el mapa relleno con todas las palabras, sus apariciones
		// y sus probabilidades
		return mapaEntrenamiento;

	}

	// ////////////////////////////////////////////////////////////////////////////////////////////
	// Utilizaremos �ste m�todo cuando, en la interfaz gr�fica, el usuario
	// introduzaca un texto //
	// directamente en el campo habilitado para ello //
	// ////////////////////////////////////////////////////////////////////////////////////////////
	public Double[] clasificaTexto(Map<String, List<Double>> mapEntrenamiento,
			String texto) {

		// En mapaEntrenamiento tendremos guardads las palabras junto con su
		// n�mero de apariciones
		// y probabilidades procesadas en el entrenamiento
		Map<String, List<Double>> mapaEntrenamiento = mapEntrenamiento;

		// Utilizaremos ret para guardar las conclusiones tras haber hecho la
		// clasificaci�n del
		// del texto pasado, es decir, los valores de clasificaci�n de spam y
		// ham
		Double[] ret = { 0.0, 0.0 };

		// En lista guardaremos cada una de las palabras del texto a clasificar
		String[] lista = texto.split("([^a-zA-Z0-9])+");

		ret = clasificaTextoTxt(mapaEntrenamiento, lista);

		// El valor devuelto son los valores de clasificaci�n de spam y ham
		return ret;

	}

	// //////////////////////////////////////////////////////////////////////////////////////////////
	// Utilizaremos �ste m�todo cuando el usuario seleccione la ruta de un
	// archivo .txt a analizar //
	// //////////////////////////////////////////////////////////////////////////////////////////////
	public Double[] clasificaTxt(Map<String, List<Double>> mapEntrenamiento,
			String rutaTxt) {

		// En mapaEntrenamiento tendremos guardads las palabras junto con su
		// n�mero de apariciones
		// y probabilidades procesadas en el entrenamiento
		Map<String, List<Double>> mapaEntrenamiento = mapEntrenamiento;

		// En rutaTx, guardaremos la ruta del archivo .txt que se desea procesar
		File rutaTx = new File(rutaTxt);

		// Utilizaremos ret para guardar las conclusiones tras haber hecho la
		// clasificaci�n del
		// del texto pasado, es decir, los valores de clasificaci�n de spam y
		// ham
		Double[] ret = { 0.0, 0.0 };

		// Mediante �ste try-catch, lo que conseguimos es procesar la ruta, y
		// acceder al contenido
		// del .txt que �sta nos indica
		try {
			FileReader reader = new FileReader(rutaTx);
			char[] chars = new char[(int) rutaTx.length()];
			reader.read(chars);
			content = new String(chars);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// En lista guardaremos cada una de las palabras del texto a clasificar
		lista = content.split("([^a-zA-Z0-9])+");

		ret = clasificaTextoTxt(mapaEntrenamiento, lista);

		// El valor devuelto son los valores de clasificaci�n de spam y ham
		return ret;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////
	// Utilizaremos �ste m�todo cuando el usuario seleccione la ruta de un
	// archivo .txt a analizar //
	// //////////////////////////////////////////////////////////////////////////////////////////////
	public Integer[] clasifica(Map<String, List<Double>> mapEntrenamiento,
			String rutaP) {

		// Creamos mapaEntrenamiento para poder usar y modificar, si fuese
		// necesario, el mapa
		// de entrenamiento original
		Map<String, List<Double>> mapaEntrenamiento = mapEntrenamiento;

		// En el caso de que tengamos diferentes rutas de entrenamiento y prueba
		if (porcentaje == 100.0) {

			// Procesamos la ruta de prueba
			rutaPrueba = new File(rutaP);

			// Guardamos todos los ficheros en el array de ficheros
			File[] ficheros = rutaPrueba.listFiles();

			// Procesamos cada uno de los ficheros detro de la ruta especificada
			for (int i = 0; i < ficheros.length; i++) {

				evaluacion = clasificacion(ficheros, i, mapaEntrenamiento);
			}

			// En el caso de que tengamos un porcentaje espec�fico distinto de
			// 100
		} else {

			// Hacemos lo mismo que antes, pero con un peque�o matiz, tenemos
			// que
			// comprobar que el �ndice asignado en la tabla de ficheros no est�
			// contenido en el conjunto de �ndices de ficheros procesados para
			// el entrenammiento aleatorio, con lo que procesaremos en la
			// prueba, s�lo aquellos archivo que no seleccionamos con
			// anterioridad
			File[] ficheros = ruta.listFiles();

			// Calculamos el tama�o del bucle a procesar restando al n�mero
			// total de archivos el n�mero de Archivos entrenados
			int tama�oBucle = ficheros.length - nArchivos;
			for (int i = 0; i < tama�oBucle; i++) {
				if (!conjuntoEntrenamiento.contains(i)) {
					evaluacion = clasificacion(ficheros, i, mapaEntrenamiento);
				}

			}

		}

		// Devolvemos la tabla de evaluaci�n
		return evaluacion;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////
	// Utilizaremos �ste m�todo para rellenar el mapa de entrenamiento con cada
	// palabra y sus //
	// respectivas probabilidades, e iremos actualizando los contadores de
	// correo spam y ham //
	// //////////////////////////////////////////////////////////////////////////////////////////////
	public Map<String, List<Double>> entrenaYRellena(
			Map<String, List<Double>> mapEntrenamiento, File fichero) {

		// res ser� el valor que devolvamos al final del m�todo, y contendr�
		// todo el entrenamiento realizado
		Map<String, List<Double>> res = mapEntrenamiento;

		// Los archivos de enron tienen el nombre en el formato -------.ham.txt
		// � -------.spam.txt
		// lo que hacemos con crtaRuta es quedarnos con el spam o ham, para
		// modificar valores de
		// ham o spam seg�n lo que sea el correo
		String cortaRuta = fichero.getName();
		String aux[] = cortaRuta.split("[.]");
		cortaRuta = aux[aux.length - 2];

		if (cortaRuta.equals("spam")) {
			contSpam++;
		} else {
			contHam++;
		}

		// Con �ste try-catch lo que hacemos es leer el contenido del .txt y lo
		// guardamos en un string
		try {
			FileReader reader = new FileReader(fichero);
			char[] chars = new char[(int) fichero.length()];
			reader.read(chars);
			content = new String(chars);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Dividimos el string guardado anteriormente en palabras
		lista = content.split("([^a-zA-Z0-9])+");

		// Procesaremos cada una de las palabras
		for (String s : lista) {

			// Para no hacer distinci�n entre may�sculas y min�sculas,
			// formateamos
			// la palabra que estamos procesando para que est� entera en
			// min�sculas
			s = s.toLowerCase();

			// Tenemos que ver si el correo que estamos procesando es spam o ham
			if (cortaRuta.equals("spam")) {

				// Comprobamos tambi�n si la palabra est� contenida en el mapa
				// de
				// entrenamiento, si est� contenida, actualizaremos su n�mero de
				// apariciones siempre y cuando no supere el valor umbral
				// establecido
				if (res.containsKey(s)) {
					List<Double> l = res.get(s);
					Double apariciones = l.get(0);

					// Si no se supera el umbral establecido, se aumenta el
					// n�mero de apariciones
					if (apariciones < umbral) {
						apariciones++;
						l.set(0, apariciones);
						res.put(s, l);
						contPalabrasTotal++;
					}

					// Si la palabra no est� en el mapa de entrenamiento, la
					// a�adimos
					// con n�mero de apariciones igual a 1
				} else {
					List<Double> l = new ArrayList<Double>();
					l.add(0, 1.0);
					l.add(1, 0.0);
					res.put(s, l);
					contPalabras++;
					contPalabrasTotal++;
				}

				// Si el correo es ham, modificaremos los contadores de ham,
				// y haremos lo mismo que antes
			} else {
				if (res.containsKey(s)) {

					List<Double> l = res.get(s);
					Double apariciones = l.get(1);

					if (apariciones < umbral) {
						apariciones++;
						l.set(1, apariciones);
						res.put(s, l);
						contPalabrasTotal++;
					}
				} else {
					List<Double> l = new ArrayList<Double>();
					l.add(0, 0.0);
					l.add(1, 1.0);
					res.put(s, l);
					contPalabras++;
					contPalabrasTotal++;
				}
			}

		}
		return res;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////
	// Utilizaremos �ste m�todo para rellenar las probabilidades de spam y ham
	// en el mapa de //
	// entrenamiento una vez que lo hemos rellenado completamente con todas las
	// palabras de //
	// entrenamiento y su respectivo n�mero de apariciones //
	// //////////////////////////////////////////////////////////////////////////////////////////////
	public Map<String, List<Double>> rellenaProbabilidades(
			Map<String, List<Double>> mapEntrenamiento) {

		Map<String, List<Double>> res = mapEntrenamiento;

		// Por cada palabra, accedemos a sus respectivos contadores de spam y
		// ham,
		// y haciendo uso de los contadores totales de ham y spam, calculamos
		// las probabilidades
		// de cada palabra y a�adimos dicho valor a la lista de doubles asociada
		// a cada palabra
		for (String s : res.keySet()) {
			List<Double> l = res.get(s);
			Double nSpam = l.get(0) + 1;
			Double nHam = l.get(1) + 1;
			Double proporcionSpam = nSpam / (contPalabrasTotal + contPalabras);
			Double proporcionHam = nHam / (contPalabrasTotal + contPalabras);

			l.add(2, proporcionSpam);
			l.add(3, proporcionHam);

			res.put(s, l);

		}
		return res;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////
	// getMapa nos devuelve el mapa de entrenamiento (usaremos �ste metodo en la
	// interfaz gr�fica) //
	// //////////////////////////////////////////////////////////////////////////////////////////////
	public Map<String, List<Double>> getMapa() {
		return mapaEntrenamiento;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////
	// setMapa sustituye el mapa de entrenamiento existente por el que se le
	// pasa por par�metros //
	// con �sto evitamos que una vez que hayamos entrenado con un directorio,
	// para realizar //
	// una nueva clasificaci�n sobre el mismo entrenamiento, tengamos que volver
	// a entrenar //
	// (usaremos �ste metodo en la interfaz gr�fica) //
	// //////////////////////////////////////////////////////////////////////////////////////////////
	public void setMapa(Map<String, List<Double>> nuevoMapa) {
		mapaEntrenamiento = nuevoMapa;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////
	// getEvaluacion devuelve la tabla evaluacion (usaremos �ste metodo en la
	// interfaz gr�fica) //
	// //////////////////////////////////////////////////////////////////////////////////////////////
	public Integer[] getEvaluacion() {
		return evaluacion;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////
	// Utilizamos �ste m�todo para imprimir todos los mensajes de clasificaci�n
	// //
	// (usaremos �ste metodo en la interfaz gr�fica) //
	// //////////////////////////////////////////////////////////////////////////////////////////////
	public String getClasifidados() {
		String res = "";
		for (int i = 0; i < clasificados.size(); i++) {
			res += clasificados.get(i);
		}
		return res;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////
	// �ste m�todo lo vamos a utilizar cuando queramos hacer una clasificaci�n
	// de un �nico correo, //
	// ya sea pasando la ruta del .txt, o escribi�ndolo en el campo destinado
	// para ello //
	// //////////////////////////////////////////////////////////////////////////////////////////////
	public Double[] clasificaTextoTxt(
			Map<String, List<Double>> mapEntrenamiento, String[] lista) {
		Map<String, List<Double>> mapaEntrenamiento = mapEntrenamiento;

		// Utilizaremos mapAux para guardar las palabras del texto a clasificar
		// que est�n contenidas
		// en el mapa de entrenamiento, y mapAux2 para las que no lo est�n
		Map<String, Integer> mapAux = new HashMap<String, Integer>();
		Map<String, Integer> mapAux2 = new HashMap<String, Integer>();

		// ret guarda los valores de spam y ham por los que el texto ha sido
		// clsificado en un
		// grupo u otro
		Double[] ret = { 0.0, 0.0 };

		Double probTotalSpam = Math.log10(contSpam / (contSpam + contHam));
		Double probTotalHam = Math.log10(contHam / (contSpam + contHam));

		// Procesamos cada una de las palabras del texto
		for (String s : lista) {

			// Formateamos a min�sculas la palabra que estamos procesando
			s = s.toLowerCase();

			contPalabrasTotal++;

			// Si la palabra est� contenida en el mapa de entrenamiento,
			// calculamos la probabilidad de que sea spam y de que sea ham
			if (mapaEntrenamiento.containsKey(s)) {

				probTotalSpam += Math.log10(mapaEntrenamiento.get(s).get(2));
				probTotalHam += Math.log10(mapaEntrenamiento.get(s).get(3));

				// Si el mapa auxiliar contiene la palabra, aumentamos las
				// apariciones
				// de �sta
				if (mapAux.containsKey(s)) {
					Integer value = mapAux.get(s);
					mapAux.put(s, value + 1);

					// Si no la contiene, la a�adimos
				} else {
					mapAux.put(s, 1);
				}

				// Si no est� la palabra en el mapa de entrenamiento, entonces
				// la a�adimos a mapAux2, o incrementamos sus apariciones en el
				// caso de que ya etuviese contenida
			} else {

				if (mapAux2.containsKey(s)) {
					Integer value = mapAux2.get(s);
					mapAux2.put(s, value + 1);
				} else {
					mapAux2.put(s, 1);
					contPalabras++;
				}
			}

		}

		// Los valores a devolver ser�n los de clasificaci�n de ham o spam
		ret[0] = probTotalSpam;
		ret[1] = probTotalHam;
		return ret;
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////
	// �ste m�todo lo vamos a utilizar para realizar la clasificaci�n de cada
	// uno de los archivos //
	// contenidos en un directorio determinado, ya sea haciendo uso del
	// porcentaje, o con la //
	// selecci�n de un directorio de pruebas distinto al de entrenamiento //
	// //////////////////////////////////////////////////////////////////////////////////////////////
	public Integer[] clasificacion(File[] ficheros, Integer i,
			Map<String, List<Double>> mapEntrenamiento) {

		// Usaremos la variable cortaRuta para guardar la cadena que indica en
		// el nombre
		// del archivo si es ham o spam
		String cortaRuta = ficheros[i].getName();
		String aux[] = cortaRuta.split("[.]");
		cortaRuta = aux[aux.length - 2];

		// Usaremos mapAux para contar el n�mero de apariciones de cada
		// palabra en un txt
		Map<String, Integer> mapAux = new HashMap<String, Integer>();

		// Nos creamos un segundo mapa aux para guardar aquellas
		// palabras que no est�n en el mapEntrenamiento
		Map<String, Integer> mapAux2 = new HashMap<String, Integer>();

		// inicializamos probTotalSpam y la probTotalHam a la probabilidad
		// de spam y ham calculadas en el entrenamiento respectivamente
		Double probTotalSpam = Math.log10(contSpam / (contSpam + contHam));
		Double probTotalHam = Math.log10(contHam / (contSpam + contHam));

		// Con �ste try-catch lo que hacemos es leer el contenido del .txt y lo
		// guardamos en un string
		try {
			FileReader reader = new FileReader(ficheros[i]);
			char[] chars = new char[(int) ficheros[i].length()];
			reader.read(chars);
			content = new String(chars);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		lista = content.split("([^a-zA-Z0-9])+");

		// procesamos de formaindividual cada palabra
		for (String s : lista) {

			s = s.toLowerCase();

			contPalabrasTotal++;

			// Si la palabra est� contenida en el mapa de entrenamiento,
			// calculamos sus probabilidades, si no, no la tendremos en cuenta
			// a la hora de clasificar el documento
			if (mapaEntrenamiento.containsKey(s)) {

				probTotalSpam += Math.log10(mapaEntrenamiento.get(s).get(2));
				probTotalHam += Math.log10(mapaEntrenamiento.get(s).get(3));

				if (mapAux.containsKey(s)) {
					Integer value = mapAux.get(s);
					mapAux.put(s, value + 1);
				} else {
					mapAux.put(s, 1);
				}
			} else {

				if (mapAux2.containsKey(s)) {
					Integer value = mapAux2.get(s);
					mapAux2.put(s, value + 1);
				} else {
					mapAux2.put(s, 1);
					contPalabras++;
				}
			}

		}

		// Dependiendo de que valor sea mayor, tendremos una clasificaci�n.
		// Adem�s, comprobaremos si la clasificaci�n realizada es correcta
		// haciendo uso del valor que guardamos anteriormente en la variable
		// cortaRuta
		if (probTotalSpam > probTotalHam) {

			if (cortaRuta.equals("spam")) {
				evaluacion[0] += 1;
				clasificados.add("Clasificado como SPAM correctamente\n");
			} else {
				evaluacion[2] += 1;
				clasificados.add("Clasificado como SPAM incorrectamente\n");
			}

		} else {

			if (cortaRuta.equals("ham")) {
				evaluacion[1] += 1;
				clasificados.add("Clasificado como HAM correctamente\n");
			} else {
				evaluacion[3] += 1;
				clasificados.add("Clasificado como HAM incorrectamente\n");
			}
		}
		return evaluacion;
	}
}