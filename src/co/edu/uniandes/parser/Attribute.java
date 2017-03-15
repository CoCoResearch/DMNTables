package co.edu.uniandes.parser;

import java.util.ArrayList;
import java.util.List;

public class Attribute {
	
	private String name;
	private String type;
	private int lb;
	private int ub;
	private List<String> values;
	
	public Attribute(String name, String type){
		this.name = name;
		this.type = type;
		this.values = new ArrayList<String>();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLb() {
		return lb;
	}

	public void setLb(int lb) {
		this.lb = lb;
	}

	public int getUb() {
		return ub;
	}

	public void setUb(int ub) {
		this.ub = ub;
	}
	
	public List<String> getValues(){
		return this.values;
	}
	
	public String getValue(int index){
		return this.values.get(index);
	}
	
	public void addValue(String value){
		this.values.add(value);
	}
}
