package co.edu.uniandes.experiments;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.constraints.LogicalConstraintFactory;
import org.chocosolver.solver.trace.Chatterbox;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;

public class CPDetectOverlappingRulesGeneric {

	private Solver solver;
	private IntVar [][] rules;
	private IntVar [][] overlaps;
	private Properties properties;
	private int rulesNumber;
	private int attrsNumber;
	private int maxRulesNumber;
	
	/**
	 * Class constructor
	 * @param propertiesPath - String with the decision rules properties path
	 * @param maxRulesNumber - int with number of initial rules to consider
	 */
	public CPDetectOverlappingRulesGeneric(String propertiesPath, int maxRulesNumber){
		this.solver = new Solver();
		this.maxRulesNumber = maxRulesNumber;
		
		//Initialize rules matrix with hyper-rectangles
		initializeMatrix(propertiesPath);
		
		//Initialize overlaps matrix to identify conflicts among rules
		initializeOverlapsMatrix();
		
		//Detect overlapping rules and fill the overlaps matrix
		detectOverlappingRules();
		
		//Diagonal has no overlaps
		setDiagonalToZero();
		
		//Solve problem
		Chatterbox.showSolutions(solver);
		solver.findAllSolutions();
		Chatterbox.printStatistics(solver);
	}
	
	/**
	 * Initialize rules matrix from properties file. N represents
	 * the number of input attributes; M represents the number of 
	 * rules; data contained in the attr[i] corresponds to space
	 * bound values (even i: lower bound - odd i: upper bound); data 
	 * contained in the rules[i][j] corresponds to bound values of an 
	 * hyper-rectangle (even j: lower bound - odd j: upper bound).
	 * @param: propertiesPath - String with the decision rules
	 * properties path
	 */
	private void initializeMatrix(String propertiesPath) {
		try {
			properties = new Properties();
			InputStream stream = new FileInputStream(propertiesPath);
			properties.load(stream);
			
			if(maxRulesNumber == -1) {
				rulesNumber = Integer.valueOf(properties.getProperty("M"));
			}
			else{
				rulesNumber = maxRulesNumber;
			}
			
			attrsNumber = Integer.valueOf(properties.getProperty("N"));
			
			rules = new IntVar[rulesNumber][2*attrsNumber];
			
			for(int i = 0; i < rulesNumber; i++){
				for(int j = 0; j < attrsNumber; j++){
					int index = 2 * j;
					int lb = Integer.valueOf(properties.getProperty("rules[" + i + "][" + index + "]"));
					int ub = Integer.valueOf(properties.getProperty("rules[" + i + "][" + (index + 1) + "]"));

					rules[i][index] = VariableFactory.fixed("Rule_" + i + "_LB" + j, lb, solver);
					rules[i][index + 1] = VariableFactory.fixed("Rule_" + i + "_UB" + j, ub, solver);
				}
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Initialize overlaps matrix. All cells are instantiated
	 * as boolean variables.
	 */
	private void initializeOverlapsMatrix(){
		overlaps = new IntVar[rulesNumber][2*attrsNumber];
		
		for(int i = 0; i < overlaps.length; i++) {
			for(int j = 0; j < overlaps.length; j++) {
				overlaps[i][j] = VariableFactory.bool("Overlaps_" + i + "_" + j, solver);
			}
		}
	}
	
	/**
	 * Detect all overlapping rules in the given decision space.
	 */
	private void detectOverlappingRules(){
		for(int i = 0; i < rules.length; i++){
			for(int j = i + 1; j < rules.length; j++){
				
				//Cases A-B, B-A)
				detectOverlappingPairRules(i,j);
			}
		}
	}
	
	/**
	 * Detect overlapping pair of hyper-rectangles:
	 * IF (for all Attr | i_Attr_LB < j_Attr_UB && i_Attr_UB > j_Attr_LB)
	 * THEN register an overlap conflict (1) ELSE register good relation (0).
	 * @param i: index in the decision table of the first 
	 * hyper-rectangle (i.e. decision rule)
	 * @param j: index in the decision table of the second 
	 * hyper-rectangle (i.e. decision rule)
	 */
	private void detectOverlappingPairRules(int i, int j){
		Constraint[] constraints = new Constraint[2*attrsNumber];
		
		for(int p = 0; p < attrsNumber; p++){			
			constraints[2*p] = IntConstraintFactory.arithm(rules[i][2*p], "<", rules[j][2*p + 1]);
			constraints[2*p + 1] = IntConstraintFactory.arithm(rules[i][2*p + 1], ">", rules[j][2*p]);
		}
		
		LogicalConstraintFactory.ifThenElse(
				LogicalConstraintFactory.and(constraints),
				LogicalConstraintFactory.and(IntConstraintFactory.arithm(overlaps[i][j], "=", 1), IntConstraintFactory.arithm(overlaps[j][i], "=", 1)),
				LogicalConstraintFactory.and(IntConstraintFactory.arithm(overlaps[i][j], "=", 0), IntConstraintFactory.arithm(overlaps[j][i], "=", 0))
		);
	}
	
	/**
	 * Set diagonal free of overlapping errors. An hyper-rectangle
	 * cannot overlap itself.
	 */
	private void setDiagonalToZero(){
		for(int i = 0; i < overlaps.length; i++) {
			solver.post(IntConstraintFactory.arithm(overlaps[i][i], "=", 0));
		}
	}
}
