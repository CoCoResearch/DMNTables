package co.edu.uniandes.experiments;

import org.chocosolver.solver.ResolutionPolicy;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.constraints.LogicalConstraintFactory;
import org.chocosolver.solver.search.loop.monitors.SMF;
import org.chocosolver.solver.trace.Chatterbox;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;

public class CPDetectMissingRulesExperiment {
	private IntVar [][] matrix;
	private IntVar[][] missingRules;
	//private IntVar missingRulesNumber;
	private Solver solver;

	public CPDetectMissingRulesExperiment(){
		solver = new Solver();

		//Initialize matrix with hyper-rectangles
		initializeMatrix();
		initializeMissingRules();
		ensureBounds();
		
		SMF.limitSolution(solver, 10);
		Chatterbox.showSolutions(solver);
		solver.findAllSolutions();
		
		for(int i = 0; i < missingRules.length; i++){
			System.out.println("Entra" + i);
			System.out.println(missingRules[i][0].getValue());
			System.out.println(missingRules[i][1].getValue());
			System.out.println(missingRules[i][2].getValue());
			System.out.println(missingRules[i][3].getValue());
		}
		
		Chatterbox.printStatistics(solver);
	}
	
	private void initializeMatrix() {
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
		matrix[3][1] = VariableFactory.fixed("4_Xe", 2500, solver);
		matrix[3][2] = VariableFactory.fixed("4_Yo", 0, solver);
		matrix[3][3] = VariableFactory.fixed("4_Ye", 2000, solver);
	}
	
	private void initializeMissingRules(){
		//missingRulesNumber = VariableFactory.enumerated("MissingRulesNumber", 1, 10, solver);
		//missingRules = new IntVar[matrix.length + missingRulesNumber.getValue()][4];
		missingRules = new IntVar[matrix.length + 1][4];
		
		for(int i = 0; i < missingRules.length; i++){
			if(i < matrix.length){
				missingRules[i][0] = matrix[i][0];
				missingRules[i][1] = matrix[i][1];
				missingRules[i][2] = matrix[i][2];
				missingRules[i][3] = matrix[i][3];
			}
			else{
				missingRules[i][0] = VariableFactory.enumerated(i + "_Xo", 0, 2500, solver);
				missingRules[i][1] = VariableFactory.enumerated(i + "_Xe", 0, 2500, solver);
				missingRules[i][2] = VariableFactory.enumerated(i + "_Yo", 0, 5000, solver);
				missingRules[i][3] = VariableFactory.enumerated(i + "_Ye", 0, 5000, solver);
			}
		}
	}
	
	private void ensureBounds() {
		for(int i = matrix.length; i < missingRules.length; i++) {
			int index = 1;
			IntVar[] constraintsXo = new IntVar[2 * missingRules.length - 2 + 1];
			IntVar[] constraintsXe = new IntVar[2 * missingRules.length - 2 + 1];
			IntVar[] constraintsYo = new IntVar[2 * missingRules.length - 2 + 1];
			IntVar[] constraintsYe = new IntVar[2 * missingRules.length - 2 + 1];
			
			IntVar sumXo = VariableFactory.enumerated(i + "_SumXo", 0, 1, solver);
			IntVar sumXe = VariableFactory.enumerated(i + "_SumXe", 0, 1, solver);
			IntVar sumYo = VariableFactory.enumerated(i + "_SumYo", 0, 1, solver);
			IntVar sumYe = VariableFactory.enumerated(i + "_SumYe", 0, 1, solver);
			
			constraintsXo[0] = VariableFactory.enumerated("Lim_XoValues", 0, 1, solver);
			constraintsXe[0] = VariableFactory.enumerated("Lim_XeValues", 0, 1, solver);
			constraintsYo[0] = VariableFactory.enumerated("Lim_YoValues", 0, 1, solver);
			constraintsYe[0] = VariableFactory.enumerated("Lim_YeValues", 0, 1, solver);
			
			postRectangleConstraints(i);
			postXLimBoundsConstraints(i, constraintsXo, constraintsXe);
			postYLimBoundsConstraints(i, constraintsYo, constraintsYe);
			
			for(int j = 0; j < missingRules.length; j++) {
				if(i != j){
					constraintsXo[index] = VariableFactory.enumerated(i + "_" + j + "_" + index + "_XoValues", 0, 1, solver);
					constraintsXo[index + 1] = VariableFactory.enumerated(i + "_" + j + "_" + (index + 1) + "_XoValues", 0, 1, solver);
					constraintsXe[index] = VariableFactory.enumerated(i + "_" + j + "_" + index + "_XeValues", 0, 1, solver);
					constraintsXe[index + 1] = VariableFactory.enumerated(i + "_" + j + "_" + (index + 1) + "_XeValues", 0, 1, solver);
					
					constraintsYo[index] = VariableFactory.enumerated(i + "_" + j + "_" + index + "_YoValues", 0, 1, solver);
					constraintsYo[index + 1] = VariableFactory.enumerated(i + "_" + j + "_" + (index + 1) + "_YoValues", 0, 1, solver);
					constraintsYe[index] = VariableFactory.enumerated(i + "_" + j + "_" + index + "_YeValues", 0, 1, solver);
					constraintsYe[index + 1] = VariableFactory.enumerated(i + "_" + j + "_" + (index + 1) + "_YeValues", 0, 1, solver);
					
					postCoordinatesCostraints(i,j);
					postXBoundsConstraints(i,j, index, constraintsXo, constraintsXe);
					postYBoundsConstraints(i,j, index, constraintsYo, constraintsYe);
					
					index+=2;
				}
			}
			
			
			solver.post(IntConstraintFactory.sum(constraintsXo, sumXo));
			solver.post(IntConstraintFactory.sum(constraintsXe, sumXe));
			solver.post(IntConstraintFactory.sum(constraintsYo, sumYo));
			solver.post(IntConstraintFactory.sum(constraintsYe, sumYe));
			
			solver.post(IntConstraintFactory.arithm(sumXo, ">=", 1));
			solver.post(IntConstraintFactory.arithm(sumXe, ">=", 1));
			solver.post(IntConstraintFactory.arithm(sumYo, ">=", 1));
			solver.post(IntConstraintFactory.arithm(sumYe, ">=", 1));
		}
	}
	
