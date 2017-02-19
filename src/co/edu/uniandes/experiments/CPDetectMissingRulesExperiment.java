package co.edu.uniandes.experiments;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.constraints.LogicalConstraintFactory;
import org.chocosolver.solver.trace.Chatterbox;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;

public class CPDetectMissingRulesExperiment {

	public CPDetectMissingRulesExperiment(){
		Solver solver = new Solver();

		IntVar A_Xo = VariableFactory.fixed("A_Xo", 0, solver);
		IntVar A_Xe = VariableFactory.fixed("A_Xe", 1000, solver);
		IntVar A_Yo = VariableFactory.fixed("A_Yo", 0, solver);
		IntVar A_Ye = VariableFactory.fixed("A_Ye", 1000, solver);

		IntVar B_Xo = VariableFactory.fixed("B_Xo", 250, solver);
		IntVar B_Xe = VariableFactory.fixed("B_Xe", 750, solver);
		IntVar B_Yo = VariableFactory.fixed("B_Yo", 4000, solver);
		IntVar B_Ye = VariableFactory.fixed("B_Ye", 5000, solver);

		IntVar C_Xo = VariableFactory.fixed("C_Xo", 500, solver);
		IntVar C_Xe = VariableFactory.fixed("C_Xe", 1500, solver);
		IntVar C_Yo = VariableFactory.fixed("C_Yo", 500, solver);
		IntVar C_Ye = VariableFactory.fixed("C_Ye", 3000, solver);

		IntVar D_Xo = VariableFactory.fixed("D_Xo", 2000, solver);
		IntVar D_Xe = VariableFactory.fixed("D_Xe", 0, solver);
		IntVar D_Yo = VariableFactory.fixed("D_Yo", 2500, solver);
		IntVar D_Ye = VariableFactory.fixed("D_Ye", 2000, solver);

		IntVar Target_Xo = VariableFactory.enumerated("Target_Xo", 0, 2500, solver);
		IntVar Target_Xe = VariableFactory.enumerated("Target_Xe", 0, 2500, solver);
		IntVar Target_Yo = VariableFactory.enumerated("Target_Yo", 0, 5000, solver);
		IntVar Target_Ye = VariableFactory.enumerated("Target_Ye", 0, 5000, solver);

		//Rules hyper-rectangle A
		LogicalConstraintFactory.ifThen(
				LogicalConstraintFactory.or(
						LogicalConstraintFactory.and(
								IntConstraintFactory.arithm(Target_Xo, ">=", A_Xo), IntConstraintFactory.arithm(Target_Xo, "<=", A_Xe)),
						LogicalConstraintFactory.and(
								IntConstraintFactory.arithm(Target_Xe, ">=", A_Xo), IntConstraintFactory.arithm(Target_Xe, "<=", A_Xe))),
				LogicalConstraintFactory.or(
						IntConstraintFactory.arithm(Target_Yo, ">", A_Ye), IntConstraintFactory.arithm(Target_Ye, "<", A_Yo)));

		LogicalConstraintFactory.ifThen(
				LogicalConstraintFactory.or(
						LogicalConstraintFactory.and(
								IntConstraintFactory.arithm(Target_Yo, ">=", A_Yo), IntConstraintFactory.arithm(Target_Yo, "<=", A_Ye)),
						LogicalConstraintFactory.and(
								IntConstraintFactory.arithm(Target_Ye, ">=", A_Yo), IntConstraintFactory.arithm(Target_Ye, "<=", A_Ye))),
				LogicalConstraintFactory.or(
						IntConstraintFactory.arithm(Target_Xo, ">", A_Xe), IntConstraintFactory.arithm(Target_Xe, "<", A_Xo)));

		//Rules hyper-rectangle B
		LogicalConstraintFactory.ifThen(
				LogicalConstraintFactory.or(
						LogicalConstraintFactory.and(
								IntConstraintFactory.arithm(Target_Xo, ">=", B_Xo), IntConstraintFactory.arithm(Target_Xo, "<=", B_Xe)),
						LogicalConstraintFactory.and(
								IntConstraintFactory.arithm(Target_Xe, ">=", B_Xo), IntConstraintFactory.arithm(Target_Xe, "<=", B_Xe))),
				LogicalConstraintFactory.or(
						IntConstraintFactory.arithm(Target_Yo, ">", B_Ye), IntConstraintFactory.arithm(Target_Ye, "<", B_Yo)));

		LogicalConstraintFactory.ifThen(
				LogicalConstraintFactory.or(
						LogicalConstraintFactory.and(
								IntConstraintFactory.arithm(Target_Yo, ">=", B_Yo), IntConstraintFactory.arithm(Target_Yo, "<=", B_Ye)),
						LogicalConstraintFactory.and(
								IntConstraintFactory.arithm(Target_Ye, ">=", B_Yo), IntConstraintFactory.arithm(Target_Ye, "<=", B_Ye))),
				LogicalConstraintFactory.or(
						IntConstraintFactory.arithm(Target_Xo, ">", B_Xe), IntConstraintFactory.arithm(Target_Xe, "<", B_Xo)));

		//Rules hyper-rectangle C
		LogicalConstraintFactory.ifThen(
				LogicalConstraintFactory.or(
						LogicalConstraintFactory.and(
								IntConstraintFactory.arithm(Target_Xo, ">=", C_Xo), IntConstraintFactory.arithm(Target_Xo, "<=", C_Xe)),
						LogicalConstraintFactory.and(
								IntConstraintFactory.arithm(Target_Xe, ">=", C_Xo), IntConstraintFactory.arithm(Target_Xe, "<=", C_Xe))),
				LogicalConstraintFactory.or(
						IntConstraintFactory.arithm(Target_Yo, ">", C_Ye), IntConstraintFactory.arithm(Target_Ye, "<", C_Yo)));

		LogicalConstraintFactory.ifThen(
				LogicalConstraintFactory.or(
						LogicalConstraintFactory.and(
								IntConstraintFactory.arithm(Target_Yo, ">=", C_Yo), IntConstraintFactory.arithm(Target_Yo, "<=", C_Ye)),
						LogicalConstraintFactory.and(
								IntConstraintFactory.arithm(Target_Ye, ">=", C_Yo), IntConstraintFactory.arithm(Target_Ye, "<=", C_Ye))),
				LogicalConstraintFactory.or(
						IntConstraintFactory.arithm(Target_Xo, ">", C_Xe), IntConstraintFactory.arithm(Target_Xe, "<", C_Xo)));

		//Rules hyper-rectangle D
		LogicalConstraintFactory.ifThen(
				LogicalConstraintFactory.or(
						LogicalConstraintFactory.and(
								IntConstraintFactory.arithm(Target_Xo, ">=", D_Xo), IntConstraintFactory.arithm(Target_Xo, "<=", A_Xe)),
						LogicalConstraintFactory.and(
								IntConstraintFactory.arithm(Target_Xe, ">=", D_Xo), IntConstraintFactory.arithm(Target_Xe, "<=", A_Xe))),
				LogicalConstraintFactory.or(
						IntConstraintFactory.arithm(Target_Yo, ">", D_Ye), IntConstraintFactory.arithm(Target_Ye, "<", A_Yo)));

		LogicalConstraintFactory.ifThen(
				LogicalConstraintFactory.or(
						LogicalConstraintFactory.and(
								IntConstraintFactory.arithm(Target_Yo, ">=", D_Yo), IntConstraintFactory.arithm(Target_Yo, "<=", D_Ye)),
						LogicalConstraintFactory.and(
								IntConstraintFactory.arithm(Target_Ye, ">=", D_Yo), IntConstraintFactory.arithm(Target_Ye, "<=", D_Ye))),
				LogicalConstraintFactory.or(
						IntConstraintFactory.arithm(Target_Xo, ">", D_Xe), IntConstraintFactory.arithm(Target_Xe, "<", D_Xo)));

		solver.post(IntConstraintFactory.arithm(Target_Xo, "<", Target_Xe));
		solver.post(IntConstraintFactory.arithm(Target_Yo, "<", Target_Ye));

		Chatterbox.showSolutions(solver);
		solver.findAllSolutions();
		Chatterbox.printStatistics(solver);
	}
}
