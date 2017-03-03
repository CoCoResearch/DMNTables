package co.edu.uniandes.experiments;

public class Main {
	public static void main (String [] args){
		//DecisionSolving experiment1 = new DecisionSolving();
		//GeometryExperiments experiment2 = new GeometryExperiments();
		//CPDetectMissingRulesExperiment experiment3 = new CPDetectMissingRulesExperiment();
		//CPDetectOverlappingRulesExperiment experiment4 = new CPDetectOverlappingRulesExperiment();
		
		String path  = "C:\\Users\\Lina8a\\Documents\\job\\asistencia\\articulo9_DMTables-BPMConf\\experiments\\decision-tables\\loan-grade.properties";
		//CPDetectOverlappingRulesGeneric generic = new CPDetectOverlappingRulesGeneric(path);
		CPDetectMissingRulesGeneric generic = new CPDetectMissingRulesGeneric(path, 2);
	}
}
