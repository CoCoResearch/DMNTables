package co.edu.uniandes.parser;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DMNParser {

	private File file;
	private Document document;
	private String propertiesText;
	private Attribute[] attributes;
	private Rule[] rules;
	
	public DMNParser(String filePath){
		
		try {
			file = new File(filePath);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(file);
			propertiesText = "";
		} 
		
		catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void parseToProperties(){
		pardeHeader();
		getAttributes();
		getRules();
		parseAttributes();
		parseRules();
		//System.out.println(propertiesText);
	}
	
	private void pardeHeader(){
		NodeList inputs = document.getElementsByTagName("input");
		NodeList rules = document.getElementsByTagName("rule");
	
		addTextToProperties("N = " + inputs.getLength());
		addTextToProperties("M = " + rules.getLength());
	}
	
	private void getAttributes(){
		NodeList inputs = document.getElementsByTagName("input");
		this.attributes = new Attribute[inputs.getLength()];
		
		for(int i = 0; i < inputs.getLength(); i++){
			Node current = inputs.item(i);
			
			if(current.getNodeType() == Node.ELEMENT_NODE){
				Element currentE = (Element) current;
				Element childE = (Element) currentE.getElementsByTagName("inputExpression").item(0);
				
				String name = currentE.getAttribute("label").replace(" ", "");
				String type = childE.getAttribute("typeRef").replace(" ", "");
				
				Attribute attribute = new Attribute(name, type);
				attributes[i] = attribute;
			}
		}
	}
	
	private void getRules(){
		NodeList rules = document.getElementsByTagName("rule");
		this.rules = new Rule[rules.getLength()];
		
		for(int i = 0; i < rules.getLength(); i++){
			Node current = rules.item(i);
			
			if(current.getNodeType() == Node.ELEMENT_NODE){
				Element currentE = (Element) current;
				NodeList children = currentE.getElementsByTagName("text");
				Rule rule = new Rule();
				int index = 0;
				
				for(int j = 0; j < attributes.length; j++){
					Node child = children.item(j);
					
					if(child.getNodeType() == Node.ELEMENT_NODE){
						Element childE = (Element) child;
						String expression = childE.getTextContent();
						Attribute attribute = attributes[index];
						
						if(attribute.getType().equals("integer")){
							createIntegerRuleExpressions(rule, attribute, expression);
						}
						else {
							createStringRuleExpressions(rule, attribute, expression);
						}
						index++;
						System.out.println(expression);
					}
				}
				
				this.rules[i] = rule;
			}
		}
	}
	
	private void createIntegerRuleExpressions(Rule rule, Attribute attribute, String expression){
		String[] expressions = expression.split(",");
		
		//Caso 1: <, >, <=, >=, -, (, [, ), ]
		for(int i = 0; i < expressions.length; i++){
			String operator;
			String value;
			Pattern valuePattern = Pattern.compile("(\\d+)");
			Pattern operatorPattern = Pattern.compile("<=|>=|>|<|=|-|\\(|\\[|\\]|\\)");
			Matcher valueMatcher = valuePattern.matcher(expressions[i].trim());
			Matcher operatorMatcher = operatorPattern.matcher(expressions[i].trim());
			
			if(operatorMatcher.find()){
				operator = operatorMatcher.group(0);
				
				if(!operator.equals("-") && valueMatcher.find()){
					value = valueMatcher.group(0);
					
					switch(operator){
					case "(":
						operator = ">";
						break;
					case "[":
						operator = ">=";
						break;
					case ")":
						operator = "<";
						break;
					case "]":
						operator = "<=";
						break;
					}
					RuleExpression ruleExpression = new RuleExpression(attribute, operator, value);
					rule.addExpression(ruleExpression);
				}
			}
		}
	}
	
	private void createStringRuleExpressions(Rule rule, Attribute attribute, String expression){
		String[] expressions = expression.split(",");
		
		//Caso 2: cadena, %
		String operator;
		String value;
		Pattern operatorPattern = Pattern.compile("%");
		Pattern valuePattern = Pattern.compile("(\\d+)");
		Matcher operatorMatcher = operatorPattern.matcher(expression.trim());
		Matcher valueMatcher = valuePattern.matcher(expression.trim());
		
		if(operatorMatcher.find()){
			operator = operatorMatcher.group(0);
			
			if(operator.equals("%") && valueMatcher.find()){
				value = valueMatcher.group(0);
				operator = "=";
				RuleExpression ruleExpression = new RuleExpression(attribute, operator, value);
				rule.addExpression(ruleExpression);
			}
		}
		else{
			value = expression;
			operator = "=";
			RuleExpression ruleExpression = new RuleExpression(attribute, operator, value);
			rule.addExpression(ruleExpression);
		}
	}
	
	private void parseAttributes(){
		for(int i = 0; i < attributes.length; i++){
			System.out.println(attributes[i].getName() + " - " + attributes[i].getType());
		}
	}
	
	private void parseRules(){
		for(int i = 0; i < rules.length; i++){
			List<RuleExpression> expressions = rules[i].getExpressions();
			System.out.println("Rule " + i);
			
			for(int j = 0; j < expressions.size(); j++){
				System.out.println(expressions.get(j).getAttribute().getName() + expressions.get(j).getOperator() + 
						expressions.get(j).getValue());
			}
			
		}
	}
	
	private void addTextToProperties(String text){
		propertiesText+=text + '\n';
	}
}
