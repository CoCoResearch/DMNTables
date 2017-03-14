package co.edu.uniandes.experiments;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.IntConstraintFactory;
import org.chocosolver.solver.constraints.LogicalConstraintFactory;
import org.chocosolver.solver.trace.Chatterbox;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VariableFactory;

public class CPPropagateDecisionsGeneric {
	//-------------------------------------------
	//ATTRIBUTES
	//-------------------------------------------

	/**
	 * CP solver
	 */
	private Solver solver;

	/**
	 * Hash map with attributes identified by its name
	 */
	private HashMap<String, IntVar> attributes;

	/**
	 * Properties file with the selected decision table
	 */
	private Properties properties;


	//-------------------------------------------
	//METHODS
	//-------------------------------------------

	/**
	 * Class constructor.
	 * @param propertiesPath - String with the FSG properties path
	 * @param configurationPath - String with the configuration properties path
	 */
	public CPPropagateDecisionsGeneric(String propertiesPath, String configurationPath){
		this.solver = new Solver();

		//Load the FSG from a properties file
		loadProperties(propertiesPath);
		
		//Initialize FSG attributes
		initializeAttributes();
		
		//Post If-Then rules
		postRules();
		
		//Post hard constraints related to config selection
		postConfig(configurationPath);
		
		//Solve the problem
		solve();
	}

	/**
	 * Load properties file with FSG definition.
	 * @param propertiesPath - String with the FSG properties path
	 */
	private void loadProperties(String propertiesPath){
		try{
			properties = new Properties();
			InputStream stream = new FileInputStream(propertiesPath);
			properties.load(stream);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Initialize FSG attributes based on the input properties
	 * file. CSP domains and variables are defined. 
	 */
	private void initializeAttributes() {
		String attributesString = properties.getProperty("attributes");
		String[] attributesArray = attributesString.split(",");
		attributes = new HashMap<String, IntVar>();

		for(int i = 0; i < attributesArray.length; i++) {
			int lb = Integer.valueOf(properties.getProperty("attr" + i + "-LB"));
			int ub = Integer.valueOf(properties.getProperty("attr" + i + "-UB"));
			attributes.put(attributesArray[i], VariableFactory.enumerated(attributesArray[i], lb, ub, solver));
			System.out.println(attributesArray[i] + ": " + lb + "-" + ub);
		}
		System.out.println();
	}

	/**
	 * Post If-Then rules for all FMs in the FSG.
	 */
	private void postRules(){
		int fms = Integer.valueOf(properties.getProperty("FM"));

		for(int i = 0; i < fms; i++){
			String inputAttrsString = properties.getProperty("FM" + i + "-IA");
			String[] inputAttrsArray = inputAttrsString.split(",");
			String outputAttrsString = properties.getProperty("FM" + i + "-OA");
			String[] outputAttrsArray = outputAttrsString.split(",");
			int rules = Integer.valueOf(properties.getProperty("FM" + i + "-rules"));

			for(int j = 0; j < rules; j++){
				String ruleString = properties.getProperty("FM" + i + "-rule" + j);
				String[] ruleArray = ruleString.split(";");
				System.out.println("FM" + i + "-rule" + j);
				translateRule(ruleArray, inputAttrsArray, outputAttrsArray);
			}
		}
	}

	/**
	 * Translate one rule into a If-Then constraint in the CP
	 * solver. Input and output attributes are considered.
	 * @param ruleArray - String array with all inequalities among attributes
	 * @param inputAttrsArray - String array with input attributes names
	 * @param outputAttrsArray - String array with output attributes names
	 */
	private void translateRule(String[] ruleArray, String[] inputAttrsArray, String[] outputAttrsArray){
		List<Constraint> ifConstraints = new ArrayList<Constraint>();
		List<Constraint> thenConstraints = new ArrayList<Constraint>();
		Constraint[] ifConstraintsArray;
		Constraint[] thenConstraintsArray;
		
		for(int i = 0; i < ruleArray.length; i++){	
			String[] ruleParts = ruleArray[i].split(",");

			for(int j = 0; j < ruleParts.length; j++){
				String number;
				String operator;
				Pattern numberPattern = Pattern.compile("(\\d+)");
				Pattern operatorPattern = Pattern.compile("<=|>=|>|<|=|-");
				Matcher numberMatcher = numberPattern.matcher(ruleParts[j].trim());
				Matcher operatorMatcher = operatorPattern.matcher(ruleParts[j].trim());

				if(operatorMatcher.find()){
					operator = operatorMatcher.group(0);

					if(numberMatcher.find()){
						number = numberMatcher.group(0);

						if(i < inputAttrsArray.length){
							ifConstraints.add(IntConstraintFactory.arithm(attributes.get(inputAttrsArray[i]), operator, Integer.valueOf(number)));
							System.out.println("IF:" + inputAttrsArray[i] + " " + operator + " " + number);
						}
						else{
							thenConstraints.add(IntConstraintFactory.arithm(attributes.get(outputAttrsArray[i - inputAttrsArray.length]), operator, Integer.valueOf(number)));
							System.out.println("ELSE:" + outputAttrsArray[i - inputAttrsArray.length] + " " + operator + " " + number);
						}
					}
				}
			}
		}
		
		ifConstraintsArray = ifConstraints.toArray(new Constraint[ifConstraints.size()]);
		thenConstraintsArray = thenConstraints.toArray(new Constraint[thenConstraints.size()]);
		
		LogicalConstraintFactory.ifThen(
				LogicalConstraintFactory.and(ifConstraintsArray),
				LogicalConstraintFactory.and(thenConstraintsArray)
		);
		System.out.println();
	}
	
	/**
	 * Post hard constraints related to the configuration selected
	 * values. Arithm constraints with an equality operator are used.
	 */
	private void postConfig(String configurationPath){
		try{
			Properties config = new Properties();
			InputStream stream = new FileInputStream(configurationPath);
			config.load(stream);
			
			int attrs = Integer.valueOf(config.getProperty("attrs"));
			for(int i = 0; i < attrs; i++){
				String configString = config.getProperty("attr" + i);
				String[] configArray = configString.split(",");
				
				solver.post(
						IntConstraintFactory.arithm(attributes.get(configArray[0]), "=", Integer.valueOf(configArray[1]))
				);
			}
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Solves the CSP problem. All solutions are searched.
	 */
	private void solve() {
		Chatterbox.showSolutions(solver);
		solver.findAllSolutions();
		Chatterbox.printStatistics(solver);
	}
}
