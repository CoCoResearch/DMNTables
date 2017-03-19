package co.edu.uniandes.analysis;

public class Main {
	public static void main (String [] args){		
		String path  = "models/decision-tables/discount2.properties";
		String configPath = "models/configs/config-loan-approval5.properties";
		
		//CPDetectOverlappingRulesGeneric generic = new CPDetectOverlappingRulesGeneric(path, 2);
		CPDetectMissingRulesGeneric generic = new CPDetectMissingRulesGeneric(path, 4, 4, 1);
		//CPPropagateDecisionsGeneric generic = new CPPropagateDecisionsGeneric(path, configPath);
		//DMNParser parser = new DMNParser("models/decision-tables/3D_500_M.dmn");
		//parser.parseToProperties();
	}
}
