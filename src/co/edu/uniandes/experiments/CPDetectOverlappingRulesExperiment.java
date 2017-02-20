package co.edu.uniandes.experiments;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.constraints.LogicalConstraintFactory;
import org.chocosolver.solver.trace.Chatterbox;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;

public class CPDetectOverlappingRulesExperiment {

	private IntVar [][] matrix;
	private IntVar [][] conflicts;
	
	public CPDetectOverlappingRulesExperiment(){
		Solver solver = new Solver();
		
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
		
		//Initialize conflicts matrix to identify conflicts among rules
		conflicts = new IntVar[4][4];
		for(int i = 0; i < conflicts.length; i++) {
			for(int j = 0; j < conflicts.length; j++) {
				conflicts[i][j] = VariableFactory.bool(i + "_" + j, solver);
			}
		}
		
		for(int i = 0; i < matrix.length; i++){
			for(int j = i + 1; j < matrix.length; j++){
				
				//Cases A-B, B-A)
				detectOverlappingRules(i,j);
			}
		}
		
		//Diagonal has no conflicts
		for(int i = 0; i < conflicts.length; i++) {
			solver.post(IntConstraintFactory.arithm(conflicts[i][i], "=", 0));
		}
		
		//Solve problem
		Chatterbox.showSolutions(solver);
		solver.findAllSolutions();
		Chatterbox.printStatistics(solver);
	}
	
	/**
	 * Case 1 of overlapping hyper-rectangles:
	 * A_Xo <= B_Xo and A_Yo <= B_Yo and A_Xe <= B_Xe and A_Ye <= B_Ye and
	 * A_Xe > B_Xo and A_Ye > B_Yo
	 * @param i: index in the decision table of the first hyper-rectangle
	 * @param j: index in the decision table of the second hyper-rectangle
	 */
	private void detectOverlappingRules(int i, int j){
		LogicalConstraintFactory.ifThenElse(
				LogicalConstraintFactory.and(
						LogicalConstraintFactory.and(
								LogicalConstraintFactory.and(
										IntConstraintFactory.arithm(matrix[i][0], "<", matrix[j][1]),
										IntConstraintFactory.arithm(matrix[i][1], ">", matrix[j][0])
								),
								IntConstraintFactory.arithm(matrix[i][3], ">", matrix[j][2])
						),
						IntConstraintFactory.arithm(matrix[i][2], "<", matrix[j][3])
				),
				LogicalConstraintFactory.and(IntConstraintFactory.arithm(conflicts[i][j], "=", 1), IntConstraintFactory.arithm(conflicts[j][i], "=", 1)),
				LogicalConstraintFactory.and(IntConstraintFactory.arithm(conflicts[i][j], "=", 0), IntConstraintFactory.arithm(conflicts[j][i], "=", 0))
		);
	}
}
