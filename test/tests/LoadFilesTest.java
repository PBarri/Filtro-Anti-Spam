package tests;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import utilidades.Utilidades;

public class LoadFilesTest {
	
	private List<File> filesToAnalize;
	private Integer numCorreos;
	private final String RUTA_ENTRENAMIENTO = "C:\\Users\\a584183\\Desktop\\IA2 - WS\\Correos de entrenamiento";
	private final String RUTA_PRUEBA = "C:\\Users\\a584183\\Desktop\\IA2 - WS\\Correos de prueba";
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
		filesToAnalize = Utilidades.cargarCorreosEntrenamiento(RUTA_PRUEBA, CATEGORIA_SPAM);
		numCorreos = filesToAnalize.size();
		assertEquals(new Integer(4500), numCorreos);
	}
	
	@Test
	public void cuentaCorreosPruebaHam() {
		filesToAnalize = Utilidades.cargarCorreosEntrenamiento(RUTA_PRUEBA, CATEGORIA_HAM);
		numCorreos = filesToAnalize.size();
		assertEquals(new Integer(1500), numCorreos);
	}
	
	@Test
	public void cuentaCorreosEntrenamientoSpam() {
		filesToAnalize = Utilidades.cargarCorreosEntrenamiento(RUTA_ENTRENAMIENTO, CATEGORIA_SPAM);
		numCorreos = filesToAnalize.size();
		assertEquals(new Integer(12671), numCorreos);
	}
	
	@Test
	public void cuentaCorreosEntramientoHam() {
		filesToAnalize = Utilidades.cargarCorreosEntrenamiento(RUTA_ENTRENAMIENTO, CATEGORIA_HAM);
		numCorreos = filesToAnalize.size();
		assertEquals(new Integer(15045), numCorreos);
	}

}
