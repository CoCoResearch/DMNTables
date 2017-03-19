package co.edu.uniandes.analysis;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.constraints.LogicalConstraintFactory;
import org.chocosolver.solver.search.loop.monitors.SMF;
import org.chocosolver.solver.search.loop.monitors.SearchMonitorFactory;
import org.chocosolver.solver.trace.Chatterbox;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;
import org.chocosolver.solver.variables.VariableFactory;

public class CPDetectMissingRulesGeneric {
	
	//-------------------------------------------
	//ATTRIBUTES
	//-------------------------------------------
	
	/**
	 * CP solver
	 */
	private Solver solver;
	
	/**
	 * Rules matrix
	 */
	private IntVar [][] rules;
	
	/**
	 * Missing rules matrix
	 */
	private IntVar[][] missingRules;
	
	/**
	 * Overlaps matrix
	 */
	private IntVar [][] overlaps;
	
	/**
	 * Array of the N-dimensional space and identified
	 * hyper-rectangles bounds
	 */
	private int[] bounds;
	
	/**
	 * Properties file with the selected decision table
	 */
	private Properties properties;
	
	/**
	 * Number of missing rules to find
	 */
	private int missingRulesNumber;
	
	/**
	 * Number of rules in the decision table
	 */
	private int rulesNumber;
	
	/**
	 * Number of input attributes in the decision table
	 */
	private int attrsNumber;
	
	/**
	 * Number of rules to evaluate
	 */
	private int maxRulesNumber;

	
	//-------------------------------------------
	//METHODS
	//-------------------------------------------
	
