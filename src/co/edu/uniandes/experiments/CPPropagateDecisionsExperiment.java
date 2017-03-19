package co.edu.uniandes.experiments;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.constraints.LogicalConstraintFactory;
import org.chocosolver.solver.trace.Chatterbox;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;

public class CPPropagateDecisionsExperiment {
	public CPPropagateDecisionsExperiment(){
		Solver solver = new Solver();
		
		IntVar age = VariableFactory.enumerated("age", 0, 100, solver);
		IntVar medicalHistory = VariableFactory.enumerated("age", new int[]{0, 1}, solver);
		IntVar riskRanking = VariableFactory.enumerated("age", new int[]{0, 1, 2}, solver);
		
		//Ingresar valores en este punto
		solver.post(IntConstraintFactory.arithm(age, "=", 20));
		solver.post(IntConstraintFactory.arithm(medicalHistory, "=", 0));
		
		LogicalConstraintFactory.ifThen(LogicalConstraintFactory.and(IntConstraintFactory.arithm(age, "<", 25), IntConstraintFactory.arithm(medicalHistory, "=", 0)), IntConstraintFactory.arithm(riskRanking, "=", 2));
		LogicalConstraintFactory.ifThen(LogicalConstraintFactory.and(IntConstraintFactory.arithm(age, "<", 25), IntConstraintFactory.arithm(medicalHistory, "=", 1)), IntConstraintFactory.arithm(riskRanking, "=", 1));
		LogicalConstraintFactory.ifThen(LogicalConstraintFactory.and(IntConstraintFactory.arithm(age, ">", 60), IntConstraintFactory.arithm(medicalHistory, "=", 0)), IntConstraintFactory.arithm(riskRanking, "=", 1));
		LogicalConstraintFactory.ifThen(LogicalConstraintFactory.and(IntConstraintFactory.arithm(age, ">", 60), IntConstraintFactory.arithm(medicalHistory, "=", 1)), IntConstraintFactory.arithm(riskRanking, "=", 0));
		LogicalConstraintFactory.ifThen(LogicalConstraintFactory.and(LogicalConstraintFactory.and(IntConstraintFactory.arithm(age, ">=", 25), IntConstraintFactory.arithm(age, "<=", 60)), IntConstraintFactory.arithm(medicalHistory, "=", 0)), IntConstraintFactory.arithm(riskRanking, "=", 1));
		LogicalConstraintFactory.ifThen(LogicalConstraintFactory.and(LogicalConstraintFactory.and(IntConstraintFactory.arithm(age, ">=", 25), IntConstraintFactory.arithm(age, "<=", 60)), IntConstraintFactory.arithm(medicalHistory, "=", 1)), IntConstraintFactory.arithm(riskRanking, "=", 1));
	
		Chatterbox.showSolutions(solver);
		solver.findAllSolutions();
		System.out.println(age.getValue());
		System.out.println(medicalHistory.getValue());
		System.out.println(riskRanking.getValue());
		Chatterbox.printStatistics(solver);
	}
}
