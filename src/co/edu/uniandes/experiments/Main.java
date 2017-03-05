package co.edu.uniandes.experiments;

public class Main {
	public static void main (String [] args){		
		String path  = "C:\\Users\\Lina8a\\Documents\\job\\asistencia\\articulo9_DMTables-BPMConf\\experiments\\decision-tables\\check-order-reduced.properties";
		CPDetectOverlappingRulesGeneric generic = new CPDetectOverlappingRulesGeneric(path, 4);
		//CPDetectMissingRulesGeneric generic = new CPDetectMissingRulesGeneric(path, 4, 7, -1);
	}
}
