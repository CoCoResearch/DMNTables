package co.edu.uniandes.analysis;

import co.edu.uniandes.parser.DMNParser;

public class Main {
	public static void main (String [] args){		
		String path  = "models/fsg/fsg-loan-approval.properties";
		String configPath = "models/configs/config-loan-approval5.properties";
		//CPDetectOverlappingRulesGeneric generic = new CPDetectOverlappingRulesGeneric(path, 4);
		//CPDetectMissingRulesGeneric generic = new CPDetectMissingRulesGeneric(path, 2, 4, -1);
		//CPPropagateDecisionsGeneric generic = new CPPropagateDecisionsGeneric(path, configPath);
		
		DMNParser parser = new DMNParser("models/decision-tables/1overlapRules.dmn");
		parser.parseToProperties();
	}
}
