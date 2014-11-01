package jUnit;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import utils.Utilities;
import exceptions.InvalidPathException;

public class LoadFilesTest {
	
	private List<File> filesToAnalize;
	private Integer numCorreos;
	private final String RUTA_ENTRENAMIENTO = "C:\\Users\\a584183\\Desktop\\IA2 - WS\\Correos de entrenamiento";
	private final String RUTA_PRUEBA = "C:\\Users\\a584183\\Desktop\\IA2 - WS\\Correos de prueba";
	private final String RUTA_INVALIDA = "C:\\Users\\a584183\\Desktop\\IA2 - WS\\Correos de prueba\\enron6\\Summary.txt";
	//private final String RUTA_FALLO = "";
	private final String CATEGORIA_HAM = "ham";
	private final String CATEGORIA_SPAM = "spam";

	@Before
	public void setUp() throws Exception {
		filesToAnalize = new ArrayList<File>();
		numCorreos = 0;
	}

	@After
	public void tearDown() throws Exception {
		filesToAnalize.clear();
	}

	@Test
	public void cuentaCorreosPruebaSpam() {
		filesToAnalize = Utilities.loadTrainingMails(RUTA_PRUEBA);
		numCorreos = filesToAnalize.size();
		assertEquals(new Integer(4500), numCorreos);
	}
	
	@Test
	public void cuentaCorreosPruebaHam() {
		filesToAnalize = Utilities.loadTrainingMails(RUTA_PRUEBA);
		numCorreos = filesToAnalize.size();
		assertEquals(new Integer(1500), numCorreos);
	}
	
	@Test
	public void cuentaCorreosEntrenamientoSpam() {
		filesToAnalize = Utilities.loadTrainingMails(RUTA_ENTRENAMIENTO);
		numCorreos = filesToAnalize.size();
		assertEquals(new Integer(12671), numCorreos);
	}
	
	@Test
	public void cuentaCorreosEntramientoHam() {
		filesToAnalize = Utilities.loadTrainingMails(RUTA_ENTRENAMIENTO);
		numCorreos = filesToAnalize.size();
		assertEquals(new Integer(15045), numCorreos);
	}
	
	@Test(expected = InvalidPathException.class) 
	public void testRutaInvalida(){
		Utilities.loadTrainingMails(RUTA_INVALIDA);
	}
	
	@Test(expected = NullPointerException.class)
	public void testFalloRuta(){
		Utilities.loadTrainingMails(null);
	}
}
