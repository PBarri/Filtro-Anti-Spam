package utilities;

import javafx.util.StringConverter;
/**
 * Clase que sirve para convertir el valor de un Slider a un valor numérico
 * 
 * @author Pablo Barrientos Lobato
 * @author Alberto Salas Cantalejo
 *
 */
public class SliderConverter extends StringConverter<Number> {

	@Override
	public Number fromString(String text) {
		return new Integer(text);
	}

	@Override
	public String toString(Number number) {
		return new Integer(number.intValue()).toString();
	}

}
