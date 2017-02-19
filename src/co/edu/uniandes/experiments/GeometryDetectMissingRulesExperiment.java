package co.edu.uniandes.experiments;

import java.util.ArrayList;
import java.util.List;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.constraints.LogicalConstraintFactory;
import org.chocosolver.solver.constraints.set.SetConstraintsFactory;
import org.chocosolver.solver.trace.Chatterbox;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.SetVar;
import org.chocosolver.solver.variables.VariableFactory;

public class GeometryDetectMissingRulesExperiment {
	
	private IntVar[][] decisionTable;
	
	public GeometryDetectMissingRulesExperiment(){
		Solver solver = new Solver();
		
		IntVar age = VariableFactory.enumerated("age", 0, 100, solver);
		IntVar medicalHistory = VariableFactory.enumerated("medicalHistory", new int[]{0, 1}, solver);
		
		decisionTable = new IntVar[6][2];
		
		decisionTable[0][0] = VariableFactory.enumerated("1-age", 61, 100, solver);
		decisionTable[0][1] = VariableFactory.fixed("1-medicalHistory", 0, solver);
		
		decisionTable[1][0] = VariableFactory.enumerated("2-age", 61, 100, solver);
		decisionTable[1][1] = VariableFactory.fixed("2-medicalHistory", 1, solver);
		
		decisionTable[2][0] = VariableFactory.enumerated("3-age", 25, 60, solver);
		decisionTable[2][1] = VariableFactory.fixed("3-medicalHistory", 0, solver);
		
		decisionTable[3][0] = VariableFactory.enumerated("4-age", 25, 60, solver);
		decisionTable[3][1] = VariableFactory.fixed("4-medicalHistory", 1, solver);
		
		decisionTable[4][0] = VariableFactory.enumerated("5-age", 0, 24, solver);
		decisionTable[4][1] = VariableFactory.fixed("5-medicalHistory", 0, solver);
		
		decisionTable[5][0] = VariableFactory.enumerated("6-age", 0, 24, solver);
		decisionTable[5][1] = VariableFactory.fixed("6-medicalHistory", 1, solver);
		
		/*SetVar[] vars = new SetVar[3];
		vars[0] = VariableFactory.set("1-age", 0, 24, solver);
		vars[1] = VariableFactory.set("2-age", 20, 60, solver);
		vars[2] = VariableFactory.set("3-age", 20, 100, solver);*/
		
		Chatterbox.showSolutions(solver);
		solver.findSolution();
		Chatterbox.printStatistics(solver);
	}
	
	private List<IntVar> detectOverlappingRules() {
		List<IntVar> overlappingRuleList = new ArrayList<IntVar>();
		
		int i = 0;
		findOverlappingRulesRecursive(i, overlappingRuleList);
		
		return overlappingRuleList;
	}
	
	private void findOverlappingRulesRecursive(int i, List overlappingRulesList) {
		
		if(i == decisionTable[0].length){

		}
		else{
			List<Bound> currentBoundsList = sortArray(i);
			List<Bound> sortedBounds = currentBoundsList;
			for(Bound bound : sortedBounds){
				if(bound.getBoundType() == "UB"){
					findOverlappingRulesRecursive(i+1, overlappingRulesList);
					currentBoundsList.remove(bound);
				}
			}
		}
	}
	
	private List<IntVar> generateRuleList(int index){
		//Get values of a given column 
		List<IntVar> rulesList = new ArrayList<IntVar>();
		for(int j = 0; j < decisionTable.length; j++){
			rulesList.add(decisionTable[index][j]);
		}
		return rulesList;
	}
	
	/**
	 * Counting sort algorithm for sorting array of IntVars
	 * depending on their lower bound.
	 * Algorithm based on solution presented in:
	 * http://www.javacodex.com/Sorting/Counting-Sort
	 * @param rulesList
	 */
	private List<Bound> sortArray(int index) {
		List<IntVar> rulesList = generateRuleList(index);
		List<Bound> sorted = new ArrayList<Bound>();
		int min = rulesList.get(0).getLB();
		int max = rulesList.get(0).getUB();
		
		for(int i = 0; i < rulesList.size(); i++){
			if(rulesList.get(i).getLB() < min){
				min = rulesList.get(i).getLB();
			}
			else if(rulesList.get(i).getUB() > max){
				max = rulesList.get(i).getUB();
			}
		}
		
		int[] count = new int[max - min + 1];
		
		//Store count
		for(int i = 0; i < rulesList.size(); i++){
			count[rulesList.get(i).getLB() - min]++; 
			count[rulesList.get(i).getUB() - min]++;
		}
		
		//Add previous counts
		count[0]--;
		for(int i = 1; i < count.length; i++){
			count[i] += count[i-1];
		}
		
		//Sort array based on LB and UB.
		for(int i = rulesList.size() - 1; i >= 0; i--){
			sorted.add(count[rulesList.get(i).getLB() - min]--, new Bound(index, i, "LB", rulesList.get(i).getLB()));
			sorted.add(count[rulesList.get(i).getUB() - min]--, new Bound(index, i, "UB", rulesList.get(i).getUB()));
		}
		
		return sorted;
	}
}
