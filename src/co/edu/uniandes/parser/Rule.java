package co.edu.uniandes.parser;

import java.util.ArrayList;
import java.util.List;

public class Rule {
	
	private List<RuleExpression> expressions;
	
	public Rule(){
		this.expressions = new ArrayList<RuleExpression>();
	}

	public List<RuleExpression> getExpressions() {
		return expressions;
	}

	public void setExpressions(List<RuleExpression> expressions) {
		this.expressions = expressions;
	}
	
	public void addExpression(RuleExpression ruleExpression){
		this.expressions.add(ruleExpression);
	}
}