	//TODO: ARREGLAR!! 
	private void postCoordinatesCostraints(int i, int j) {
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][0], ">", missingRules[j][0]), 
				IntConstraintFactory.arithm(missingRules[i][0], ">=", missingRules[j][1]), 
				IntConstraintFactory.arithm(missingRules[i][1], "<=", missingRules[j][0])	
		);
		
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][2], ">", missingRules[j][2]), 
				IntConstraintFactory.arithm(missingRules[i][2], ">=", missingRules[j][3]), 
				IntConstraintFactory.arithm(missingRules[i][3], "<=", missingRules[j][2])
		);
	}
	
	private void postXLimBoundsConstraints(int i, IntVar[] constraintsXo, IntVar[] constraintsXe){
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][0], "=", 0), 
				IntConstraintFactory.arithm(constraintsXo[0], "=", 1), 
				IntConstraintFactory.arithm(constraintsXo[0], "=", 0)
		);
		
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][1], "=", 2500), 
				IntConstraintFactory.arithm(constraintsXe[0], "=", 1), 
				IntConstraintFactory.arithm(constraintsXe[0], "=", 0)
		);
	}
	
	private void postXBoundsConstraints(int i, int j, int index, IntVar[] constraintsXo, IntVar[] constraintsXe){
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][0], "=", missingRules[j][0]), 
				IntConstraintFactory.arithm(constraintsXo[index], "=", 1), 
				IntConstraintFactory.arithm(constraintsXo[index], "=", 0)
		);
		
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][0], "=", missingRules[j][1]), 
				IntConstraintFactory.arithm(constraintsXo[index + 1], "=", 1), 
				IntConstraintFactory.arithm(constraintsXo[index + 1], "=", 0)
		);
		
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][1], "=", missingRules[j][0]), 
				IntConstraintFactory.arithm(constraintsXe[index], "=", 1), 
				IntConstraintFactory.arithm(constraintsXe[index], "=", 0)
		);
		
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][1], "=", missingRules[j][1]), 
				IntConstraintFactory.arithm(constraintsXe[index + 1], "=", 1), 
				IntConstraintFactory.arithm(constraintsXe[index + 1], "=", 0)
		);
	}
	
	private void postYLimBoundsConstraints(int i, IntVar[] constraintsYo, IntVar[] constraintsYe){
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][0], "=", 0), 
				IntConstraintFactory.arithm(constraintsYo[0], "=", 1), 
				IntConstraintFactory.arithm(constraintsYo[0], "=", 0)
		);
		
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][1], "=", 5000), 
				IntConstraintFactory.arithm(constraintsYe[0], "=", 1), 
				IntConstraintFactory.arithm(constraintsYe[0], "=", 0)
		);
	}
	
	private void postYBoundsConstraints(int i, int j, int index, IntVar[] constraintsYo, IntVar[] constraintsYe){
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][2], "=", missingRules[j][2]), 
				IntConstraintFactory.arithm(constraintsYo[index], "=", 1), 
				IntConstraintFactory.arithm(constraintsYo[index], "=", 0)
		);
		
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][2], "=", missingRules[j][3]), 
				IntConstraintFactory.arithm(constraintsYo[index + 1], "=", 1), 
				IntConstraintFactory.arithm(constraintsYo[index + 1], "=", 0)
		);
		
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][3], "=", missingRules[j][2]), 
				IntConstraintFactory.arithm(constraintsYe[index], "=", 1), 
				IntConstraintFactory.arithm(constraintsYe[index], "=", 0)
		);
		
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][3], "=", missingRules[j][3]), 
				IntConstraintFactory.arithm(constraintsYe[index + 1], "=", 1), 
				IntConstraintFactory.arithm(constraintsYe[index + 1], "=", 0)
		);
	}
	
	private void postRectangleConstraints(int i){
		solver.post(IntConstraintFactory.arithm(missingRules[i][0], "<", missingRules[i][1]));
		solver.post(IntConstraintFactory.arithm(missingRules[i][2], "<", missingRules[i][3]));
	}
}
