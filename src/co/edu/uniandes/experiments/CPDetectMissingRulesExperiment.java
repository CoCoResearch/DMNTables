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
	private IntVar missingRulesNumber;
	private Solver solver;

	public CPDetectMissingRulesExperiment(){
		solver = new Solver();

		//Initialize matrix with hyper-rectangles
		initializeMatrix();
		initializeMissingRules();
		ensureBounds();
		ensureArea();
		
		SMF.limitSolution(solver, 5);
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
		matrix = new IntVar[2][4];

		matrix[0][0] = VariableFactory.fixed("1_Xo", 0, solver);
		matrix[0][1] = VariableFactory.fixed("1_Xe", 1000, solver);
		matrix[0][2] = VariableFactory.fixed("1_Yo", 0, solver);
		matrix[0][3] = VariableFactory.fixed("1_Ye", 1000, solver);

		matrix[1][0] = VariableFactory.fixed("2_Xo", 250, solver);
		matrix[1][1] = VariableFactory.fixed("2_Xe", 750, solver);
		matrix[1][2] = VariableFactory.fixed("2_Yo", 4000, solver);
		matrix[1][3] = VariableFactory.fixed("2_Ye", 5000, solver);

		/*matrix[2][0] = VariableFactory.fixed("3_Xo", 500, solver);
		matrix[2][1] = VariableFactory.fixed("3_Xe", 1500, solver);
		matrix[2][2] = VariableFactory.fixed("3_Yo", 500, solver);
		matrix[2][3] = VariableFactory.fixed("3_Ye", 3000, solver);

		matrix[3][0] = VariableFactory.fixed("4_Xo", 2000, solver);
		matrix[3][1] = VariableFactory.fixed("4_Xe", 2500, solver);
		matrix[3][2] = VariableFactory.fixed("4_Yo", 0, solver);
		matrix[3][3] = VariableFactory.fixed("4_Ye", 2000, solver);*/
	}
	
	private void initializeMissingRules(){
		missingRulesNumber = VariableFactory.fixed("MissingRulesNumber", 3, solver);
		missingRules = new IntVar[matrix.length + missingRulesNumber.getValue()][4];
		
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
			IntVar[] valuesXo = populateBoundaryValues(0,i);
			IntVar[] valuesXe = populateBoundaryValues(0,i);
			IntVar[] valuesYo = populateBoundaryValues(1,i);
			IntVar[] valuesYe = populateBoundaryValues(1,i);
			
			IntVar sumXo = VariableFactory.enumerated(i + "_SumXo", 0, 100, solver);
			IntVar sumXe = VariableFactory.enumerated(i + "_SumXe", 0, 100, solver);
			IntVar sumYo = VariableFactory.enumerated(i + "_SumYo", 0, 100, solver);
			IntVar sumYe = VariableFactory.enumerated(i + "_SumYe", 0, 100, solver);
			
			postRectangleConstraints(i);
			postXLimBoundsConstraints(i, valuesXo, valuesXe);
			postYLimBoundsConstraints(i, valuesYo, valuesYe);
			
			for(int j = 0; j < missingRules.length; j++) {
				if(i != j){
					postCoordinatesCostraints(i,j);
					postXBoundsConstraints(i,j, index, valuesXo, valuesXe);
					postYBoundsConstraints(i,j, index, valuesYo, valuesYe);
					index+=2;
				}
			}
			
			solver.post(IntConstraintFactory.sum(valuesXo, sumXo));
			solver.post(IntConstraintFactory.sum(valuesXe, sumXe));
			solver.post(IntConstraintFactory.sum(valuesYo, sumYo));
			solver.post(IntConstraintFactory.sum(valuesYe, sumYe));
			
			solver.post(IntConstraintFactory.arithm(sumXo, ">=", 1));
			solver.post(IntConstraintFactory.arithm(sumXe, ">=", 1));
			solver.post(IntConstraintFactory.arithm(sumYo, ">=", 1));
			solver.post(IntConstraintFactory.arithm(sumYe, ">=", 1));
		}
	}
	
	private IntVar[] populateBoundaryValues(int attribute, int i){
		IntVar[] values = new IntVar[2*missingRules.length - 1];

		for(int j = 0; j < values.length; j++) {
			values[j] = VariableFactory.enumerated(i + "_" + j + "_" + attribute + "Values", 0, 1, solver);
		}
		
		return values;
	}
	
	private void postRectangleConstraints(int i){
		solver.post(IntConstraintFactory.arithm(missingRules[i][0], "<", missingRules[i][1]));
		solver.post(IntConstraintFactory.arithm(missingRules[i][2], "<", missingRules[i][3]));
	}
	
	private void postXLimBoundsConstraints(int i, IntVar[] valuesXo, IntVar[] valuesXe){
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][0], "=", 0), 
				IntConstraintFactory.arithm(valuesXo[0], "=", 1), 
				IntConstraintFactory.arithm(valuesXo[0], "=", 0)
		);
		
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][1], "=", 2500), 
				IntConstraintFactory.arithm(valuesXe[0], "=", 1), 
				IntConstraintFactory.arithm(valuesXe[0], "=", 0)
		);
	}
	
	private void postYLimBoundsConstraints(int i, IntVar[] valuesYo, IntVar[] valuesYe){
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][2], "=", 0), 
				IntConstraintFactory.arithm(valuesYo[0], "=", 1), 
				IntConstraintFactory.arithm(valuesYo[0], "=", 0)
		);
		
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][3], "=", 5000), 
				IntConstraintFactory.arithm(valuesYe[0], "=", 1), 
				IntConstraintFactory.arithm(valuesYe[0], "=", 0)
		);
	}
	
	private void postXBoundsConstraints(int i, int j, int index, IntVar[] valuesXo, IntVar[] valuesXe){
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][0], "=", missingRules[j][0]), 
				IntConstraintFactory.arithm(valuesXo[index], "=", 1), 
				IntConstraintFactory.arithm(valuesXo[index], "=", 0)
		);
		
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][0], "=", missingRules[j][1]), 
				IntConstraintFactory.arithm(valuesXo[index + 1], "=", 1), 
				IntConstraintFactory.arithm(valuesXo[index + 1], "=", 0)
		);
		
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][1], "=", missingRules[j][0]), 
				IntConstraintFactory.arithm(valuesXe[index], "=", 1), 
				IntConstraintFactory.arithm(valuesXe[index], "=", 0)
		);
		
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][1], "=", missingRules[j][1]), 
				IntConstraintFactory.arithm(valuesXe[index + 1], "=", 1), 
				IntConstraintFactory.arithm(valuesXe[index + 1], "=", 0)
		);
	}
	
	private void postYBoundsConstraints(int i, int j, int index, IntVar[] valuesYo, IntVar[] valuesYe){
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][2], "=", missingRules[j][2]), 
				IntConstraintFactory.arithm(valuesYo[index], "=", 1), 
				IntConstraintFactory.arithm(valuesYo[index], "=", 0)
		);
		
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][2], "=", missingRules[j][3]), 
				IntConstraintFactory.arithm(valuesYo[index + 1], "=", 1), 
				IntConstraintFactory.arithm(valuesYo[index + 1], "=", 0)
		);
		
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][3], "=", missingRules[j][2]), 
				IntConstraintFactory.arithm(valuesYe[index], "=", 1), 
				IntConstraintFactory.arithm(valuesYe[index], "=", 0)
		);
		
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][3], "=", missingRules[j][3]), 
				IntConstraintFactory.arithm(valuesYe[index + 1], "=", 1), 
				IntConstraintFactory.arithm(valuesYe[index + 1], "=", 0)
		);
	}
	
	//TODO: ARREGLAR!! 
	private void postCoordinatesCostraints(int i, int j) {

		LogicalConstraintFactory.ifThen(
				LogicalConstraintFactory.and(
						IntConstraintFactory.arithm(missingRules[i][0], ">=", missingRules[j][0]),
						IntConstraintFactory.arithm(missingRules[i][2], ">=", missingRules[j][2])), 
				LogicalConstraintFactory.or(
						IntConstraintFactory.arithm(missingRules[i][0], ">=", missingRules[j][1]),
						IntConstraintFactory.arithm(missingRules[i][2], ">=", missingRules[j][3]))
		);
		
		LogicalConstraintFactory.ifThen(
				LogicalConstraintFactory.and(
						IntConstraintFactory.arithm(missingRules[i][0], ">=", missingRules[j][0]),
						IntConstraintFactory.arithm(missingRules[i][2], "<", missingRules[j][2])), 
				LogicalConstraintFactory.or(
						IntConstraintFactory.arithm(missingRules[i][0], ">=", missingRules[j][1]),
						IntConstraintFactory.arithm(missingRules[i][3], "<=", missingRules[j][2]))
		);
		
		LogicalConstraintFactory.ifThen(
				LogicalConstraintFactory.and(
						IntConstraintFactory.arithm(missingRules[i][0], "<", missingRules[j][0]),
						IntConstraintFactory.arithm(missingRules[i][2], ">=", missingRules[j][2])), 
				LogicalConstraintFactory.or(
						IntConstraintFactory.arithm(missingRules[i][1], "<=", missingRules[j][0]),
						IntConstraintFactory.arithm(missingRules[i][2], ">=", missingRules[j][3]))
		);
		
		LogicalConstraintFactory.ifThen(
				LogicalConstraintFactory.and(
						IntConstraintFactory.arithm(missingRules[i][0], "<", missingRules[j][0]),
						IntConstraintFactory.arithm(missingRules[i][2], "<", missingRules[j][2])), 
				LogicalConstraintFactory.or(
						IntConstraintFactory.arithm(missingRules[i][1], "<=", missingRules[j][0]),
						IntConstraintFactory.arithm(missingRules[i][3], "<=", missingRules[j][2]))
		);
	}
	
	private void ensureArea(){
		IntVar[] areas = new IntVar[missingRules.length];
		IntVar areasSum = VariableFactory.fixed("SumAreas", 2500*5000, solver);
		
		for(int i = 0; i < missingRules.length; i++) {
			IntVar xDistanceSum = VariableFactory.enumerated(i + "_XDistance", 0, 2500, solver);
			IntVar yDistanceSum = VariableFactory.enumerated(i + "_YDistance", 0, 5000, solver);
			
			IntVar[] xDistance = new IntVar[2];
			xDistance[0] = VariableFactory.minus(missingRules[i][0]);
			xDistance[1] = missingRules[i][1];
			
			IntVar[] yDistance = new IntVar[2];
			yDistance[0] = VariableFactory.minus(missingRules[i][2]);
			yDistance[1] = missingRules[i][3];
			
			solver.post(IntConstraintFactory.sum(xDistance, xDistanceSum));
			solver.post(IntConstraintFactory.sum(yDistance, yDistanceSum));
			
			areas[i] = VariableFactory.enumerated(i + "_Area", 0, 2500*5000, solver);
			solver.post(IntConstraintFactory.times(xDistanceSum, yDistanceSum, areas[i]));
		}
		
		solver.post(IntConstraintFactory.sum(areas, ">=", areasSum));
	}
}
