package co.edu.uniandes.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Attribute {
	
	private String name;
	private String type;
	private int lb;
	private int ub;
	private HashMap<String, Integer> values;
	private int valuesNum;
	
	public Attribute(String name, String type){
		this.name = name;
		this.type = type;
		this.values = new HashMap<String, Integer>();
		this.valuesNum = 1;
		this.lb = 0;
		
		if(this.type.equals("string")){
			this.ub = 0;
		}
		else{
			this.ub = 100;
		}
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
	
	public HashMap<String, Integer> getValues(){
		return this.values;
	}
	
	public int getValue(String value){
		int answer;
		
		if(values.get(value) == null){
			answer = -1; 
		}
		else{
			answer = values.get(value);
		}
		return answer;
	}
	
	public void addValue(String value){
		boolean exists = values.containsKey(value);
		if(!exists){
			values.put(value, valuesNum);
			ub = valuesNum;
			valuesNum++;
		}
	}
}
