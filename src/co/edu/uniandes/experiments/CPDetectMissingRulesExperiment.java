package co.edu.uniandes.experiments;

import org.chocosolver.solver.ResolutionPolicy;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.constraints.LogicalConstraintFactory;
import org.chocosolver.solver.constraints.nary.cnf.LogOp;
import org.chocosolver.solver.search.loop.monitors.SMF;
import org.chocosolver.solver.search.strategy.IntStrategyFactory;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMax;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.strategy.IntStrategy;
import org.chocosolver.solver.trace.Chatterbox;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;

public class CPDetectMissingRulesExperiment {
	private IntVar [][] matrix;
	private IntVar[][] missingRules;
	private IntVar[][] space;
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
		matrix[3][1] = VariableFactory.fixed("4_Xe", 2500, solver);
		matrix[3][2] = VariableFactory.fixed("4_Yo", 0, solver);
		matrix[3][3] = VariableFactory.fixed("4_Ye", 2000, solver);

		missingRulesNumber = VariableFactory.enumerated("MissingRulesNumber", 1, 10, solver);
		missingRules = new IntVar[matrix.length + missingRulesNumber.getValue()][4];
		//missingRules = new IntVar[5][6];
		space = new IntVar[2500][5000];
		
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
			int index = 0;
			Constraint[] constraintsXo = new Constraint[2 * missingRules.length - 2];
			Constraint[] constraintsXe = new Constraint[2 * missingRules.length - 2];
			Constraint[] constraintsYo = new Constraint[2 * missingRules.length - 2];
			Constraint[] constraintsYe = new Constraint[2 * missingRules.length - 2];
			System.out.println(2 * missingRules.length);
			for(int j = 0; j < missingRules.length; j++) {
				if(i != j){
					postRectangleConstraints(i);
					//postCoordinatesConstraintsBounds(i);
					postCoordinatesCostraints(i,j);
					//oooopostXConstraints(i,j);
					//postYConstraints(i,j);
					constraintsXo[index] = IntConstraintFactory.arithm(missingRules[i][0], "=", missingRules[j][0]);
					constraintsXo[index + 1] = IntConstraintFactory.arithm(missingRules[i][0], "=", missingRules[j][1]);
					constraintsXe[index] = IntConstraintFactory.arithm(missingRules[i][1], "=", missingRules[j][0]);
					constraintsXe[index + 1] = IntConstraintFactory.arithm(missingRules[i][1], "=", missingRules[j][1]);
					
					constraintsYo[index] = IntConstraintFactory.arithm(missingRules[i][2], "=", missingRules[j][2]);
					constraintsYo[index + 1] = IntConstraintFactory.arithm(missingRules[i][2], "=", missingRules[j][3]);
					constraintsYe[index] = IntConstraintFactory.arithm(missingRules[i][3], "=", missingRules[j][2]);
					constraintsYe[index + 1] = IntConstraintFactory.arithm(missingRules[i][3], "=", missingRules[j][3]);
					
					index+=2;
				}
			}
			System.out.println(index);
			LogicalConstraintFactory.or(constraintsXo);
			LogicalConstraintFactory.or(constraintsXe);
			LogicalConstraintFactory.or(constraintsYo);
			LogicalConstraintFactory.or(constraintsYe);
		}
		
		
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
		//solver.findAllSolutions();
		Chatterbox.printStatistics(solver);
	}
	
	private void postCoordinatesConstraintsBounds(int i){
		LogicalConstraintFactory.or(IntConstraintFactory.arithm(missingRules[i][0], "=", 0));
		LogicalConstraintFactory.or(IntConstraintFactory.arithm(missingRules[i][1], "=", 2500));
		LogicalConstraintFactory.or(IntConstraintFactory.arithm(missingRules[i][2], "=", 0));
		LogicalConstraintFactory.or(IntConstraintFactory.arithm(missingRules[i][3], "=", 5000));
	}
	
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
	
	private void postXConstraints(int i, int j){
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][0], "<=", missingRules[j][0]),
				IntConstraintFactory.arithm(missingRules[i][1], "<=", missingRules[j][0]),
				LogicalConstraintFactory.or(
						LogicalConstraintFactory.or(
								IntConstraintFactory.arithm(missingRules[i][0], ">=", missingRules[j][1]),
								IntConstraintFactory.arithm(missingRules[i][2], ">=", missingRules[j][3])
						),
						IntConstraintFactory.arithm(missingRules[i][3], "<=", missingRules[j][2])
				)
		);
	}
	
	private void postYConstraints(int i, int j){
		LogicalConstraintFactory.ifThenElse(
				IntConstraintFactory.arithm(missingRules[i][2], "<=", missingRules[j][2]),
				IntConstraintFactory.arithm(missingRules[i][3], "<=", missingRules[j][2]),
				LogicalConstraintFactory.or(
						LogicalConstraintFactory.or(
								IntConstraintFactory.arithm(missingRules[i][2], ">=", missingRules[j][3]),
								IntConstraintFactory.arithm(missingRules[i][0], ">=", missingRules[j][1])
						),
						IntConstraintFactory.arithm(missingRules[i][1], "<=", missingRules[j][0])
				)
		);
	}
	
	private void postRectangleConstraints(int i){
		solver.post(IntConstraintFactory.arithm(missingRules[i][0], "<", missingRules[i][1]));
		solver.post(IntConstraintFactory.arithm(missingRules[i][2], "<", missingRules[i][3]));
	}
}
