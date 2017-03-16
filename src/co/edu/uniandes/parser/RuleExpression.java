package co.edu.uniandes.parser;

public class RuleExpression {
	private Attribute attribute;
	public String operator;
	public String value;
	
	public RuleExpression(Attribute attribute, String operator, String value){
		this.attribute = attribute;
		this.operator = operator;
		this.value = value;
	}
	
	public Attribute getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute attribute) {
		this.attribute = attribute;
	}
	
	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
