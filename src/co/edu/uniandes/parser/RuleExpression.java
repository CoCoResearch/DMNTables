package co.edu.uniandes.parser;

public class RuleExpression {
	private Attribute attribute;
	private String operator;
	private String value;
	
	public RuleExpression(Attribute attribute, String operator, String value){
		this.attribute = attribute;
		this.operator = operator;
		setValue(value);
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
		if(!this.attribute.getType().equals("string") && value.length() > 2){
			String shortValue = value.substring(0, 2);
			this.value = shortValue;
		}
		
		else{
			this.value = value;
		}
	}
	
	public int getIntValue(){
		int intValue;
		if(this.attribute.getType().equals("string")){
			intValue = attribute.getValue(value);
		}
		else{
			intValue = Integer.parseInt(value);
		}
		
		return intValue;
	}
}
