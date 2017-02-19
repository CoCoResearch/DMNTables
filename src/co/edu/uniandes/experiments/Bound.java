package co.edu.uniandes.experiments;

public class Bound {
	private String boundType;
	private int value;
	private int rule;
	private int attribute;
	
	public Bound(int attribute, int rule, String boundType, int value) {
		this.attribute = attribute;
		this.rule = rule;
		this.boundType = boundType;
		this.value = value;
	}

	public String getBoundType() {
		return boundType;
	}

	public void setBoundType(String boundType) {
		this.boundType = boundType;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getRule() {
		return rule;
	}

	public void setRule(int rule) {
		this.rule = rule;
	}

	public int getAttribute() {
		return attribute;
	}

	public void setAttribute(int attribute) {
		this.attribute = attribute;
	}
}