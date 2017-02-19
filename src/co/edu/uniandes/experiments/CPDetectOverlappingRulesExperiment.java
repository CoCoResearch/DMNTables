package co.edu.uniandes.experiments;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.constraints.LogicalConstraintFactory;
import org.chocosolver.solver.trace.Chatterbox;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;

public class CPDetectOverlappingRulesExperiment {

	public CPDetectOverlappingRulesExperiment(){
		Solver solver = new Solver();
		
		IntVar [][] matrix = new IntVar[4][4];
		
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
		
		IntVar [][] conflicts = new IntVar[4][4];
		
		for(int i = 0; i < conflicts.length; i++) {
			for(int j = 0; j < conflicts.length; j++) {
				conflicts[i][j] = VariableFactory.bool(i + "_" + j, solver);
			}
		}

		/*IntVar Target_Xo = VariableFactory.enumerated("Target_Xo", 0, 2500, solver);
		IntVar Target_Xe = VariableFactory.enumerated("Target_Xe", 0, 2500, solver);
		IntVar Target_Yo = VariableFactory.enumerated("Target_Yo", 0, 5000, solver);
		IntVar Target_Ye = VariableFactory.enumerated("Target_Ye", 0, 5000, solver);*/
		
		//BoolVar scenario1 = VariableFactory.bool("S1", solver);
		
		/*LogicalConstraintFactory.ifThenElse(
				LogicalConstraintFactory.and(
						LogicalConstraintFactory.and(
								LogicalConstraintFactory.and(
										LogicalConstraintFactory.and(
												LogicalConstraintFactory.and(IntConstraintFactory.arithm(matrix[0][1], "<=", matrix[1][1]),
													IntConstraintFactory.arithm(matrix[0][0], "<=", matrix[1][0])),
												IntConstraintFactory.arithm(matrix[0][2], "<=", matrix[1][2])
										),
										IntConstraintFactory.arithm(matrix[0][3], "<=", matrix[1][3])
								),
								IntConstraintFactory.arithm(matrix[0][1], ">", matrix[1][0])
						),
						IntConstraintFactory.arithm(matrix[0][3], ">", matrix[1][2])
				),
				
				LogicalConstraintFactory.and(
					LogicalConstraintFactory.and(
						LogicalConstraintFactory.and(
								LogicalConstraintFactory.and(
										LogicalConstraintFactory.and(IntConstraintFactory.arithm(Target_Xe, "=", matrix[0][1]),
											IntConstraintFactory.arithm(Target_Xo, "=", matrix[1][0])),
										IntConstraintFactory.arithm(Target_Yo, ">=", matrix[1][2])	
								),
								IntConstraintFactory.arithm(Target_Ye, "<=", matrix[0][3])
						),
						IntConstraintFactory.arithm(scenario1, "=", 1)
					),
					LogicalConstraintFactory.and(IntConstraintFactory.arithm(conflicts[0][1], "=", 1), IntConstraintFactory.arithm(conflicts[1][0], "=", 1))
				),
				IntConstraintFactory.arithm(scenario1, "=", 0)
		);*/
		
		for(int i = 0; i < matrix.length; i++){
			for(int j = i + 1; j < matrix.length; j++){
				LogicalConstraintFactory.ifThenElse(
						LogicalConstraintFactory.and(
								LogicalConstraintFactory.and(
										LogicalConstraintFactory.and(
												LogicalConstraintFactory.and(
														LogicalConstraintFactory.and(IntConstraintFactory.arithm(matrix[i][1], "<=", matrix[j][1]),
															IntConstraintFactory.arithm(matrix[i][0], "<=", matrix[j][0])),
														IntConstraintFactory.arithm(matrix[i][2], "<=", matrix[j][2])
												),
												IntConstraintFactory.arithm(matrix[i][3], "<=", matrix[j][3])
										),
										IntConstraintFactory.arithm(matrix[i][1], ">", matrix[j][0])
								),
								IntConstraintFactory.arithm(matrix[i][3], ">", matrix[j][2])
						),
						LogicalConstraintFactory.and(IntConstraintFactory.arithm(conflicts[i][j], "=", 1), IntConstraintFactory.arithm(conflicts[j][i], "=", 1)),
						LogicalConstraintFactory.and(IntConstraintFactory.arithm(conflicts[i][j], "=", 0), IntConstraintFactory.arithm(conflicts[j][i], "=", 0))
				);
			}
		}
		
		for(int i = 0; i < conflicts.length; i++) {
			solver.post(IntConstraintFactory.arithm(conflicts[i][i], "=", 0));
		}
		
		//solver.post(IntConstraintFactory.arithm(scenario1, "=", 1));
		/*solver.post(IntConstraintFactory.arithm(Target_Xo, "<", Target_Xe));
		solver.post(IntConstraintFactory.arithm(Target_Yo, "<", Target_Ye));*/
		
		Chatterbox.showSolutions(solver);
		solver.findAllSolutions();
		Chatterbox.printStatistics(solver);
	}
}
