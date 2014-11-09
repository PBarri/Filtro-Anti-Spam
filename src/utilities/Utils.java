package utilities;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Utils {
	
	public static List<String> convertPredictions(List<Float> predictions){
		List<String> result = new ArrayList<String>();
		for(Float f : predictions){
			switch(predictions.indexOf(f)){
			case 0:
				result.add(f.toString());
				break;
			case 1:
				result.add(f.toString());
				break;
			case 2:
				if(f.equals(new Float(1))){
					result.add("Spam");
				}else if(f.equals(new Float(2))){
					result.add("Ham");
				}else{
					result.add("Error clasificación");
				}
				break;
			case 3:
				if(f.equals(new Float(1))){
					result.add("Spam");
				}else{
					result.add("Ham");
				}
				break;
			default:
				break;
			}
		}
		return result;
	}
	
	public static String getPercentage(Number number, Number total){
		DecimalFormat df = new DecimalFormat("#.##");
		Float percentage;
		if(total != null){
			percentage = (number.floatValue() / total.floatValue()) * 100;
		}else{
			percentage = number.floatValue() * 100;
		}		
		return df.format(percentage) + " %";
	}

}