	/**
	 * Class constructor
	 * @param propertiesPath - String with the decision rules properties path
	 * @param missingRulesNumber - int with the number of missing rules to 
	 * identify (K)
	 * @param maxRulesNumber - int with number of initial rules to consider
	 */
	public CPDetectMissingRulesGeneric(String propertiesPath, int maxRulesNumber, int missingRulesNumber, int maxSolutions){
		this.solver = new Solver();
		this.missingRulesNumber = missingRulesNumber;
		this.maxRulesNumber = maxRulesNumber;

		//Initialize rules matrix with hyper-rectangles
		initializeMatrix(propertiesPath);
		
		//Initialize missingRules matrix with K hyper-rectangles
		initializeMissingRules();
		
		//Initialize overlaps matrix to identify conflicts among rules
		initializeOverlapsMatrix();
		
		//Detect overlapping rules and fill the overlaps matrix
		detectOverlappingRules();
		
		//Diagonal has no overlaps
		setDiagonalToZero();
		
		//Ensure the complete decision space area is filled with hyper-rectangles
		ensureArea();
		
		//Set solutions limit
		if(maxSolutions != -1){
			SearchMonitorFactory.limitSolution(solver, maxSolutions);
		}
		else{
			SearchMonitorFactory.limitSolution(solver, 20);
		}
		
		//Solve problem
		Chatterbox.showSolutions(solver);
		solver.findAllSolutions();
		Chatterbox.printStatistics(solver);
		
		Variable[] vars = solver.getVars();
		
		for(int i = 0; i < vars.length; i++) {
			System.out.println(vars[i]);
		}
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
			bounds = new int[2*attrsNumber];
			
			for(int i = 0; i < attrsNumber; i++){
				int lb = Integer.valueOf(properties.getProperty("attr[" + (2*i) + "]"));
				int ub = Integer.valueOf(properties.getProperty("attr[" + (2*i + 1) + "]"));
				
				bounds[2*i] = lb;
				bounds[2*i + 1] = ub;
			}
			
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
	 * Initialize missing rules matrix based in the K number
	 * given by parameter during the object construction.
	 */
	private void initializeMissingRules(){
		missingRules = new IntVar[rules.length + missingRulesNumber][2*attrsNumber];
		
		for(int i = 0; i < missingRules.length; i++){
			if(i < rules.length){
				for(int p = 0; p < attrsNumber; p++){
					missingRules[i][2*p] = rules[i][2*p];
					missingRules[i][2*p + 1] = rules[i][2*p + 1];
					solver.post(IntConstraintFactory.arithm(missingRules[i][2*p], "<", missingRules[i][2*p + 1]));
				}
			}
			else{
				for(int p = 0; p < attrsNumber; p++){
					missingRules[i][2*p] = VariableFactory.enumerated("Rule_" + i + "_LB" + p, bounds[2*p], bounds[2*p + 1], solver);
					missingRules[i][2*p + 1] = VariableFactory.enumerated("Rule_" + i + "_UB" + p, bounds[2*p], bounds[2*p + 1], solver);
					solver.post(IntConstraintFactory.arithm(missingRules[i][2*p], "<", missingRules[i][2*p + 1]));
				}
			}
			
		}
	}
	
	/**
	 * Initialize overlaps matrix. All cells are instantiated
	 * as boolean variables.
	 */
	private void initializeOverlapsMatrix(){
		overlaps = new IntVar[missingRules.length][missingRules.length];
		
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
		for(int i = rulesNumber; i < missingRules.length; i++){
			for(int j = 0; j < missingRules.length; j++){
				if(i != j){
					detectOverlappingPairRules(i,j);
				}
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
		int binomial = getBinomialCoefficient();
		BoolVar[] overlappingVars = new BoolVar[binomial];
		Constraint[] constraints = new Constraint[binomial];
		int index = 0;
		
		for(int p = 0; p < attrsNumber - 1; p++){	
			Constraint[] innerConstraints = new Constraint[4];
			
			for(int q = p + 1; q < attrsNumber; q++){
				innerConstraints[0] = IntConstraintFactory.arithm(missingRules[i][2*p], "<", missingRules[j][2*p + 1]);
				innerConstraints[1] = IntConstraintFactory.arithm(missingRules[i][2*p + 1], ">", missingRules[j][2*p]);
				innerConstraints[2] = IntConstraintFactory.arithm(missingRules[i][2*q], "<", missingRules[j][2*q + 1]);
				innerConstraints[3] = IntConstraintFactory.arithm(missingRules[i][2*q + 1], ">", missingRules[j][2*q]);
				
				overlappingVars[index] = VariableFactory.bool("InnerOverlaps_" + i + "_" + j + "_Attributes_" + p + "_" + q, solver);
				constraints[index] = IntConstraintFactory.arithm(overlappingVars[index], "=", 1);
				
				LogicalConstraintFactory.ifThenElse(
						LogicalConstraintFactory.and(innerConstraints),
						LogicalConstraintFactory.and(IntConstraintFactory.arithm(overlappingVars[index] , "=", 1)),
						LogicalConstraintFactory.and(IntConstraintFactory.arithm(overlappingVars[index] , "=", 0))
				); 	
				
				index++;
			}
		}
		
		LogicalConstraintFactory.ifThenElse(
				LogicalConstraintFactory.and(overlappingVars),
				LogicalConstraintFactory.and(IntConstraintFactory.arithm(overlaps[i][j], "=", 1), IntConstraintFactory.arithm(overlaps[j][i], "=", 1)),
				LogicalConstraintFactory.and(IntConstraintFactory.arithm(overlaps[i][j], "=", 0), IntConstraintFactory.arithm(overlaps[j][i], "=", 0))
		);
		
		solver.post(LogicalConstraintFactory.and(IntConstraintFactory.arithm(overlaps[i][j], "=", 0), IntConstraintFactory.arithm(overlaps[j][i], "=", 0)));
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
	
	/**
	 * Ensure the complete decision space area is filled with hyper-rectangles.
	 * Hyper-rectangles areas are calculated and compared against the space
	 * total area.
	 */
	private void ensureArea(){
		IntVar[] areas = new IntVar[missingRules.length];
		int totalArea = calculateTotalArea();
		IntVar areasSum = VariableFactory.fixed("SumAreas", totalArea, solver);
		
		for(int i = 0; i < missingRules.length; i++) {
			IntVar[] distances = new IntVar[attrsNumber];
			areas[i] = VariableFactory.enumerated("Area_" + i, 0, totalArea, solver);
			
			for(int p = 0; p < attrsNumber; p++) {
				IntVar[] distance = new IntVar[2];
				distance[0] = VariableFactory.minus(missingRules[i][2*p]);
				distance[1] = missingRules[i][2*p + 1];
				
				distances[p] = VariableFactory.enumerated("Distance_" + p + "_Rule_" + i, bounds[2*p], bounds[2*p + 1], solver);
				
				solver.post(IntConstraintFactory.sum(distance, distances[p]));
			}
			
			for(int p = 1; p < attrsNumber; p++) {
				if(p == 1){
					solver.post(IntConstraintFactory.times(distances[p - 1], distances[p], areas[i]));
				}
				else{
					solver.post(IntConstraintFactory.times(areas[i], distances[p], areas[i]));
				}
			}	
		}
		
		solver.post(IntConstraintFactory.sum(areas, ">=", areasSum));
	}
	
	/**
	 * Calculate total area of the decision space. This method
	 * considers the upper bounds of each input attribute.
	 * @return totalArea - int with the calculated space area
	 */
	public int calculateTotalArea(){
		int totalArea = 0;
		
		for(int p = 0; p < attrsNumber; p++){
			if(totalArea == 0){
				totalArea = bounds[2*p + 1];
			}
			else{
				totalArea *= bounds[2*p + 1];
			}
		}
		
		return totalArea;
	}
	
	//https://rosettacode.org/wiki/Evaluate_binomial_coefficients#Java
	private int getBinomialCoefficient(){
		int n = attrsNumber;
		int k = 2;
		int binomial = 1;
		
		if(k > n - k){
			k =  n - k;
		}
		
		for(int i = 1, j = n; i <= k; i++, j--){
			binomial = (binomial * j) / i;
		}
		
		return binomial;
	}
}
