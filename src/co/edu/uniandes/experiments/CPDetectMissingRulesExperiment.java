package co.edu.uniandes.experiments;

import org.chocosolver.solver.ResolutionPolicy;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.constraints.LogicalConstraintFactory;
import org.chocosolver.solver.search.loop.monitors.SMF;
import org.chocosolver.solver.trace.Chatterbox;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;

public class CPDetectMissingRulesExperiment {
	private IntVar [][] matrix;
	private IntVar[][] missingRules;
	private IntVar missingRulesNumber;
	private Solver solver;

	public CPDetectMissingRulesExperiment(){
		solver = new Solver();

		//Initialize matrix with hyper-rectangles
		matrix = new IntVar[4][4];

		matrix[0][0] = VariableFactory.fixed("1_Xo", 0, solver);
		matrix[0][1] = VariableFactory.fixed("1_Xe", 1000, solver);
		matrix[0][2] = VariableFactory.fixed("1_Yo", 0, solver);
		matrix[0][3] = VariableFactory.fixed("1_Ye", 1000, solver);

		matrix[1][0] = VariableFactory.fixed("2_Xo", 250, solver);
		matrix[1][1] = VariableFactory.fixed("2_Xe", 750, solver);
		matrix[1][2] = VariableFactory.fixed("2_Yo", 4000, solver);
		matrix[1][3] = VariableFactory.fixed("2_Ye", 5000, solver);

		matrix[2][0] = VariableFactory.fixed("3_Xo", 500, solver);
		matrix[2][1] = VariableFactory.fixed("3_Xe", 1500, solver);
		matrix[2][2] = VariableFactory.fixed("3_Yo", 500, solver);
		matrix[2][3] = VariableFactory.fixed("3_Ye", 3000, solver);

		matrix[3][0] = VariableFactory.fixed("4_Xo", 2000, solver);
		matrix[3][1] = VariableFactory.fixed("4_Xe", 0, solver);
		matrix[3][2] = VariableFactory.fixed("4_Yo", 2500, solver);
		matrix[3][3] = VariableFactory.fixed("4_Ye", 2000, solver);

		missingRulesNumber = VariableFactory.enumerated("MissingRulesNumber", -2, 0, solver);
		missingRules = new IntVar[matrix.length + -(missingRulesNumber.getValue())][4];
		
		for(int i = 0; i < missingRules.length; i++){
			if(i < matrix.length){
				missingRules[i][0] = matrix[i][0];
				missingRules[i][1] = matrix[i][1];
				missingRules[i][2] = matrix[i][2];
				missingRules[i][3] = matrix[i][3];
			}
			else{
				System.out.println("Entra" + i);
				missingRules[i][0] = VariableFactory.enumerated(i + "_Xo", 0, 2500, solver);
				missingRules[i][1] = VariableFactory.enumerated(i + "_Xe", 0, 2500, solver);
				missingRules[i][2] = VariableFactory.enumerated(i + "_Yo", 0, 5000, solver);
				missingRules[i][3] = VariableFactory.enumerated(i + "_Ye", 0, 5000, solver);
			}
		}
		System.out.println(missingRules.length);
		for(int i = matrix.length; i < missingRules.length; i++) {
			for(int j = 0; j < missingRules.length; j++) {
				if(i != j){
					postRectangleConstraints(i);
					postXConstraints(i,j);
					postYConstraints(i,j);
				}
			}
		}
		
		/*IntVar[] areaValues = new IntVar[missingRules.length];
		IntVar[] objectiveVars = new IntVar[(2*missingRules.length)];
		IntVar areaSum = VariableFactory.fixed("AreaSum", 12500000, solver);
		objectiveVars[0] = missingRulesNumber;
		
		for(int i = 0; i < missingRules.length; i++) {
			int width = missingRules[i][1].getValue() - missingRules[i][0].getValue();
			int height = missingRules[i][3].getValue() - missingRules[i][2].getValue();
			
			areaValues[i] = VariableFactory.fixed(i + "_Area", width * height, solver);
			objectiveVars[i+1] = areaValues[i];
		}
		
		solver.post(IntConstraintFactory.sum(areaValues, areaSum));*/
		
		SMF.limitSolution(solver, 10);
		Chatterbox.showSolutions(solver);
		//solver.findParetoFront(ResolutionPolicy.MAXIMIZE, objectiveVars);
		solver.findAllSolutions();
		Chatterbox.printStatistics(solver);
	}
	
	private void postXConstraints(int i, int j){
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][0], "<", missingRules[j][0]),
				IntConstraintFactory.arithm(missingRules[i][1], "<", missingRules[j][0]),
				LogicalConstraintFactory.or(
						LogicalConstraintFactory.or(
								IntConstraintFactory.arithm(missingRules[i][0], ">", missingRules[j][1]),
								IntConstraintFactory.arithm(missingRules[i][2], ">", missingRules[j][3])
						),
						IntConstraintFactory.arithm(missingRules[i][3], "<", missingRules[j][2])
				)
		);
	}
	
	private void postYConstraints(int i, int j){
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][2], "<", missingRules[j][2]),
				IntConstraintFactory.arithm(missingRules[i][3], "<", missingRules[j][2]),
				LogicalConstraintFactory.or(
						LogicalConstraintFactory.or(
								IntConstraintFactory.arithm(missingRules[i][2], ">", missingRules[j][3]),
								IntConstraintFactory.arithm(missingRules[i][0], ">", missingRules[j][1])
						),
						IntConstraintFactory.arithm(missingRules[i][1], "<", missingRules[j][0])
				)
		);
	}
	
	private void postRectangleConstraints(int i){
		solver.post(IntConstraintFactory.arithm(missingRules[i][0], "<", missingRules[i][1]));
		solver.post(IntConstraintFactory.arithm(missingRules[i][2], "<", missingRules[i][3]));
	}
}
