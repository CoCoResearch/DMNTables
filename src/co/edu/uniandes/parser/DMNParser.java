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
		parseHeader();
		getAttributes();
		getRules();
		parseAttributes();
		parseRules();
		System.out.println(propertiesText);
	}

	private void parseHeader(){
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

						if(attributes[index].getType().equals("integer") || attributes[index].getType().equals("double")){
							createIntegerRuleExpressions(rule, attributes[index], expression);
						}
						else {
							createStringRuleExpressions(rule, attributes[index], expression);
						}
						index++;
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
			
			if(expressions[i].isEmpty()){
				RuleExpression ruleExpression = new RuleExpression(attribute, ">=", "0");
				rule.addExpression(ruleExpression);
			}
			else{
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
	}

	private void createStringRuleExpressions(Rule rule, Attribute attribute, String expression){
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
				attribute.setType("integer");
				RuleExpression ruleExpression = new RuleExpression(attribute, operator, value);
				rule.addExpression(ruleExpression);
			}
		}
		else{
			value = expression;
			operator = "=";
			if(!value.isEmpty()){
				attribute.addValue(value);
				RuleExpression ruleExpression = new RuleExpression(attribute, operator, value);
				rule.addExpression(ruleExpression);
			}
			else {
				RuleExpression ruleExpression = new RuleExpression(attribute, operator, "-1");
				rule.addExpression(ruleExpression);
			}
		}
	}

	private void parseAttributes(){
		for(int i = 0; i < attributes.length; i++){
			if(attributes[i].getType().equals("string")){
				addTextToProperties("attr[" + (2*i) + "] = " + attributes[i].getLb());
				addTextToProperties("attr[" + (2*i + 1) + "] = " + (attributes[i].getUb() - 1));
			}
			else{
				addTextToProperties("attr[" + (2*i) + "] = " + attributes[i].getLb());
				addTextToProperties("attr[" + (2*i + 1) + "] = " + attributes[i].getUb());
			}
			
		}
	}

	private void parseRules(){
		for(int i = 0; i < rules.length; i++){
			List<RuleExpression> expressions = rules[i].getExpressions();
			RuleExpression pair = null;
			int pairValue = 0;

			for(int j = 0; j <= expressions.size(); j++){
				RuleExpression expression;
				String operator1;
				boolean discardPair = false;
				
				if(j < expressions.size()){
					expression = expressions.get(j);
					operator1 = expression.getOperator();
				}
				
				else{
					expression = null;
					operator1 = null;
				}
			
				if(pair != null){
					Attribute attribute = pair.getAttribute();
					int attrIndex = getAttributeIndex(attribute);
					
					if(pair.getAttribute().getType().equals("string")) {
						if(pair.getIntValue() == -1){
							addTextToProperties("rules[" + i + "][" + (2*attrIndex) + "] = " + pair.getAttribute().getLb());
							addTextToProperties("rules[" + i + "][" + (2*attrIndex + 1) + "] = " + pair.getAttribute().getUb());
						}
						else{
							addTextToProperties("rules[" + i + "][" + (2*attrIndex) + "] = " + (pair.getIntValue() - 1));
							addTextToProperties("rules[" + i + "][" + (2*attrIndex + 1) + "] = " + pair.getIntValue());
						}
					}
					else{
						String operator2 = pair.getOperator();

						if(expression != null && pair.getAttribute().getName().equals(expression.getAttribute().getName())){
							switch (operator1){
							case "<=":
								addTextToProperties("rules[" + i + "][" + (2*attrIndex) + "] = " + pairValue);
								addTextToProperties("rules[" + i + "][" + (2*attrIndex + 1) + "] = " + expression.getIntValue());
								break;
							case "<":
								addTextToProperties("rules[" + i + "][" + (2*attrIndex) + "] = " + pairValue);
								addTextToProperties("rules[" + i + "][" + (2*attrIndex + 1) + "] = " + (expression.getIntValue() - 1));
								break;
							}
							discardPair = true;
						}
						else{
							switch (operator2){
							case "=":
								addTextToProperties("rules[" + i + "][" + (2*attrIndex) + "] = " + pair.getIntValue());
								addTextToProperties("rules[" + i + "][" + (2*attrIndex + 1) + "] = " + pair.getIntValue());
								break;
							case ">=":
								addTextToProperties("rules[" + i + "][" + (2*attrIndex) + "] = " + pair.getIntValue());
								addTextToProperties("rules[" + i + "][" + (2*attrIndex + 1) + "] = " + 100);
								break;
							case "<=":
								addTextToProperties("rules[" + i + "][" + (2*attrIndex) + "] = " + 0);
								addTextToProperties("rules[" + i + "][" + (2*attrIndex + 1) + "] = " + pair.getIntValue());
								break;
							case ">":
								addTextToProperties("rules[" + i + "][" + (2*attrIndex) + "] = " +(pair.getIntValue() + 1));
								addTextToProperties("rules[" + i + "][" + (2*attrIndex + 1) + "] = " + 100);
								break;
							case "<":
								addTextToProperties("rules[" + i + "][" + (2*attrIndex) + "] = " + 0);
								addTextToProperties("rules[" + i + "][" + (2*attrIndex + 1) + "] = " + (pair.getIntValue() + 1));
								break;
							}
						}
					}
				}
				
				if(operator1 != null && !discardPair){
					if(operator1.equals("=") || operator1.equals("<=") || operator1.equals(">=")){
						pairValue = expression.getIntValue();
					}
					else if(operator1.equals("<")){
						pairValue = expression.getIntValue() - 1;
					}
					else{
						pairValue = expression.getIntValue() + 1;
					}

					pair = expression;
				}
				else{
					pair = null;
				}

			}
		}

	}


	private int getAttributeIndex(Attribute attribute){
		int index = -1;
		for(int i = 0; i < attributes.length && index == -1; i++){
			if(attributes[i].getName().equals(attribute.getName())){
				index = i;
			}
		}

		return index;
	}

	private void addTextToProperties(String text){
		propertiesText+=text + '\n';
	}
}
