package co.edu.uniandes.experiments;

public class Main {
	public static void main (String [] args){		
		String path  = "decision-tables/fsg-loan-approval.properties";
		//CPDetectOverlappingRulesGeneric generic = new CPDetectOverlappingRulesGeneric(path, 4);
		//CPDetectMissingRulesGeneric generic = new CPDetectMissingRulesGeneric(path, 2, 4, -1);
		CPPropagateDecisionsGeneric generic = new CPPropagateDecisionsGeneric(path);
	}
}
